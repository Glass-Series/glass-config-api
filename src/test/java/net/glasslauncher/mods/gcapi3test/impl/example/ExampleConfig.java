package net.glasslauncher.mods.gcapi3test.impl.example;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

/**
 * An example parent config class. You can put @GConfig configs inside classes with other non-config related functionality without issue.
 */
public class ExampleConfig {

    @ConfigRoot(value = "config", visibleName = "Config stuff", index = 0)
    public static final ExampleConfigClass exampleConfigClass = new ExampleConfigClass();

    @ConfigRoot(value = "second", visibleName = "Second Config", index = 1)
    public static final SecondConfigClass secondConfigClass = new SecondConfigClass();

    @ConfigRoot(value = "third", visibleName = "Third Config", index = 2)
    public static final ThirdConfigClass thirdConfigClass = new ThirdConfigClass();

    @ConfigRoot(value = "fourth", visibleName = "Fourth Config", index = 3)
    public static final ThirdConfigClass fourthConfigClass = new ThirdConfigClass();

    @ConfigRoot(value = "fifth", visibleName = "Fifth Config", index = 4)
    public static final ThirdConfigClass fifthConfigClass = new ThirdConfigClass();

    @ConfigRoot(value = "sixth", visibleName = "Sixth Config", index = 5)
    public static final ThirdConfigClass sixthConfigClass = new ThirdConfigClass();
}
