package net.glasslauncher.mods.api.gcapi.api;

/**
 * Used in ValueOnVanillaServer.
 * @see ValueOnVanillaServer
 */
public enum TriBoolean {
    TRUE(true),
    FALSE(false),
    DEFAULT(null);

    public final Boolean value;

    TriBoolean(Boolean value) {
        this.value = value;
    }
}
