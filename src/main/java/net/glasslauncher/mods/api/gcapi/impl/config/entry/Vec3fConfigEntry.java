package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.ExtensibleTextbox;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.util.maths.Vec3f;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Vec3fConfigEntry extends ConfigEntry<Vec3f> {
    private ExtensibleTextbox extensibleTextbox;

    public Vec3fConfigEntry(String id, String name, String description, Field parentField, Vec3f value) {
        super(id, name, description, parentField, value);
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        extensibleTextbox = new ExtensibleTextbox(textRenderer, (value) -> {
            String[] split = value.split(",");
            if (split.length == 3) {
                for (String part : split) {
                    if (!CharacterUtils.isFloat(part)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        });
        setDrawableValue(value);
    }

    @Override
    public Vec3f getDrawableValue() {
        if (extensibleTextbox == null) {
            return null;
        }
        String[] split = extensibleTextbox.getText().split(",");
        return Vec3f.from(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
    }

    @Override
    public void setDrawableValue(Vec3f value) {
        extensibleTextbox.setText(value.x + "," + value.y + "," + value.z);
    }

    @Override
    public boolean isValueValid() {
        return extensibleTextbox.isValueValid();
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        return extensibleTextbox;
    }
}
