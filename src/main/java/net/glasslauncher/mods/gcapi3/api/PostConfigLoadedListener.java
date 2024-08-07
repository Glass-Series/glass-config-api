package net.glasslauncher.mods.gcapi3.api;

public interface PostConfigLoadedListener {

    /**
     * Mostly useful for config post-processing on edge cases.
     * @see net.modificationstation.stationapi.impl.config.EventStorage.EventSource
     */
    void PostConfigLoaded(int source);
}
