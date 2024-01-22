package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.JsonObject;
import net.glasslauncher.mods.api.gcapi.api.PostConfigLoadedListener;
import net.glasslauncher.mods.api.gcapi.api.PreConfigSavedListener;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;

public class ExampleEntryPointListeners implements PreConfigSavedListener, PostConfigLoadedListener {
    @Override
    public void PostConfigLoaded() {
        //noinspection deprecation
        GCCore.log("Example config load listener is happy.");
    }

    @Override
    public void onPreConfigSaved(JsonObject oldValues, JsonObject newValues) {
        //noinspection deprecation
        GCCore.log("Example config save listener is also happy");
    }
}
