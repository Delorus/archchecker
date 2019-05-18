package ru.sherb.archchecker.report;

import ru.sherb.archchecker.analysis.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 18.05.19
 */
public final class ReportManager {

    private ReportManager() { }

    public static Diagram createStabilityDiagram(ReportSetting setting, Map<Module, Double> stabilities) {
        var infos = new ModuleInfos(
                new ArrayList<>(stabilities.keySet()),
                toModuleInfoList(stabilities));

        var serializer = new PlantUMLSerializer(setting, infos);
        return new Diagram(serializer.serialize());
    }

    private static List<ModuleInfo> toModuleInfoList(Map<Module, Double> stabilities) {
        return stabilities.entrySet().stream()
                .map(stability -> {
                    var info = new ModuleInfo(stability.getKey().name());
                    info.setStability(stability.getValue());
                    return info;
                })
                .collect(Collectors.toList());
    }
}
