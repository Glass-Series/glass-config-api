package net.glasslauncher.mods.api.gcapi.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.api.PostConfigLoadedListener;
import net.glasslauncher.mods.api.gcapi.api.PreConfigSavedListener;

import java.util.*;

public class EventStorage {
    public static final Map<String, EntrypointContainer<PreConfigSavedListener>> PRE_SAVE_LISTENERS = new HashMap<>();
    public static final Map<String, EntrypointContainer<PostConfigLoadedListener>> POST_LOAD_LISTENERS = new HashMap<>();

    public static void loadListeners() {
        FabricLoader.getInstance().getEntrypointContainers("gcapi:presave", PreConfigSavedListener.class).forEach(preConfigSavedListenerEntrypointContainer -> PRE_SAVE_LISTENERS.put(preConfigSavedListenerEntrypointContainer.getProvider().getMetadata().getId(), preConfigSavedListenerEntrypointContainer));
        FabricLoader.getInstance().getEntrypointContainers("gcapi:postload", PostConfigLoadedListener.class).forEach(postConfigLoadedListenerEntrypointContainer -> POST_LOAD_LISTENERS.put(postConfigLoadedListenerEntrypointContainer.getProvider().getMetadata().getId(), postConfigLoadedListenerEntrypointContainer));
    }
}
