package net.glasslauncher.mods.api.gcapi.impl.example;

import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;

/**
 * An example config category, note the use of IsConfigCategory.
 * You must use that interface in order for gcapi to recognise your category.
 */
public class ExampleConfigCategory implements IsConfigCategory {

    // Same deal as before, this time it's inside a category.
    @ConfigName("Oh No!")
    public String ohNo = "reee";

    // And functioning integer config! MUST be the class, not the primitive!
    @ConfigName("Example Integer!")
    public Integer ohYes = 0;

    /**
     * Also the same as ExampleConfigClass, where this shows up as the name of the button to press, as well as the name shown in the header when the screen is opened.
     */
    @Override
    public String getVisibleName() {
        return "§6Oh Noes";
    }
}
