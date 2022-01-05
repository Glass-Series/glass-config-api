package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Syncs the config entry with the server upon join, and server config change.
 * Will also be able to be edited by ops in-game at a later date.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MultiplayerSynced {
}
