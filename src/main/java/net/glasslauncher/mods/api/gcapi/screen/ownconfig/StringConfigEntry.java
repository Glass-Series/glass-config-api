package net.glasslauncher.mods.api.gcapi.screen.ownconfig;

import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.HasDrawable;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Textbox;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

public class StringConfigEntry extends ConfigEntry<String> {
    private Textbox textbox;

    public StringConfigEntry(String name, String description, String value) {
        super(name, description, value);
        System.out.println(value);
    }

    public void init(ScreenBase parent, TextRenderer textRenderer) {
        textbox = new Textbox(parent, textRenderer, 0, 0, 0, 0, value);
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        return (HasDrawable) textbox;
    }
}
