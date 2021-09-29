package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigName {

    /**
     * This should be the visible name that you want users to see in the config GUI.
     * @return a string, supports colour codes.
     */
    String value();
}
