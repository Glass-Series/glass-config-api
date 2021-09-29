package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;
import net.glasslauncher.mods.api.gcapi.api.MultiplayerSynced;

/**
 * An example config class, you can view this in-game inside modmenu's settings button for gcapi.
 * Make sure to add it inside your gcapi entrypoints in your fabric.mod.json. Look at the one for this mod for an example.
 */
public class ExampleConfigClass implements HasConfigFields {

    @ConfigName("Tested Config") // Shows up above the config entry in white, unless you use colour codes, then it will use those.
    @Comment("Used to translate nerd") // Shows up in grey under the config entry.
    public static String testedConfig = "nerd";

    @ConfigName("Tested Config 1")
    @MultiplayerSynced // Marks this entry to be synced with the server on join, and when server config changes. Do not use for client-side configs, you will annoy your users.
    public static String testConfig1 = "wow";

    @ConfigName("ASD 2")
    public static String asd2 = "hmmm";

    @ConfigName("ASD 3")
    public static String asd3 = "hmmm";

    /**
     * A config category, you can put other categories inside a category too.
     * See the ExampleConfigCategory class for more detail.
     */
    @Comment("My config category")
    public static ExampleConfigCategory configCategory = new ExampleConfigCategory();

    /**
     * This shows up in white, or whatever colour codes you choose at the top of the screen when you open it.
     */
    @Override
    public String getVisibleName() {
        return "My Config";
    }
}
