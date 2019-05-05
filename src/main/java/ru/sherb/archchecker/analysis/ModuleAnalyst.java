package ru.sherb.archchecker.analysis;

import ru.sherb.archchecker.java.ModuleFile;
import ru.sherb.archchecker.java.QualifiedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class ModuleAnalyst {

    private final List<ModuleFile> environment;

    public ModuleAnalyst(List<ModuleFile> environment) {
        this.environment = environment;
    }

    public Map<ModuleFile, Double> countModulesStability() {
        Map<ModuleFile, List<QualifiedName>> modulesInputDeps = new HashMap<>(environment.size());
        Map<ModuleFile, List<QualifiedName>> modulesOutputDeps = new HashMap<>(environment.size());

        for (ModuleFile module : environment) {
            List<QualifiedName> deps = module.findDependenciesFrom(environment);
            modulesInputDeps.put(module, deps);

            List<QualifiedName> out = new ArrayList<>();
            for (ModuleFile another : environment) {
                if (module.equals(another)) {
                    continue;
                }

                out.addAll(another.findDependenciesFrom(module));
            }

            modulesOutputDeps.put(module, out);
        }


        Map<ModuleFile, Double> result = new HashMap<>(environment.size());
        modulesInputDeps.forEach((module, dependencies) -> {
            var fanOut = dependencies.size();
            var fanIn = modulesOutputDeps.get(module).size();
            result.put(module, ((double) fanOut / (fanOut + fanIn)));
        });

        return result;
    }
}
