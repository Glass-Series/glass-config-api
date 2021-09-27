package net.glasslauncher.mods.api.gcapi.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.screen.HasDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widgets.Button;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Button.class)
public abstract class MixinButton implements HasDrawable {

    @Shadow public int x;

    @Shadow public int y;

    @Shadow protected int height;

    @Shadow protected int width;

    @Shadow public abstract void render(Minecraft minecraft, int mouseX, int mouseY);

    @Shadow public abstract void mouseReleased(int mouseX, int mouseY);

    @Shadow public int id;

    @Override
    public void setXYWH(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void tick() {

    }

    @Override
    public void keyPressed(char character, int key) {

    }

    @Override
    public void draw() {
        render((Minecraft) FabricLoader.getInstance().getGameInstance(), Mouse.getX(), Mouse.getY());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            mouseReleased(mouseX, mouseY);
        }
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }
}
