package net.glasslauncher.mods.gcapi3.impl.screen.widget;

import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResetConfigWidget extends IconWidget {
    private final ConfigEntryHandler<?> configEntry;

    public ResetConfigWidget(ConfigEntryHandler<?> configEntry) {
        super(0, 0, 0, 0, "/assets/gcapi3/reset.png");
        this.configEntry = configEntry;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            try {
                if(!configEntry.multiplayerLoaded) {
                    configEntry.reset(configEntry.defaultValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setXYWH(int x, int y, int width, int height) {
        super.setXYWH(x, y - 10, width, height);
    }

    @Override
    public List<String> getTooltip() {
        if(configEntry.multiplayerLoaded) {
            return new ArrayList<>();
        }
        return Collections.singletonList("Reset this config to default.");
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if(!configEntry.multiplayerLoaded) {
            super.draw(mouseX, mouseY);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void keyPressed(char character, int key) {

    }

    @Override
    public void setID(int id) {

    }
}
