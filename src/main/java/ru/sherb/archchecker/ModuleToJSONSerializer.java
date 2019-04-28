package ru.sherb.archchecker;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author maksim
 * @since 28.04.19
 */
public class ModuleToJSONSerializer {

    public void saveAsJSON(Path path, List<Module> modules) throws IOException {
        try (var writer = new GZIPOutputStream(
                Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE))) {

            var objects = modules.stream()
                                 .map(Module.SerializeAgent::from)
                                 .toArray();
            JSON.writeJSONString(writer, objects);
        }
    }

    public List<Module> fromJSON(String jsonModules) {
        var agents = JSON.parseArray(jsonModules, Module.SerializeAgent.class);
        if (agents == null || agents.isEmpty()) {
            return Collections.emptyList();
        }

        return agents.stream()
                     .map(Module.SerializeAgent::toModule)
                     .collect(Collectors.toList());
    }

    public List<Module> loadFrom(Path path) throws IOException {
        try(var reader = new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ))) {

            return fromJSON(new String(reader.readAllBytes()));
        }
    }
}
