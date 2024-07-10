package net.glasslauncher.mods.gcapi.impl;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.gcapi.api.ConfigRoot;
import net.glasslauncher.mods.gcapi.impl.object.ConfigCategoryHandler;

public record ConfigRootEntry(
        ModContainer modContainer,
        ConfigRoot configRoot,
        Object configObject,
        ConfigCategoryHandler configCategoryHandler
) {}
