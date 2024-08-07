package net.glasslauncher.mods.gcapi3test.impl.example;

import net.glasslauncher.mods.gcapi3.api.PostConfigLoadedListener;
import net.glasslauncher.mods.gcapi3.api.PreConfigSavedListener;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile;
import org.apache.logging.log4j.Level;

public class ExampleEntryPointListeners implements PreConfigSavedListener, PostConfigLoadedListener {

    @Override
    public void PostConfigLoaded(int source) {
        //noinspection deprecation
        GCCore.log(Level.DEBUG, "Example config load listener is happy.");
    }

    @Override
    public void onPreConfigSaved(int source, GlassYamlFile oldValues, GlassYamlFile newValues) {
        //noinspection deprecation
        GCCore.log(Level.DEBUG, "Example config save listener is also happy");
    }
}
