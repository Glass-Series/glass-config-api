package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.impl.EventStorage;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.*;
import java.util.function.*;

public class RootScreenBuilder extends ScreenBuilder {

    private final ArrayList<BiFunction<Screen, EntrypointContainer<Object>, ScreenBuilder>> otherRoots = new ArrayList<>();
    private final List<Integer> switchButtons = new ArrayList<>();
    public int currentIndex = 1; // Arrays start at 1 :fatlaugh:

    public RootScreenBuilder(Screen parent, EntrypointContainer<Object> mod, ConfigCategory baseCategory) {
        super(parent, mod, baseCategory);
        //noinspection deprecation
        GCCore.MOD_CONFIGS.forEach((key, value) -> {
            if (key.namespace.toString().equals(mod.getProvider().getMetadata().getId())) {
                otherRoots.add((parent1, mod2) -> value.two().getConfigScreen(parent1, mod2));
            }
        });
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    public void init() {
        super.init();
        switchButtons.clear();
        ButtonWidget button = new ButtonWidget(buttons.size(), 2, 0, 20, 20, "<");
        //noinspection unchecked
        buttons.add(button);
        screenButtons.add(button);
        switchButtons.add(button.id);
        button = new ButtonWidget(buttons.size(), 24, 0, 20, 20, ">");
        //noinspection unchecked
        buttons.add(button);
        screenButtons.add(button);
        switchButtons.add(button.id);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == backButtonID) {
            //noinspection deprecation Intentional use of GCCore internals.
            GCCore.saveConfig(mod, baseCategory, EventStorage.EventSource.USER_SAVE);
        }
        if (switchButtons.contains(button.id)) {
            int index = switchButtons.get(0) == button.id? -1 : 1;
            index += currentIndex;
            if (index > otherRoots.size()-1) {
                index = 0;
            }
            else if (index < 0) {
                index = otherRoots.size()-1;
            }
            RootScreenBuilder builder = (RootScreenBuilder) otherRoots.get(index).apply(parent, mod);
            builder.currentIndex = index;
            //noinspection deprecation
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).setScreen(builder);
        }
        super.buttonClicked(button);
    }
}
