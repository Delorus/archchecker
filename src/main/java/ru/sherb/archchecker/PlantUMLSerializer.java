package ru.sherb.archchecker;

import ru.sherb.archchecker.uml.PlantUMLBuilder;

import java.util.List;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class PlantUMLSerializer {

    private final List<ModuleInfo> infos;

    public PlantUMLSerializer(List<ModuleInfo> infos) {
        this.infos = infos;
    }

    @Override
    public String toString() {
        var diagram = PlantUMLBuilder
                .newObjectDiagram()
                .start();

        for (ModuleInfo info : infos) {
            diagram = diagram.startObject(info.name())
                             .addField("I = " + info.stability())
                             .endObject();
        }

        return diagram.end();
    }

}
