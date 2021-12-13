package net.glasslauncher.mods.api.gcapi.api;

public interface HasDrawable {

    void draw();
    void mouseClicked(int mouseX, int mouseY, int button);
    void setXYWH(int x, int y, int width, int height);
    void tick();
    void keyPressed(char character, int key);
    void setID(int id);
}
