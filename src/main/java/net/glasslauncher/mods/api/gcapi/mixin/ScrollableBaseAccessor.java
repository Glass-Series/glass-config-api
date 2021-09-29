package net.glasslauncher.mods.api.gcapi.mixin;

import net.minecraft.client.gui.widgets.ScrollableBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScrollableBase.class)
public interface ScrollableBaseAccessor {

    @Accessor
    float getField_1540();

    @Accessor
    void setField_1540(float value);
}
