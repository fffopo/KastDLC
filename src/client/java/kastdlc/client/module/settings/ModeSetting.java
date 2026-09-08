package kastdlc.client.module.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {

    private final List<String> modes;

    public ModeSetting(String name, String value, String... modes) {
        super(name, value);
        this.modes = Arrays.asList(modes);
    }

    public List<String> getModes() {
        return modes;
    }

    public String getMode() {
        return getValue();
    }

    public void setMode(String mode) {
        if (modes.contains(mode)) {
            setValue(mode);
        }
    }

    public void cycle() {
        int index = modes.indexOf(getValue());

        if (index == -1 || index >= modes.size() - 1) {
            setValue(modes.get(0));
            return;
        }

        setValue(modes.get(index + 1));
    }

    public boolean is(String mode) {
        return getValue().equalsIgnoreCase(mode);
    }
}