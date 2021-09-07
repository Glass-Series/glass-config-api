package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.GlassConfigAPI;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.ScrollableBase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.registry.ModID;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class ScreenBuilder extends ScreenBase {

    private ScreenScrollList scrollList;
    private final List<ConfigEntry<?>> entryList = new ArrayList<>();
    private int selectedIndex = -1;
    private final ScreenBase parent;
    private final ModID mod;
    private int mouseX = -1;
    private int mouseY = -1;

    public ScreenBuilder(ScreenBase parent, ModID mod) {
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    public void init() {
        buttons.clear();
        entryList.clear();
        this.scrollList = new ScreenScrollList();
        buttons.add(new Button(0,0, 0, 150, 20, TranslationStorage.getInstance().translate("gui.cancel")));
        GlassConfigAPI.MOD_CONFIGS.get(mod).forEach(property -> {
            property.keySet().forEach((key) -> {
                entryList.addAll(property.get(key));
                property.get(key).forEach((value) -> value.init(this, textManager));
            });
        });
    }

    @Override
    public void tick() {
        super.tick();
        for (ConfigBase configBase : entryList) {
            if (configBase instanceof ConfigEntry) {
                ((ConfigEntry<?>) configBase).getDrawable().tick();
            }
        }
    }

    @Override
    protected void keyPressed(char character, int key) {
        super.keyPressed(character, key);
        for (ConfigBase configBase : entryList) {
            if (configBase instanceof ConfigEntry) {
                ((ConfigEntry<?>) configBase).getDrawable().keyPressed(character, key);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        scrollList.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void onMouseEvent() {
        super.onMouseEvent();
        if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
            for (ConfigBase configBase : entryList) {
                if (configBase instanceof ConfigEntry) {
                    ((ConfigEntry<?>) configBase).getDrawable().mouseClicked(mouseX, mouseY);
                }
            }
        }
        else if (Mouse.getDWheel() != 0) {

        }
    }

    @Override
    protected void buttonClicked(Button button) {
        minecraft.openScreen(parent);
    }

    class ScreenScrollList extends ScrollableBase {
        public ScreenScrollList() {
            super(ScreenBuilder.this.minecraft, ScreenBuilder.this.width, ScreenBuilder.this.height, 32, ScreenBuilder.this.height - 32, 48);
        }

        @Override
        protected int getSize() {
            return entryList.size();
        }

        @Override
        protected void entryClicked(int entryIndex, boolean doLoad) {
            ScreenBuilder.this.selectedIndex = entryIndex;
        }

        @Override
        protected boolean isWorldSelected(int i) {
            return i == selectedIndex;
        }

        @Override
        protected void renderBackground() {
            ScreenBuilder.this.renderBackground();
        }

        @Override
        protected void renderStatEntry(int itemId, int x, int y, int i1, Tessellator arg) {
            ConfigBase configBase = ScreenBuilder.this.entryList.get(itemId);

            ScreenBuilder.this.drawTextWithShadow(ScreenBuilder.this.textManager, configBase.name, x + 2, y + 1, 16777215);
            if (configBase instanceof ConfigEntry) {
                ((ConfigEntry) configBase).getDrawable().setXYWH(x + 2, y + 12, 50, 20);
                ((ConfigEntry) configBase).getDrawable().draw();
            }
            ScreenBuilder.this.drawTextWithShadow(ScreenBuilder.this.textManager, configBase.description, x + 2, y + 34, 8421504);
        }
    }
}
