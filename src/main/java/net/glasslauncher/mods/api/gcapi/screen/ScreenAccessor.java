package net.glasslauncher.mods.api.gcapi.screen;

import java.util.*;

public interface ScreenAccessor {

    List<String> getMouseTooltip(int mouseX, int mouseY, List<?> extraObjectsToCheck);
}
