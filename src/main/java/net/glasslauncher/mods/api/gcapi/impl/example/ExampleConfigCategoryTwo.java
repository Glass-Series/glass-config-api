package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;

public class ExampleConfigCategoryTwo implements IsConfigCategory {

    @ConfigName("Floating Point Value!")
    @Comment("Floats are cool.")
    public Float yayFloatingPoint = 1.0f;

    @ConfigName("Boolean?!")
    public Boolean aBoolean = false;

    @Override
    public String getVisibleName() {
        return "Fancy Config Category";
    }
}
