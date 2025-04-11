package net.glasslauncher.mods.gcapi3.impl.object;


import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConfigHandlerBase {

    public final String id;

    /**
     * The name of the category. Supports colour codes. White by default.
     * Maximum length of 50.
     */
    public String name;

    public String nameKey;

    /**
     * Description of the category. Do not use colour codes. This is greyed out for legibility.
     * Maximum length of 100.
     */
    public String description;

    public String descriptionKey;

    public final Field parentField;

    public final Object parentObject;

    public final boolean multiplayerSynced;

    public ConfigHandlerBase(String id, String name, String nameKey, String description, String descriptionKey, Field parentField, Object parentObject, boolean multiplayerSynced) {
        this.id = id;
        this.name = name;
        this.nameKey = nameKey;
        this.description = description;
        this.descriptionKey = descriptionKey;
        this.parentField = parentField;
        this.parentObject = parentObject;
        this.multiplayerSynced = multiplayerSynced;
    }

    // Required because GCAPI triggers before translations are loaded. Annoying.
    public void applyTranslations(AtomicInteger count) {
        if (nameKey != null) {
            String oldName = nameKey;
            nameKey = TranslationStorage.getInstance().get(nameKey);
            if (!oldName.equals(nameKey)) {
                count.addAndGet(1);
                name = nameKey;
            }
            nameKey = oldName;
        }
        if (descriptionKey != null) {
            String oldDescription = descriptionKey;
            descriptionKey = TranslationStorage.getInstance().get(descriptionKey);
            if (!oldDescription.equals(descriptionKey)) {
                count.addAndGet(1);
                description = descriptionKey;
            }
            descriptionKey = oldDescription;
        }
    }

    @NotNull
    public abstract List<HasDrawable> getDrawables();
}
