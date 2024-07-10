package net.glasslauncher.mods.gcapi.impl.screen;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.*;

public interface ScreenAccessor {

    List<String> glass_config_api$getMouseTooltip(int mouseX, int mouseY, List<?> extraObjectsToCheck);

    void glass_config_api$setSelectedButton(ButtonWidget value);
}
