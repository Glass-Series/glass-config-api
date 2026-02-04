package net.glasslauncher.mods.gcapi3.impl.screen;

import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.stat.Stats;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RestartWantedScreen extends Screen {
    private static final List<String> lines = new ArrayList<>() {{
        add("One or more changed settings require a game restart.");
        add("Would you like to restart now?");
    }};
    private static final int fontColor = CharacterUtils.getIntFromColour(Color.WHITE);
    private final Screen parent;

    public RestartWantedScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int length = textRenderer.getWidth(line);
            drawTextWithShadow(textRenderer, line, (width / 2) - (length / 2), 20 + (14 * i + (i == lines.size() - 1 ? 28 : 0)), fontColor);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.add(new ButtonWidget(0, (width / 2) - 100,  height - 64, I18n.getTranslation("menu.quit")));
        buttons.add(new ButtonWidget(1, (width / 2) - 100, height - 42, "Cancel"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 0) {
            if (minecraft.world != null) {
                minecraft.stats.increment(Stats.LEAVE_GAME, 1);
                if (minecraft.isWorldRemote()) {
                    minecraft.world.disconnect();
                }

                minecraft.setWorld(null);
            }
            minecraft.scheduleStop();
        }
        else if (button.id == 1) {
            // Maybe I'll do this better for GCAPI4.
            //noinspection deprecation
            GCCore.RELOAD_WANTED = false;
            minecraft.setScreen(parent);
        }
    }
}
