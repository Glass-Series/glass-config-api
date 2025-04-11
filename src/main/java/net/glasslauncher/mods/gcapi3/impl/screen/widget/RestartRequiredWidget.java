package net.glasslauncher.mods.gcapi3.impl.screen.widget;

import java.util.Collections;
import java.util.List;

public class RestartRequiredWidget extends IconWidget {
    public RestartRequiredWidget() {
        super(0, 0, 0, 0, "/assets/gcapi3/requires_restart.png");
    }

    @Override
    public List<String> getTooltip() {
        return Collections.singletonList("This requires a game restart on change");
    }

    @Override
    public void setXYWH(int x, int y, int width, int height) {
        super.setXYWH(x + 10, y, width, height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

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
