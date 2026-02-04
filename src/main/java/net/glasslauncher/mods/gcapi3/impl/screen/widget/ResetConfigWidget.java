package net.glasslauncher.mods.gcapi3.impl.screen.widget;

import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigCategoryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;

import java.util.ArrayList;
import java.util.List;

public class ResetConfigWidget extends IconWidget {
    private final ConfigEntryHandler<?> configEntry;
    private final ConfigCategoryHandler configCategory;

    public ResetConfigWidget(ConfigEntryHandler<?> configEntry) {
        this(configEntry, null);
    }

    public ResetConfigWidget(ConfigCategoryHandler configCategory) {
        this(null, configCategory);
    }

    protected ResetConfigWidget(ConfigEntryHandler<?> configEntry, ConfigCategoryHandler configCategory) {
        super(0, 0, 0, 0, "/assets/gcapi3/reset.png");
        this.configEntry = configEntry;
        this.configCategory = configCategory;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            try {
                if (configEntry != null) {
                    configEntry.resetMultiplayerSafeRecursive();
                }
                else if (configCategory != null) {
                    configCategory.resetMultiplayerSafeRecursive();
                }
                MinecraftAccessor.getInstance().soundManager.playSound("random.click", 1.0F, 1.0F);
            } catch (IllegalAccessException e) {
                //noinspection deprecation
                GCCore.logError(e);
            }
        }
    }

    @Override
    public void setXYWH(int x, int y, int width, int height) {
        super.setXYWH(x, y - 10, width, height);
    }

    @Override
    public List<String> getTooltip() {
        if (configEntry != null && configEntry.multiplayerLoaded) {
            return new ArrayList<>();
        }
        if (configCategory != null) {
            return List.of("Reset all inside to default.");
        }
        return List.of("Reset this config to default.");
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (configEntry != null && !configEntry.multiplayerLoaded) {
            super.draw(mouseX, mouseY);
        }
        else if (configCategory != null) {
            super.draw(mouseX, mouseY);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void keyPressed(char character, int key) {

    }

    @Override
    public void setID(int id) {

    }
}
