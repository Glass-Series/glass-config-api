package net.glasslauncher.mods.api.gcapi.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// TODO: actually implement
/**
 * Syncs the config entry with the server upon join, and server config change.
 * Will also be able to be edited by ops in-game at a later date.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiplayerSynced {
}
