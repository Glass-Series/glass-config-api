package net.glasslauncher.mods.gcapi3test.impl.example;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class ExampleConfigCategoryTwo {

    @ConfigEntry(name = "Floating Point Value!", description = "Floats are cool.")
    public Float yayFloatingPoint = 1.0f;

    @ConfigEntry(name = "Boolean?!")
    public Boolean aBoolean = false;

    @ConfigEntry(name = "A LIST??!!")
    public String[] aList = new String[0];

    @ConfigEntry(name = "AN INTEGER LIST??!!")
    public Integer[] aIList = new Integer[0];

    @ConfigEntry(name = "A FLOAT LIST??!!")
    public Float[] aFList = new Float[0];

    @ConfigEntry(name = "A FIXED LIST?!", maxValue = 10, maxArrayLength = 3, minArrayLength = 3)
    public Integer[] aFIList = new Integer[] { 1, 3, 4 };
}
