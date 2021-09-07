package net.glasslauncher.mods.api.gcapi.mixin;

import net.modificationstation.stationapi.impl.config.PropertyImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PropertyImpl.class)
public interface StAPIPropertyAccessor {

    @Accessor
    String getComment();
}
