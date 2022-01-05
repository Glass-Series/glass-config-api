package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;

/**
 * An example config category, note the use of IsConfigCategory.
 * You must use that interface in order for gcapi to recognise your category.
 */
public class ExampleConfigCategory {

    // Same deal as before, this time it's inside a category.
    @ConfigName("Oh No!")
    public String ohNo = "reee";

    // And functioning integer config! MUST be the class, not the primitive!
    @ConfigName("Example Integer!")
    public Integer ohYes = 0;

    @Comment("Fancy values ahead!")
    @ConfigCategory("Fancy Config Category")
    public ExampleConfigCategoryTwo secondCategory = new ExampleConfigCategoryTwo();
}
