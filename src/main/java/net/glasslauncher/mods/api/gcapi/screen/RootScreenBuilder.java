package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.impl.GlassConfigAPI;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;

public class RootScreenBuilder extends ScreenBuilder {

    boolean doSave = true;

    public RootScreenBuilder(ScreenBase parent, ModContainerEntrypoint mod, ConfigCategory baseCategory) {
        super(parent, mod, baseCategory);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (doSave) {
            GlassConfigAPI.saveConfigs(mod);
            GlassConfigAPI.log("Saved!");
        }
    }

    @Override
    public void init() {
        doSave = true;
        super.init();
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id != 0) {
            doSave = false;
        }
        super.buttonClicked(button);
    }
}
