package net.glasslauncher.mods.api.gcapi.screen;


import org.jetbrains.annotations.NotNull;

public abstract class ConfigBase {

    /**
     * The name of the category. Supports colour codes. White by default.
     * @return string of maximum length of 50.
     */
    public final String name;

    /**
     * Description of the category. Do not use colour codes. This is greyed out for legibility.
     * @return string of maximum length of 100.
     */
    public final String description;

    public ConfigBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @NotNull
    public abstract HasDrawable getDrawable();
}
