package net.glasslauncher.mods.gcapi3.mixin.client;
//    "client.ModMenuMixin",
import com.terraformersmc.modmenu.ModMenu;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(ModMenu.class)
public class ModMenuMixin {

    @Shadow(remap = false) private static Map<String, Function<Screen, ? extends Screen>> configScreenFactories;

    @Unique private final HashMap<String, Integer> lowestIndexes = new HashMap<>();

    @Inject(method = "onInitializeClient", at = @At("TAIL"), remap = false)
    private void hijackConfigScreens(CallbackInfo ci) {
        //noinspection deprecation
        GCCore.log("Adding config screens to ModMenu...");
        Map<String, Function<Screen, ? extends Screen>> map = new HashMap<>();
        //noinspection deprecation
        GCCore.MOD_CONFIGS.forEach((key, value) -> {
            String namespace = key.split(":")[0];

            if (!map.containsKey(namespace)) {
                lowestIndexes.put(namespace, value.configRoot().index());
                map.put(namespace, (parent) -> value.configCategoryHandler().getConfigScreen(parent, value.modContainer()));
            } else if (value.configRoot().index() < lowestIndexes.getOrDefault(namespace, Integer.MAX_VALUE)) {
                lowestIndexes.put(namespace, value.configRoot().index());
                map.put(namespace, (parent) -> value.configCategoryHandler().getConfigScreen(parent, value.modContainer()));
            }
        });
        configScreenFactories.putAll(map);
    }

}
