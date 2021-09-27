package net.glasslauncher.mods.api.gcapi.api;

public interface HasConfigFields {

    /**
     * The relative path to your JSON file. Default value will place your config inside ".minecraft/config/modid/config.json"
     * @return a string for the config file name. Allows using "/" for subfolders.
     */
    default String getConfigPath() {
        return "config";
    }

    /**
     * Return the name you want to have on the header of your config GUI.
     * @return a string with your desired name. Supports colour codes.
     */
    String getVisibleName();
}
