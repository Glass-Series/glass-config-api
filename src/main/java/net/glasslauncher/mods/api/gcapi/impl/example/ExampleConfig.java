package net.glasslauncher.mods.api.gcapi.impl.example;

import net.glasslauncher.mods.api.gcapi.api.GConfig;

public class ExampleConfig {

    @GConfig(visibleName = "Config stuff")
    public static final ExampleConfigClass exampleConfigClass = new ExampleConfigClass();
}
