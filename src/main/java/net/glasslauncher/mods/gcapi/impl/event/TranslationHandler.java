package net.glasslauncher.mods.gcapi.impl.event;

import net.glasslauncher.mods.gcapi.impl.GCCore;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.modificationstation.stationapi.api.event.resource.language.TranslationInvalidationEvent;

import java.util.concurrent.atomic.*;

public class TranslationHandler {

    @SuppressWarnings("deprecation")
    @EventListener(priority = ListenerPriority.LOWEST)
    private static void handleTranslations(TranslationInvalidationEvent event) {
        AtomicInteger count = new AtomicInteger();
        GCCore.log("Loading config translations.");
        GCCore.MOD_CONFIGS.forEach((identifier, configRootEntry) -> configRootEntry.configCategoryHandler().applyTranslations(count));
        GCCore.log("Loaded " + count.get() + " translations.");
    }
}
