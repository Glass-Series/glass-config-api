package net.glasslauncher.mods.api.gcapi.screen.widget;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;
import org.lwjgl.opengl.GL11;

public class FancyButton extends Button implements HasDrawable {

    private int disabledColour = -6250336;

    public FancyButton(int id, int x, int y, int width, int height, String text, int overriddenColour) {
        super(id, x, y, width, height, text);
        disabledColour = overriddenColour;
    }

    public FancyButton(int id, int x, int y, int width, int height, String text) {
        super(id, x, y, width, height, text);
    }

    public FancyButton(int id, int x, int y, String text) {
        super(id, x, y, text);
    }

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
    public void draw(int mouseX, int mouseY) {
        render((Minecraft) FabricLoader.getInstance().getGameInstance(), mouseX, mouseY);
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            TextRenderer var4 = minecraft.textRenderer;
            GL11.glBindTexture(3553, minecraft.textureManager.getTextureId("/gui/gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int var6 = this.getYImage(var5);
            this.blit(this.x, this.y, 0, 46 + var6 * 20, this.width / 2, this.height);
            this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + var6 * 20, this.width / 2, this.height);
            this.postRender(minecraft, mouseX, mouseY);
            if (!this.active) {
                this.drawTextWithShadowCentred(var4, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, disabledColour);
            } else if (var5) {
                this.drawTextWithShadowCentred(var4, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, 16777120);
            } else {
                this.drawTextWithShadowCentred(var4, this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, 14737632);
            }

        }
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
