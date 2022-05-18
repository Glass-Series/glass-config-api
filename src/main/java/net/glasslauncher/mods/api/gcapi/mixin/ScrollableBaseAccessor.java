package net.glasslauncher.mods.api.gcapi.mixin;

import net.minecraft.client.gui.widgets.ScrollableBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScrollableBase.class)
public interface ScrollableBaseAccessor {

    @Accessor("scrollAmount")
    float getScrollAmount();

    @Accessor("scrollAmount")
    void setScrollAmount(float value);
}
