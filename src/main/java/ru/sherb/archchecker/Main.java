package ru.sherb.archchecker;

import picocli.CommandLine;
import ru.sherb.archchecker.analysis.Module;
import ru.sherb.archchecker.analysis.ModuleAnalyst;
import ru.sherb.archchecker.analysis.ModuleInfo;
import ru.sherb.archchecker.analysis.PlantUMLSerializer;
import ru.sherb.archchecker.args.MainCommand;
import ru.sherb.archchecker.java.DependencyGraphCreator;
import ru.sherb.archchecker.java.ModuleFile;
import ru.sherb.archchecker.java.ModuleToJSONSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
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
        var command = CommandLine.populateCommand(new MainCommand(), args);

        List<ModuleFile> modules = getFromCacheOrLoadModules(command.root);

        var jsonSerializer = new ModuleToJSONSerializer();
        jsonSerializer.saveAsJSON(command.root.resolve(".module_infos.json"), modules);

        var graphCreator = new DependencyGraphCreator(modules);
        ModuleAnalyst analyst = new ModuleAnalyst(graphCreator.createDependsGraph());
        var stabilities = analyst.countModulesStability();

        List<ModuleInfo> infos = toModuleInfos(stabilities);
        PlantUMLSerializer serializer = new PlantUMLSerializer(infos);
        Files.writeString(Paths.get("./module_infos.puml"), serializer.serialize(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    private static List<ModuleFile> getFromCacheOrLoadModules(Path root) throws IOException {
        var modules = loadFromCache(root);

        if (modules.isEmpty()) {
            modules = loadFromDir(root);
        }

        return modules;
    }

    private static List<ModuleFile> loadFromCache(Path root) throws IOException {
        try (var caches = Files.find(root, 1,
                (path, basicFileAttributes) -> path.getFileName().toString().equals(".module_infos.json"))) {

            var cache = caches.findAny();

            if (cache.isEmpty()) {
                return Collections.emptyList();
            }

            return new ModuleToJSONSerializer().loadFrom(cache.get());
        }
    }

    private static List<ModuleFile> loadFromDir(Path root) throws IOException {
        return getAllPaths(root)
                .flatMap(Main::findModule)
                .peek(ModuleFile::load)
                .collect(Collectors.toList());
    }

    private static Stream<Path> getAllPaths(Path root) throws IOException {
        return Files.find(root, 1,  //todo подразумевается что все модули прямо в той папке, которую передали в аргументе
                (path, attrs) -> attrs.isDirectory());
    }

    private static Stream<ModuleFile> findModule(Path path) {
        try {
            return Files.walk(path, 1)
                        .filter(Main::isBuildFile)
                        .map(Path::getParent)
                        .distinct()
                        .map(ModuleFile::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO нужно что-то делать с парент помом, там нет исходников, а файл есть и мы падаем
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