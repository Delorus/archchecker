package ru.sherb.archchecker.analysis;

import ru.sherb.archchecker.uml.Object;
import ru.sherb.archchecker.uml.PlantUMLBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class PlantUMLSerializer {

    private final List<ModuleInfo> infos;

    public PlantUMLSerializer(List<ModuleInfo> infos) {
        this.infos = infos;
    }

    public String serialize() {
        var diagram = PlantUMLBuilder
                .newObjectDiagram()
                .start();

        for (ModuleInfo info : infos) {
            diagram = createObjectWithDeps(diagram, info.module())
                    .addField("I = " + info.stability())
                    .endObject();
        }

        return diagram.end();
    }

    private final Map<String, Object> cache = new HashMap<>();

    private Object createObjectWithDeps(PlantUMLBuilder.ObjectBuilder builder, Module module) {
        if (cache.containsKey(module.name())) {
            return cache.get(module.name());
        }

        var object = builder.startObject(module.name());

        cache.put(module.name(), object);

        for (Module dependency : module.allDependencyModules()) {
            object.relateTo(createObjectWithDeps(builder, dependency));
        }

        return object;
    }

}
