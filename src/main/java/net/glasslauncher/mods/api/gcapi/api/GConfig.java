package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface GConfig {

    /**
     * The identifier of this config entrypoint. !!!MUST BE UNIQUE FROM OTHER CONFIGS IN YOUR MOD!!!
     */
    String value();

    /**
     * This is what's shown on the top of the screen when opened. Less than 100 characters recommended.
     */
    String visibleName();

    /**
     * Make the config screen attached to the annotation the one that shows by default.
     */
    boolean primary() default false;
}
