package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.mixin.ScrollableBaseAccessor;
import net.glasslauncher.mods.api.gcapi.screen.widget.ExtensibleTextbox;
import net.glasslauncher.mods.api.gcapi.screen.widget.TexturedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ParticleRenderer;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.ScrollableBase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public abstract class BaseListScreenBuilder<T> extends ScreenBase {

    protected ScreenScrollList scrollList;
    protected final ScreenBase parent;
    protected int mouseX = -1;
    protected int mouseY = -1;
    protected ConfigEntry<T[]> configEntry;
    public final List<ExtensibleTextbox> textboxes = new ArrayList<>();
    protected Function<String, Boolean> validator;
    protected final int maxLength;
    private boolean isInUse = false;

    protected BaseListScreenBuilder(ScreenBase parent, int maxLength, ConfigEntry<T[]> configEntry, Function<String, Boolean> validator) {
        this.parent = parent;
        this.maxLength = maxLength;
        this.configEntry = configEntry;
        this.validator = validator;
    }

    public void setValues(List<T> list) {
        textboxes.clear();
        list.forEach((value) -> {
            ExtensibleTextbox textbox = new ExtensibleTextbox(textManager, validator);
            textbox.setMaxLength(maxLength);
            textbox.setText(String.valueOf(value));
            textboxes.add(textbox);
        });
    }

    public void setValues(T[] list) {
        setValues(Arrays.asList(list));
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        this.smokeRenderer = new ParticleRenderer(minecraft);
        this.minecraft = minecraft;
        this.textManager = minecraft.textRenderer;
        this.width = width;
        this.height = height;
        init();
    }

    @Override
    public void init() {
        if (isInUse) {
            scrollList = new ScreenScrollList();
            Button button = ((Button)buttons.get(0));
            button.x = width/2-75;
            button.y = height-26;
            button = ((Button)buttons.get(1));
            button.x = ((width/3)*2)-75;
            button.y = height-48;
            return;
        }
        setValues(configEntry.value);
        buttons.clear();
        this.scrollList = new ScreenScrollList();
        //noinspection unchecked
        buttons.add(new Button(0,width/2-75, height-26, 150, 20, TranslationStorage.getInstance().translate("gui.cancel")));
        //noinspection unchecked
        buttons.add(new TexturedButton(1,((width/3)*2)-75, height-48, 20, 20, 0, 0, "assets/gcapi/add_button.png", 32, 64, "Add a new entry at the end"));
        AtomicInteger id = new AtomicInteger(1);
        textboxes.forEach((te) -> {
            //noinspection unchecked
            buttons.add(new TexturedButton(id.incrementAndGet(),0, 0, 20, 20, 0, 0, "assets/gcapi/remove_button.png", 32, 64, "Remove the entry on this line"));
        });
        isInUse = true;
    }

    @Override
    public void tick() {
        super.tick();
        for (ExtensibleTextbox configBase : textboxes) {
            configBase.tick();
        }
    }

    @Override
    protected void keyPressed(char character, int key) {
        super.keyPressed(character, key);
        for (ExtensibleTextbox configBase : textboxes) {
            configBase.keyPressed(character, key);
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
        ((Button) buttons.get(1)).render(minecraft, mouseX, mouseY);
        textManager.drawTextWithShadow(configEntry.name, (width / 2) - (textManager.getTextWidth(configEntry.name) / 2), 4, 16777215);
        textManager.drawTextWithShadow(configEntry.description, (width / 2) - (textManager.getTextWidth(configEntry.description) / 2), 18, 8421504);
        List<String> tooltip = ((ScreenBaseAccessor) this).getMouseTooltip(mouseX, mouseY, textboxes);
        if (tooltip != null) {
            CharacterUtils.renderTooltip(textManager, tooltip, mouseX, mouseY, this);
        }
    }

    @Override
    public void onMouseEvent() {
        super.onMouseEvent();
        float dWheel = Mouse.getDWheel();
        if (Mouse.isButtonDown(0)) {
            for (ExtensibleTextbox configBase : textboxes) {
                    configBase.mouseClicked(mouseX, mouseY, 0);
            }
        }
        else if (dWheel != 0) {
            scrollList.scroll(-(dWheel/10));
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 0) {
            isInUse = false;
            minecraft.openScreen(parent);
        }
        else if (button.id == 1) {
            ExtensibleTextbox textbox = new ExtensibleTextbox(textManager, validator);
            textbox.setText("");
            textboxes.add(textbox);
            //noinspection unchecked
            buttons.add(new TexturedButton(buttons.size(),0, 0, 20, 20, 0, 0, "assets/gcapi/remove_button.png", 32, 64, "Remove the entry on this line"));
        }
        else if (button.id > 1) {
            textboxes.remove(button.id-2);
            buttons.remove(button.id);
            for (int i = 1; i<buttons.size(); i++) {
                ((Button) buttons.get(i)).id = i;
            }
        }
    }

    abstract T convertStringToValue(String value);

    @Override
    public void onClose() {
        if (isInUse) {
            return;
        }
        List<T> list = new ArrayList<>();
        textboxes.forEach((value) -> {
            if (value.isValueValid()) {
                list.add(convertStringToValue(value.getText()));
            }
        });
        //noinspection unchecked
        configEntry.value = (T[]) list.toArray();
        super.onClose();
        if (parent instanceof RootScreenBuilder) {
            ((RootScreenBuilder) parent).doSave = false;
        }
    }

    class ScreenScrollList extends ScrollableBase {
        public ScreenScrollList() {
            super(BaseListScreenBuilder.this.minecraft, BaseListScreenBuilder.this.width, BaseListScreenBuilder.this.height, 32, BaseListScreenBuilder.this.height - 64, 24);

        }

        public void scroll(float value) {
            ScrollableBaseAccessor baseAccessor = ((ScrollableBaseAccessor) this);
            baseAccessor.gcapi$setField_1540(baseAccessor.gcapi$getField_1540() + value);
        }

        @Override
        protected int getSize() {
            return textboxes.size();
        }

        @Override
        protected void entryClicked(int entryIndex, boolean doLoad) {
        }

        @Override
        protected boolean isEntrySelected(int i) {
            return false;
        }

        @Override
        protected void renderBackground() {
            BaseListScreenBuilder.this.renderBackground();
        }

        @Override
        protected void renderEntry(int itemId, int x, int y, int i1, Tessellator arg) {
            if (itemId+2 >= buttons.size()) {
                return;
            }
            ExtensibleTextbox configBase = textboxes.get(itemId);
            BaseListScreenBuilder.this.drawTextWithShadow(textManager, String.valueOf(itemId), x - 2 - textManager.getTextWidth(String.valueOf(itemId)), y + 1, 16777215);
            ((TexturedButton) buttons.get(itemId+2)).setPos(x + 214 + 34, y+2);
            ((TexturedButton) buttons.get(itemId+2)).render(minecraft, mouseX, mouseY);
            configBase.setXYWH(x + 2, y + 1, 212, 20);
            configBase.draw(mouseX, mouseY);
        }
    }
}
