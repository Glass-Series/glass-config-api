package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.GlassConfigAPI;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.minecraft.client.gui.screen.ScreenBase;

public class RootScreenBuilder extends ScreenBuilder {

    public RootScreenBuilder(ScreenBase parent, ModContainerEntrypoint mod, ConfigCategory baseCategory) {
        super(parent, mod, baseCategory);
    }

    @Override
    public void onClose() {
        super.onClose();
        GlassConfigAPI.saveConfigs(mod);
        System.out.println("Saved!");
    }
}
