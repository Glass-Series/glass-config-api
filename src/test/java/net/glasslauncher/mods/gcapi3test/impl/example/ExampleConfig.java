package net.glasslauncher.mods.gcapi3test.impl.example;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

/**
 * An example parent config class. You can put @GConfig configs inside classes with other non-config related functionality without issue.
 */
public class ExampleConfig {

    @ConfigRoot(value = "config", visibleName = "Config stuff", index = 0)
    public static final ExampleConfigClass exampleConfigClass = new ExampleConfigClass();

    @ConfigRoot(value = "second", visibleName = "Second Config")
    public static final SecondConfigClass secondConfigClass = new SecondConfigClass();

    @ConfigRoot(value = "third", visibleName = "Third Config")
    public static final ThirdConfigClass thirdConfigClass = new ThirdConfigClass();
}
