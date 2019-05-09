package ru.sherb.archchecker.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class ModuleAnalyst {

    private final List<Module> environment;

    public ModuleAnalyst(List<Module> environment) {
        this.environment = environment;
    }

    public Map<Module, Double> countModulesStability() {

        Map<Module, Double> result = new HashMap<>(environment.size());
        for (Module module : environment) {
            var fanIn = module.allDependents().size();
            var fanOut = module.allDependencies().size();
            result.put(module, ((double) fanOut / (fanOut + fanIn)));
        }

        return result;
    }
}
