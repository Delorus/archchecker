package ru.sherb.archchecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        validate(args);

        Path root = Paths.get(args[0]);
        List<Module> modules = getAllPaths(root)
                .flatMap(Main::findModule)
                .collect(Collectors.toList());

        ModuleAnalyst analyst = new ModuleAnalyst(modules);
        Map<Module, Double> stabilities = analyst.countModulesStability();

        List<ModuleInfo> infos = toModuleInfos(stabilities);
        PlantUMLSerializer serializer = new PlantUMLSerializer(infos);
        Files.writeString(Paths.get("./module_infos.puml"), serializer.serialize(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

        /*
        fout-- count of import in component
        fin -- count of dependency from this component
        fout / (fout + fin)
        */
    }

    private static void validate(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: ./archchecker <path_to_project>");
        }
    }

    private static Stream<Path> getAllPaths(Path root) throws IOException {
        return Files.find(root, 1,  //todo подразумевается что все модули прямо в той папке, которую передали в аргументе
                (path, attrs) -> attrs.isDirectory());
    }

    private static Stream<Module> findModule(Path path) {
        try {
            return Files.walk(path, 1)
                        .filter(Main::isBuildFile)
                        .map(Path::getParent)
                        .distinct()
                        .map(Module::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isBuildFile(Path cur) {
        return cur.endsWith("build.gradle.kts") || cur.endsWith("pom.xml") || cur.endsWith("build.gradle");
    }

    private static List<ModuleInfo> toModuleInfos(Map<Module, Double> stabilities) {
        return stabilities.entrySet().stream()
                          .map(stability -> {
                              var info = new ModuleInfo(stability.getKey());
                              info.setStability(stability.getValue());
                              return info;
                          })
                          .collect(Collectors.toList());
    }
}

/*
module;full.fullName.class;dependency
 */