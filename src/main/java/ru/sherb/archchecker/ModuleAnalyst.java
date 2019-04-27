package ru.sherb.archchecker;

import java.util.ArrayList;
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
        Map<Module, List<QualifiedName>> modulesInputDeps = new HashMap<>(environment.size());
        Map<Module, List<QualifiedName>> modulesOutputDeps = new HashMap<>(environment.size());

        for (Module module : environment) {
            List<QualifiedName> deps = module.findDependenciesFrom(environment);
            modulesInputDeps.put(module, deps);

            List<QualifiedName> out = new ArrayList<>();
            for (Module another : environment) {
                if (module.equals(another)) {
                    continue;
                }

                out.addAll(another.findDependenciesFrom(module));
            }

            modulesOutputDeps.put(module, out);
        }


        Map<Module, Double> result = new HashMap<>(environment.size());
        modulesInputDeps.forEach((module, dependencies) -> {
            var fanOut = dependencies.size();
            var fanIn = modulesOutputDeps.get(module).size();
            result.put(module, ((double) fanOut / (fanOut + fanIn)));
        });

        return result;
    }
}
