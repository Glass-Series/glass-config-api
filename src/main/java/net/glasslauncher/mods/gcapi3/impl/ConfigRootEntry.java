package net.glasslauncher.mods.gcapi3.impl;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigCategoryHandler;

public record ConfigRootEntry(
        ModContainer modContainer,
        ConfigRoot configRoot,
        Object configObject,
        ConfigCategoryHandler configCategoryHandler
) {}
