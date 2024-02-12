package com.badbones69.crazycrates.api.modules;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleLoader {

    private final List<ModuleHandler> modules = new ArrayList<>();

    private EventRegistry registry;

    public void load() {
        this.registry = new EventRegistry();

        this.modules.forEach(module -> {
            if (module.isEnabled()) {
                this.registry.addListener(module);
                return;
            }

            this.registry.removeListener(module);
        });
    }
    
    public void unload() {
        this.modules.forEach(module -> {
            if (module.isEnabled()) {
                this.registry.removeListener(module);
            }
        });
    }

    public void addModule(ModuleHandler module) {
        if (containsModule(module)) return;

        this.modules.add(module);
    }

    public void removeModule(ModuleHandler module) {
        if (!containsModule(module)) return;

        this.modules.remove(module);
    }

    @NotNull
    public List<ModuleHandler> getModules() {
        return Collections.unmodifiableList(this.modules);
    }

    private boolean containsModule(ModuleHandler module) {
        return this.modules.contains(module);
    }

    @NotNull
    public EventRegistry getRegistry() {
        return this.registry;
    }
}