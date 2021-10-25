package net.glasslauncher.mods.api.gcapi.screen;

import java.util.List;

public interface ScreenBaseAccessor {

    List<String> getMouseTooltip(int mouseX, int mouseY, List<?> extraObjectsToCheck);
}
