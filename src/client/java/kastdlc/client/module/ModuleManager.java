package kastdlc.client.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModules(Module.Category category) {
        return modules.stream()
                .filter(module -> module.getCategory() == category)
                .toList();
    }
}