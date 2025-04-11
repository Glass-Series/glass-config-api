package net.glasslauncher.mods.gcapi3.impl.screen;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.List;

public interface ScreenAccessor {

    List<String> glass_config_api$getMouseTooltip(int mouseX, int mouseY, List<?> extraObjectsToCheck);

    void glass_config_api$setSelectedButton(ButtonWidget value);
}
