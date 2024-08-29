package net.glasslauncher.mods.gcapi3.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ConfigRoot {

    /**
     * The identifier of this config entrypoint. !!!MUST BE UNIQUE FROM OTHER CONFIGS IN YOUR MOD!!!
     */
    String value();

    /**
     * This is what's shown at the top of the screen when opened. Less than 100 characters recommended.
     */
    String visibleName();

    /**
     * If this is set, GCAPI will attempt to use the translation key, if it has a translation entry.
     * Requires something that handles translations, like StationAPI.
     * Falls back to visibleName.
     */
    String nameKey() default "";

    /**
     * The index this page uses. Those without a specified index will be added last.
     */
    int index() default Integer.MAX_VALUE;


    boolean multiplayerSynced() default false;
}
