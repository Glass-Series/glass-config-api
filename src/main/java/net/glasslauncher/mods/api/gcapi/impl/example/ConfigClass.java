package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;

public class ConfigClass implements HasConfigFields {

    @Comment("Used to translate nerd")
    public static String testedConfig = "nerd";
    public static String testConfig1 = "wow";
    public static String asd2 = "hmmm";
    public static String asd3 = "hmmm";

    @Comment("My config category")
    public static ExampleConfigCategory configCategory = new ExampleConfigCategory();

    @Override
    public String getVisibleName() {
        return "My Config";
    }
}
