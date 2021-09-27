package net.glasslauncher.mods.api.gcapi.impl.example;

import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;

public class ExampleConfigCategory implements IsConfigCategory {

    public String ohNo = "reee";

    @Override
    public String getVisibleName() {
        return "§6Oh Noes";
    }
}
