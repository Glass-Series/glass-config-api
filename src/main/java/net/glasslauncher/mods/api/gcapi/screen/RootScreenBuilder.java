package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.impl.GlassConfigAPI;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class RootScreenBuilder extends ScreenBuilder {

    private Map<String, BiFunction<ScreenBase, EntrypointContainer<Object>, ScreenBuilder>> otherRoots = new HashMap<>();
    private List<Integer> otherRootIDs = new ArrayList<>();

    boolean doSave = true;

    public RootScreenBuilder(ScreenBase parent, EntrypointContainer<Object> mod, ConfigCategory baseCategory) {
        super(parent, mod, baseCategory);
        GlassConfigAPI.MOD_CONFIGS.forEach((key, value) -> {
            if (key.modID.toString().equals(mod.getProvider().getMetadata().getId())){// && !value.two().id.equals(baseCategory.id)) {
                otherRoots.put(value.two().name, (parent1, mod2) -> value.two().getConfigScreen(parent1, mod2));
            }
        });
    }

    @Override
    public void onClose() {
        super.onClose();
        if (doSave) {
            GlassConfigAPI.saveConfig(mod, baseCategory);
        }
    }

    @Override
    public void init() {
        super.init();
        doSave = true;
        otherRootIDs.clear();
        otherRoots.keySet().forEach((key) -> {
            Button button = new Button(buttons.size(), 81*otherRootIDs.size(), 0, 80, 20, key);
            buttons.add(button);
            screenButtons.add(button);
            otherRootIDs.add(button.id);
        });
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id != backButtonID) {
            doSave = false;
        }
        if (otherRootIDs.contains(button.id)) {
            ((Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(otherRoots.get(button.text).apply(parent, mod));
        }
        super.buttonClicked(button);
    }
}
