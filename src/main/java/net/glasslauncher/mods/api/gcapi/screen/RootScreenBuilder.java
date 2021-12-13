package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.impl.GlassConfigAPI;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;

public class RootScreenBuilder extends ScreenBuilder {

    boolean doSave = true;

    public RootScreenBuilder(ScreenBase parent, EntrypointContainer<Object> mod, ConfigCategory baseCategory) {
        super(parent, mod, baseCategory);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (doSave) {
            GlassConfigAPI.saveConfig(mod, baseCategory);
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
