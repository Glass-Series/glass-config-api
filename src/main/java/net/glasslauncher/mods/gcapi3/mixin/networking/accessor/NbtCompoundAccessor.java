package net.glasslauncher.mods.gcapi3.mixin.networking.accessor;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataInput;

@Mixin(NbtCompound.class)
public interface NbtCompoundAccessor {
    @Invoker("read")
    void invokeRead(DataInput input);
}
