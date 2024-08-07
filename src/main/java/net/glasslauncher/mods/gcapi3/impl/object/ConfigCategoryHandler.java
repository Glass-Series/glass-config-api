package net.glasslauncher.mods.gcapi3.impl.object;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.glasslauncher.mods.gcapi3.impl.screen.RootScreenBuilder;
import net.glasslauncher.mods.gcapi3.impl.screen.ScreenBuilder;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.FancyButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class ConfigCategoryHandler extends ConfigHandlerBase {

    public final boolean isRoot;
    public Multimap<Class<?>, ConfigHandlerBase> values;

    private List<HasDrawable> button;

    public ConfigCategoryHandler(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, Multimap<Class<?>, ConfigHandlerBase> values, boolean isRoot) {
        super(id, name, description, parentField, parentObject, multiplayerSynced);
        this.values = values;
        this.isRoot = isRoot;
    }

    /**
     * The ScreenBuilder for this category. Can only have config entries.
     * @return ScreenBuilder
     */
    @Environment(EnvType.CLIENT)
    public @NotNull ScreenBuilder getConfigScreen(Screen parent, ModContainer mod) {
        return isRoot ? new RootScreenBuilder(parent, mod, this) : new ScreenBuilder(parent, mod, this);
    }

    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        if (button == null) {
            button = Collections.singletonList(new FancyButtonWidget(0, 0, 0, "Open"));
        }
        return button;
    }

    @Override
    public void applyTranslations(AtomicInteger count) {
        super.applyTranslations(count);
        values.forEach((aClass, configHandlerBase) -> configHandlerBase.applyTranslations(count));
    }
}
