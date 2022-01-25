package net.glasslauncher.mods.api.gcapi.mixin;

import net.glasslauncher.mods.api.gcapi.impl.DrawableHelperAccessor;
import net.minecraft.client.gui.DrawableHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrawableHelper.class)
public abstract class MixinDrawableHelper implements DrawableHelperAccessor {


    @Shadow protected abstract void fill(int x1, int y1, int x2, int y2, int colour);

    @Override
    public void invokeFill(int x1, int y1, int x2, int y2, int colour) {
        fill(x1, y1, x2, y2, colour);
    }
}
