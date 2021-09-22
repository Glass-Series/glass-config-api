package net.glasslauncher.mods.api.gcapi.api;

public interface HasConfigFields {

    /**
     * The relative path to your JSON file. Default value will place your config inside ".minecraft/config/modid/config.json"
     * @return a string for the config file name. Allows using "/" for subfolders.
     */
    default String getConfigPath() {
        return "config";
    }
}
