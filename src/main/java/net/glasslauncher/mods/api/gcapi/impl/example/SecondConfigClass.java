package net.glasslauncher.mods.api.gcapi.impl.example;

import net.glasslauncher.mods.api.gcapi.api.ConfigName;

public class SecondConfigClass {

    @ConfigName("Test Boolean")
    public Boolean test1 = false;

    @ConfigName("Test String")
    public String test2 = "Hmmmm";
}
