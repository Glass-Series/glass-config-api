package net.glasslauncher.mods.api.gcapi.impl;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;

public class ModContainerEntrypoint {

    public final ModContainer mod;
    public final HasConfigFields entrypoint;

    public ModContainerEntrypoint(ModContainer mod, HasConfigFields entrypoint) {
        this.mod = mod;
        this.entrypoint = entrypoint;
    }
}
