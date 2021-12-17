package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigBase;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.mixin.ScrollableBaseAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.ScrollableBase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScreenBuilder extends ScreenBase {

    protected ScreenScrollList scrollList;
    protected HashMap<Integer, ConfigBase> buttonToEntry;
    protected final ConfigCategory baseCategory;
    protected int selectedIndex = -1;
    protected final ScreenBase parent;
    protected final EntrypointContainer<Object> mod;
    protected int mouseX = -1;
    protected int mouseY = -1;
    protected List<ConfigBase> configBases = new ArrayList<>();

    public ScreenBuilder(ScreenBase parent, EntrypointContainer<Object> mod, ConfigCategory baseCategory) {
        this.parent = parent;
        this.mod = mod;
        this.baseCategory = baseCategory;
        configBases.addAll(baseCategory.values.values());
        configBases.sort((self, other) -> {
            if (other instanceof ConfigCategory) {
                return 1;
            }
            return self instanceof ConfigCategory? -1 : self.name.compareTo(other.name);
        });
    }

    @Override
    public void init() {
        baseCategory.values.values().forEach((value) -> {
            if (value instanceof ConfigEntry<?>) {
                //noinspection rawtypes
                ConfigEntry configEntry = (ConfigEntry<?>) value;
                if (configEntry.getDrawableValue() != null) {
                    configEntry.value = configEntry.getDrawableValue();
                }
            }
        });
        buttons.clear();
        this.scrollList = new ScreenScrollList();
        this.buttonToEntry = new HashMap<>();
        //noinspection unchecked
        buttons.add(new Button(0,width/2-75, height-26, 150, 20, TranslationStorage.getInstance().translate("gui.cancel")));
        baseCategory.values.values().forEach((value) -> {
            if (value instanceof ConfigEntry) {
                ((ConfigEntry<?>) value).init(this, textManager);
            }
            value.getDrawables().forEach(val -> {
                if (val instanceof Button) {
                    val.setID(buttons.size());
                    buttonToEntry.put(buttons.size(), value);
                    buttons.add(val);
                }
            });
        });
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigBase configBase : baseCategory.values.values()) {
            if (configBase instanceof ConfigEntry) {
                configBase.getDrawables().forEach(HasDrawable::tick);
            }
        }
    }

    @Override
    protected void keyPressed(char character, int key) {
        super.keyPressed(character, key);
        for (ConfigBase configBase : baseCategory.values.values()) {
            if (configBase instanceof ConfigEntry) {
                configBase.getDrawables().forEach(val -> val.keyPressed(character, key));
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        scrollList.render(mouseX, mouseY, delta);
        // Breaks rendering of category buttons.
        //super.render(mouseX, mouseY, delta);
        ((Button) buttons.get(0)).render(minecraft, mouseX, mouseY);
        textManager.drawTextWithShadow(baseCategory.name, (width/2) - (textManager.getTextWidth(baseCategory.name)/2), 4, 16777215);
        textManager.drawTextWithShadow(baseCategory.description, (width/2) - (textManager.getTextWidth(baseCategory.description)/2), 18, 8421504);
        ArrayList<HasDrawable> drawables = new ArrayList<>();
        configBases.forEach((configBase -> drawables.addAll(configBase.getDrawables())));
        List<String> tooltip = ((ScreenBaseAccessor) this).getMouseTooltip(mouseX, mouseY, drawables);
        if (tooltip != null) {
            CharacterUtils.renderTooltip(textManager, tooltip, mouseX, mouseY, this);
        }
    }

    @Override
    public void onMouseEvent() {
        super.onMouseEvent();
        float dWheel = Mouse.getDWheel();
        if (Mouse.isButtonDown(0)) {
            for (ConfigBase configBase : baseCategory.values.values()) {
                if (configBase instanceof ConfigEntry) {
                    configBase.getDrawables().forEach(val -> val.mouseClicked(mouseX, mouseY, 0));
                }
            }
        }
        else if (dWheel != 0) {
            scrollList.scroll(-(dWheel/10));
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        System.out.println("Clicked " + button.id);
        if (button.id == 0) {
            minecraft.openScreen(parent);
        }
        else if (buttonToEntry.get(button.id) instanceof ConfigEntryWithButton) {
            ((ConfigEntryWithButton) buttonToEntry.get(button.id)).onClick();
        }
        else if (buttonToEntry.get(button.id) instanceof ConfigCategory) {
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(((ConfigCategory) buttonToEntry.get(button.id)).getConfigScreen(this, mod));
        }
    }

    @Override
    public void onClose() {
        baseCategory.values.values().forEach((value) -> {
            if (value instanceof ConfigEntry<?>) {
                //noinspection rawtypes
                ConfigEntry configEntry = (ConfigEntry<?>) value;
                if (configEntry.isValueValid()) {
                    configEntry.value = configEntry.getDrawableValue();
                }
                else {
                    //noinspection unchecked
                    configEntry.setDrawableValue(configEntry.value);
                }
            }
        });
        super.onClose();
        if (parent instanceof RootScreenBuilder) {
            ((RootScreenBuilder) parent).doSave = false;
        }
    }

    class ScreenScrollList extends ScrollableBase {
        public ScreenScrollList() {
            super(ScreenBuilder.this.minecraft, ScreenBuilder.this.width, ScreenBuilder.this.height, 32, ScreenBuilder.this.height - 32, 48);
        }

        public void scroll(float value) {
            ScrollableBaseAccessor baseAccessor = ((ScrollableBaseAccessor) this);
            baseAccessor.setField_1540(baseAccessor.getField_1540() + value);
        }

        @Override
        protected int getSize() {
            return configBases.size();
        }

        @Override
        protected void entryClicked(int entryIndex, boolean doLoad) {
            ScreenBuilder.this.selectedIndex = entryIndex;
        }

        @Override
        protected boolean isEntrySelected(int i) {
            return i == selectedIndex;
        }

        @Override
        protected void renderBackground() {
            ScreenBuilder.this.renderBackground();
        }

        @Override
        protected void renderEntry(int itemId, int x, int y, int i1, Tessellator arg) {
            ConfigBase configBase = configBases.get(itemId);
            ScreenBuilder.this.drawTextWithShadow(ScreenBuilder.this.textManager, configBase.name, x + 2, y + 1, 16777215);
            configBase.getDrawables().forEach(val -> val.setXYWH(x + 2, y + 12, 212, 20));
            configBase.getDrawables().forEach(val -> val.draw(mouseX, mouseY));
            ScreenBuilder.this.drawTextWithShadow(ScreenBuilder.this.textManager, configBase.description, x + 2, y + 34, 8421504);
        }
    }
}
