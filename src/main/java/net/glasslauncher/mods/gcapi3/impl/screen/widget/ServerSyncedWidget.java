package net.glasslauncher.mods.gcapi3.impl.screen.widget;

import java.util.Collections;
import java.util.List;

public class ServerSyncedWidget extends IconWidget {
    public ServerSyncedWidget() {
        super(0, 0, 0, 0, "/assets/gcapi3/server_synced.png");
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

    @Override
    public List<String> getTooltip() {
        return Collections.singletonList("This value is synced to the server in multiplayer");
    }
}
