package net.glasslauncher.mods.api.gcapi.api;

import net.glasslauncher.mods.api.gcapi.impl.EventStorage;

public interface PostConfigLoadedListener {

    /**
     * Mostly useful for config post-processing on edge cases.
     * @see net.glasslauncher.mods.api.gcapi.impl.EventStorage.EventSource
     */
    void PostConfigLoaded(int source);
}
