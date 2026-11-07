package net.glasslauncher.mods.gcapi3.impl.screen;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.minecraft.client.gui.screen.Screen;
import net.glasslauncher.mods.gcapi3.impl.EventStorage;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigCategoryHandler;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfirmChangesScreen extends Screen {
    private static final List<String> lines = new ArrayList<>() {
        {
            add("You have one or more unsaved changes.");
            add("Would you like to save them?");
        }
    };
    private static final int fontColor = CharacterUtils.getIntFromColour(Color.WHITE);
    private final Screen parent;
    private final ModContainer mod;
    private final ConfigCategoryHandler baseCategory;

    public ConfirmChangesScreen(Screen parent, ModContainer mod, ConfigCategoryHandler baseCategory) {
        this.parent = parent;
        this.mod = mod;
        this.baseCategory = baseCategory;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int length = textRenderer.getWidth(line);
            drawTextWithShadow(textRenderer, line, (width / 2) - (length / 2),
                    20 + (14 * i + (i == lines.size() - 1 ? 28 : 0)), fontColor);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.add(new ButtonWidget(0, (width / 2) - 100, height - 64, "Save Changes"));
        buttons.add(new ButtonWidget(1, (width / 2) - 100, height - 42, "Cancel"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            if (parent instanceof ScreenBuilder builder) {
                builder.saveToEntries();
            }
            // noinspection deprecation Intentional use of GCCore internals.
            GCCore.saveConfig(mod, baseCategory, EventStorage.EventSource.USER_SAVE);
            // noinspection deprecation
            if (GCCore.RELOAD_WANTED) {
                MinecraftAccessor.getInstance().setScreen(new RestartWantedScreen(this));
            } else {
                // Close the menu.
                if (parent instanceof ScreenBuilder builder) {
                    this.minecraft.setScreen(builder.parent);
                }
            }
        } else if (button.id == 1) {
            // Close the menu.
            if (parent instanceof ScreenBuilder builder) {
                this.minecraft.setScreen(builder.parent);
            }
        }
    }
}
