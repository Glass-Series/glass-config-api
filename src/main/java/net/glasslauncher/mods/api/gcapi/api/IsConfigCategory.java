package net.glasslauncher.mods.api.gcapi.api;

public interface IsConfigCategory {

    /**
     * Return the name you want to have on the button to access your category and at the top while it's open.
     * @return a string with your desired name. Supports colour codes.
     */
    String getVisibleName();
}
