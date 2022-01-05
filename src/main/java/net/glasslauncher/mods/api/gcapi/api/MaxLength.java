package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MaxLength {

    /**
     * The maximum length of the value(s) of your entry.
     * @return int value deciding the max character length of your value.
     */
    int value();
}
