package kastdlc.client.module;

import kastdlc.client.module.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private final String name;
    private final Category category;

    private boolean enabled;

    private int key = -1;

    private final List<Setting<?>> settings =
            new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {

        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean hasKey() {
        return key != -1;
    }

    public void clearKey() {
        key = -1;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public enum Category {
        COMBAT,
        MOVEMENT,
        RENDER,
        PLAYER,
        WORLD
    }
}