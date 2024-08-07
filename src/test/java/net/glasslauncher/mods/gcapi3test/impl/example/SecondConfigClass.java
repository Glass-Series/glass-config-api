package net.glasslauncher.mods.gcapi3test.impl.example;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class SecondConfigClass {

    @ConfigEntry(name = "Test Boolean")
    public Boolean test1 = false;

    @ConfigEntry(name = "Test String")
    public String test2 = "Hmmmm";
}
