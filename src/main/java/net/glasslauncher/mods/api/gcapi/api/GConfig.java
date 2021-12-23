package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GConfig {

    /**
     * The identifier of this config entrypoint. !!!MUST BE UNIQUE FROM OTHER CONFIGS IN YOUR MOD!!!
     */
    String value();

    /**
     * This is what's shown on the top of the screen when opened. Less than 100 characters recommended.
     */
    String visibleName();
}
