package net.glasslauncher.mods.gcapi3.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ConfigEntry {

    /**
     * This should be the visible name that you want users to see in the config GUI. Supports translation keys.
     * @return a string, supports colour codes.
     */
    String name();

    /**
     * If this is set, GCAPI will attempt to use the translation key, if it has a translation entry.
     * Requires something that handles translations, like StationAPI.
     * Falls back to name.
     */
    String nameKey() default "";

    /**
     * The description shown to users in the scroll menu. ~30 chars max is recommended. Supports translation keys.
     * @return a string, supports colour codes.
     */
    String description() default "";

    /**
     * If this is set, GCAPI will attempt to use the translation key, if it has a translation entry.
     * Requires something that handles translations, like StationAPI.
     * Falls back to description.
     */
    String descriptionKey() default "";

    /**
     * The comment shown inside config files. Can be as long as you want, and supports newlines. Does NOT support colour codes.
     * If blank, description is shown instead.
     */
    String comment() default "";

    /**
     * Unimplemented. Will be attached to a "?" button for users to show a fullscreen and scrollable description.
     */
    String longDescription() default "";

    /**
     * Syncs the config entry with the server upon join, and server config change.
     * Will also be able to be edited by ops in-game.
     */
    boolean multiplayerSynced() default false;

    /**
     * Use maxValue instead. Will be removed in 4.0.
     */
    @Deprecated(forRemoval = true)
    long maxLength() default 32;

    /**
     * Use minValue instead. Will be removed in 4.0.
     */
    @Deprecated(forRemoval = true)
    long minLength() default 0;

    /**
     * The maximum length of this value.
     * Default 32.
     * Numeric values: the actual number value.
     * Strings: how many characters.
     * Applies to the contents of arrays, not the arrays themselves. See max and minArrayLength.
     */
    double maxValue() default 32;

    /**
     * The minimum length of this value.
     * Default 0.
     * Numeric values: the actual number value.
     * Strings: how many characters.
     * Applies to the contents of arrays, not the arrays themselves. See max and minArrayLength.
     */
    double minValue() default 0;

    long maxArrayLength() default Byte.MAX_VALUE;
    long minArrayLength() default 0;

    /**
     * If true, hides this config entry from the user, and also skips all UI generation.
     * This is a good idea to set on any lists that are destined to get abhorrently long.
     */
    boolean hidden() default false;

    /**
     * If true, displays a warning to the user advising them to reboot their game after changing this.
     */
    boolean requiresRestart() default false;
}
