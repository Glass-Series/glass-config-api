package net.glasslauncher.mods.gcapi3.impl.object;


import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public abstract class ConfigHandlerBase {

    public final String id;

    /**
     * The name of the category. Supports colour codes. White by default.
     * Maximum length of 50.
     */
    public String name;

    /**
     * Description of the category. Do not use colour codes. This is greyed out for legibility.
     * Maximum length of 100.
     */
    public String description;

    public final Field parentField;

    public final Object parentObject;

    public final boolean multiplayerSynced;

    public ConfigHandlerBase(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced) {
        this.id = id;
        this.name = name;
        this.description = description == null ? "" : description;
        this.parentField = parentField;
        this.parentObject = parentObject;
        this.multiplayerSynced = multiplayerSynced;
    }

    // Required because GCAPI triggers before translations are loaded. Annoying.
    public void applyTranslations(AtomicInteger count) {
        String oldName = name;
        name = TranslationStorage.getInstance().get(name);
        if(!oldName.equals(name)) {
            count.addAndGet(1);
        }
        String oldDescription = description;
        description = TranslationStorage.getInstance().get(description);
        if(!oldDescription.equals(description)) {
            count.addAndGet(1);
        }
    }

    @NotNull
    public abstract List<HasDrawable> getDrawables();
}
