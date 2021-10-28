package net.glasslauncher.mods.api.gcapi.api;

public interface PostConfigLoadedListener {

    /**
     * Not sure why someone would need this, because of config factories existing, but this would be useful for config post-processing.
     */
    void PostConfigLoaded();
}
