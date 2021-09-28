package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;
import net.glasslauncher.mods.api.gcapi.api.MultiplayerSynced;

public class ConfigClass implements HasConfigFields {

    @ConfigName("Tested Config")
    @Comment("Used to translate nerd")
    public static String testedConfig = "nerd";
    @ConfigName("Tested Config 1")
    @MultiplayerSynced
    public static String testConfig1 = "wow";
    @ConfigName("ASD 2")
    public static String asd2 = "hmmm";
    @ConfigName("ASD 3")
    public static String asd3 = "hmmm";

    @Comment("My config category")
    public static ExampleConfigCategory configCategory = new ExampleConfigCategory();

    @Override
    public String getVisibleName() {
        return "My Config";
    }
}
