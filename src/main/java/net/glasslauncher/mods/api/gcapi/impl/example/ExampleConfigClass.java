package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.Comment;
import net.glasslauncher.mods.api.gcapi.api.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.MultiplayerSynced;
import net.glasslauncher.mods.api.gcapi.api.ValueOnVanillaServer;

/**
 * An example config class, you can view this in-game inside modmenu's settings button for gcapi.
 */
public class ExampleConfigClass {

    @ConfigName("Tested Config") // Shows up above the config entry in white, unless you use colour codes, then it will use those.
    @Comment("Used to translate nerd") // Shows up in grey under the config entry.
    public String testedConfig = "nerd";

    @ConfigName("Tested Config 1")
    @MultiplayerSynced // Marks this entry to be synced with the server on join, and when server config changes. Do not use for client-side configs, you will annoy your users.
    public String testConfig1 = "wow";

    @ConfigName("ASD 2")
    public String asd2 = "hmmm";

    @ConfigName("ASD 3")
    public String asd3 = "hmmm";

    @ConfigName("MP Synced Boolean")
    @MultiplayerSynced
    @ValueOnVanillaServer(booleanValue = true)
    public Boolean mpBool = false;

    /**
     * A config category, you can put other categories inside a category too.
     * See the ExampleConfigCategory class for more details.
     */
    @Comment("My config category")
    @ConfigCategory("§6Oh Noes")
    public ExampleConfigCategory configCategory = new ExampleConfigCategory();

}
