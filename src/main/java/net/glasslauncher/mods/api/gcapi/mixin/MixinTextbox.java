package net.glasslauncher.mods.api.gcapi.mixin;

import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widgets.Textbox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Textbox.class)
public abstract class MixinTextbox extends DrawableHelper implements HasDrawable {
    @Mutable
    @Shadow @Final private int x;

    @Mutable
    @Shadow @Final private int y;

    @Mutable
    @Shadow @Final private int width;

    @Mutable
    @Shadow @Final private int height;

    @Override
    public void setXYWH(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
