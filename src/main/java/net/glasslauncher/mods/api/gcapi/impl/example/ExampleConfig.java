package net.glasslauncher.mods.api.gcapi.impl.example;

import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.api.PrimaryGConfig;

public class ExampleConfig {

    @PrimaryGConfig
    @GConfig(value = "config", visibleName = "Config stuff")
    public static final ExampleConfigClass exampleConfigClass = new ExampleConfigClass();

    @GConfig(value = "second", visibleName = "Second Config")
    public static final SecondConfigClass secondConfigClass = new SecondConfigClass();
}
