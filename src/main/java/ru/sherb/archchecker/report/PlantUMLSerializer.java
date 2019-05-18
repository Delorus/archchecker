package ru.sherb.archchecker.report;

import ru.sherb.archchecker.analysis.Module;
import ru.sherb.archchecker.uml.Object;
import ru.sherb.archchecker.uml.PlantUMLBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 23.04.19
 */
final class PlantUMLSerializer {

    private final ModuleInfos infos;
    private final ReportSetting setting;

    PlantUMLSerializer(ReportSetting setting, ModuleInfos infos) {
        this.setting = setting;
        this.infos = infos;
    }

    String serialize() {
        var diagram = PlantUMLBuilder
                .newObjectDiagram()
                .start();

        if (setting.includeModules.isEmpty()) {
            diagram = serializeAllModules(diagram);
        } else {
            diagram = serializeOnlyIncluding(diagram);
        }

        return diagram.end();
    }

    private PlantUMLBuilder.ObjectBuilder serializeAllModules(PlantUMLBuilder.ObjectBuilder diagram) {
        for (ModuleInfo info : infos) {
            diagram = createObjectWithDeps(diagram, null, infos.getModule(info), -1)
                    .addField("I = " + info.stability())
                    .endObject();
        }
        return diagram;
    }

    private final Map<String, Object> cache = new HashMap<>();

    private Object createObjectWithDeps(PlantUMLBuilder.ObjectBuilder builder, Module prev, Module module, int depth) {
        if (cache.containsKey(module.name())) {
            return cache.get(module.name());
        }

        var object = builder.startObject(module.name());

        cache.put(module.name(), object);

        if (depth == 0) {
            return object;
        }

        for (Module dependency : module.allDependencyModules()) { //todo heavy operation
            if (dependency.equals(prev)) {
                continue;
            }

            object.relateTo(createObjectWithDeps(builder, module, dependency, depth - 1));
        }

        for (Module dependent : module.allDependentModules()) { //todo heavy operation
            if (dependent.equals(module) || dependent.equals(prev)) {
                continue;
            }

            createObjectWithDeps(builder, module, dependent, depth - 1).relateTo(object); //todo double relations
        }

        return object;
    }

    private PlantUMLBuilder.ObjectBuilder serializeOnlyIncluding(PlantUMLBuilder.ObjectBuilder diagram) {
        for (ModuleInfo info : infos) {
            if (!setting.includeModules.contains(info.name())) {
                continue;
            }

            createObjectWithDeps(diagram, null, infos.getModule(info), setting.dependencyDepth);

            cache.forEach((name, object) ->
                    object.addField("I = " + infos.getInfoByName(name).stability())
                          .endObject());
        }
        return diagram;
    }

    private List<ModuleInfo> findAllDependencies(ModuleInfo info, int depth) {
        var current = infos.getModule(info);

        var dependencies = allDependencies(current, depth);
        dependencies.addAll(allDependents(current, depth));
        return dependencies.stream()
                .map(infos::getInfo)
                .collect(Collectors.toList());
    }

    private List<Module> allDependencies(Module current, int depth) {
        var allDependencyModules = current.allDependencyModules();
        return getAllDeps(allDependencyModules, depth);
    }

    private List<Module> allDependents(Module current, int depth) {
        var allDependentModules = current.allDependentModules();
        return getAllDeps(allDependentModules, depth);
    }

    private List<Module> getAllDeps(Set<Module> deps, int depth) {
        // не возвращаем крайние зависимости,
        // потому что мы их все равно получим выше в serializeOnlyIncluding
        if (deps.isEmpty() || depth == 0) {
            return Collections.emptyList();
        }

        List<Module> dependencies = new ArrayList<>(deps);
        for (Module module : deps) {
            dependencies = allDependencies(module, depth - 1);
            dependencies.addAll(allDependents(module, depth - 1));
        }

        return dependencies;
    }
}
