package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Shows you'd rather have this config screen shown by default. Don't use this annotation more than once in your entire mod.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryGConfig {
}
