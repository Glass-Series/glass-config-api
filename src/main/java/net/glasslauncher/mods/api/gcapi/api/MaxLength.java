package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MaxLength {

    /**
     * The maximum length of the value(s) of your entry.
     * @return int value deciding the max character length of your value.
     */
    int value();
}
