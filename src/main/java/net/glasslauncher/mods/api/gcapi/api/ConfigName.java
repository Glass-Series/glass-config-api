package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ConfigName {

    /**
     * This should be the visible name that you want users to see in the config GUI.
     * @return a string, supports colour codes.
     */
    String value();
}
