package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.impl.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.glasslauncher.mods.api.gcapi.mixin.ScrollableBaseAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.ScrollableBase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Mouse;

import java.util.HashMap;

public class ScreenBuilder extends ScreenBase {

    protected ScreenScrollList scrollList;
    protected HashMap<Integer, ConfigCategory> buttonToCategory;
    protected final ConfigCategory baseCategory;
    protected int selectedIndex = -1;
    protected final ScreenBase parent;
    protected final ModContainerEntrypoint mod;
    protected int mouseX = -1;
    protected int mouseY = -1;

    public ScreenBuilder(ScreenBase parent, ModContainerEntrypoint mod, ConfigCategory baseCategory) {
        this.parent = parent;
        this.mod = mod;
        this.baseCategory = baseCategory;
    }

    @Override
    public void init() {
        baseCategory.values.values().forEach((value) -> {
            if (value instanceof ConfigEntry<?>) {
                ConfigEntry configEntry = (ConfigEntry<?>) value;
                if (configEntry.getDrawableValue() != null) {
                    configEntry.value = configEntry.getDrawableValue();
                }
            }
        });
        buttons.clear();
        this.scrollList = new ScreenScrollList();
        this.buttonToCategory = new HashMap<>();
        buttons.add(new Button(0,width/2-75, height-26, 150, 20, TranslationStorage.getInstance().translate("gui.cancel")));
        baseCategory.values.values().forEach((value) -> {
            if (value instanceof ConfigEntry) {
                ((ConfigEntry<?>) value).init(this, textManager);
            }
            else if (value.getDrawable() instanceof Button) {
                value.getDrawable().setID(buttons.size());
                buttonToCategory.put(buttons.size(), (ConfigCategory) value);
                buttons.add(value.getDrawable());
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigBase configBase : baseCategory.values.values()) {
            if (configBase instanceof ConfigEntry) {
                configBase.getDrawable().tick();
            }
        }
    }

    @Override
    protected void keyPressed(char character, int key) {
        super.keyPressed(character, key);
        for (ConfigBase configBase : baseCategory.values.values()) {
            if (configBase instanceof ConfigEntry) {
                configBase.getDrawable().keyPressed(character, key);
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
    }

    @Override
    public void onMouseEvent() {
        super.onMouseEvent();
        float dWheel = Mouse.getDWheel();
        if (Mouse.isButtonDown(0)) {
            for (ConfigBase configBase : baseCategory.values.values()) {
                if (configBase instanceof ConfigEntry) {
                    configBase.getDrawable().mouseClicked(mouseX, mouseY, 0);
                }
            }
        }
        else if (dWheel != 0) {
            scrollList.scroll(-(dWheel/10));
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 0) {
            minecraft.openScreen(parent);
        }
        else {
            ConfigCategory configCategory = buttonToCategory.get(button.id);
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(configCategory.getConfigScreen(this, mod));
        }
    }

    @Override
    public void onClose() {
        baseCategory.values.values().forEach((value) -> {
            if (value instanceof ConfigEntry<?>) {
                ConfigEntry configEntry = (ConfigEntry<?>) value;
                configEntry.value = configEntry.getDrawableValue();
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
            return baseCategory.values.values().size();
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
            ConfigBase configBase = (ConfigBase) ScreenBuilder.this.baseCategory.values.values().toArray()[itemId];
            ScreenBuilder.this.drawTextWithShadow(ScreenBuilder.this.textManager, configBase.name, x + 2, y + 1, 16777215);
            configBase.getDrawable().setXYWH(x + 2, y + 12, 212, 20);
            configBase.getDrawable().draw();
            ScreenBuilder.this.drawTextWithShadow(ScreenBuilder.this.textManager, configBase.description, x + 2, y + 34, 8421504);
        }
    }
}
