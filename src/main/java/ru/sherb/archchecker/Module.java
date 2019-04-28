package ru.sherb.archchecker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class Module {

    private final Path path;

    private final String name;

    private List<Class> classes;

    public Module(Path path) {
        this.path = path;
        this.name = path.getFileName().toString();
    }

    public String name() {
        return name;
    }

    public List<QualifiedName> findDependenciesFrom(Module... environment) {

        return findDependenciesFrom(Arrays.asList(environment));
    }

    public List<QualifiedName> findDependenciesFrom(Iterable<Module> environment) {
        ensureLoad();

        List<QualifiedName> dependencies = new ArrayList<>();
        for (Module module : environment) {
            if (this.equals(module)) {
                continue;
            }

            module.ensureLoad();

            for (Class cls : classes) {
                dependencies.addAll(cls.findImportsFrom(module.classes));
            }
        }

        return dependencies;
    }

    public void load() {
        this.classes = loadClasses();
    }

    private void ensureLoad() {
        if (classes != null) {
            return;
        }

        load();
    }

    private List<Class> loadClasses() {
        Path classpath = this.path.resolve("src/main/java");

        try (var walker = Files.walk(classpath)) {
            return walker
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .map(this::loadClass)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Class loadClass(Path path) {
        try (var lines = Files.lines(path)) {
            var builder = Class
                    .builder()
                    .module(this)
                    .name(trimNameFromFile(path.getFileName()));

            return lines
                    .filter(this::isPackageOrImport)
                    .sequential()
                    .reduce(builder,
                            (b, line) -> {
                                if (isPackage(line)) {
                                    b.pkg(trimPackage(line));
                                } else if (isImport(line)) {
                                    b.addImport(trimImport(line));
                                }
                                return b;
                            },
                            (b, __) -> b) // work only in sequential stream
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String trimNameFromFile(Path fileName) {
        String name = fileName.toString();
        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }
        return name;
    }

    private boolean isPackageOrImport(String line) {
        return isPackage(line) || isImport(line);
    }

    private boolean isImport(String line) {
        return line.startsWith("import");
    }

    private boolean isPackage(String line) {
        return line.startsWith("package");
    }

    private String trimPackage(String line) {
        return trim("package", line);
    }

    private String trimImport(String line) {
        return trim("import", line);
    }

    private String trim(String prefix, String line) {
        return line.substring(prefix.length(), line.length() - 1).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Module module = (Module) o;
        return path.equals(module.path)
                && name.equals(module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }

    static final class SerializeAgent {
        String path;
        String name;
        List<Class.SerializeAgent> classes;

        static SerializeAgent from(Module module) {
            var agent = new SerializeAgent();

            agent.name = module.name();
            agent.path = module.path.toString();
            agent.classes = module.classes
                    .stream()
                    .map(Class.SerializeAgent::from)
                    .collect(Collectors.toList());

            return agent;
        }

        Module toModule() {
            var module = new Module(Path.of(path));
            module.classes = classes
                    .stream()
                    .map(jsonClass -> jsonClass.toClass(module))
                    .collect(Collectors.toList());

            return module;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Class.SerializeAgent> getClasses() {
            return classes;
        }

        public void setClasses(List<Class.SerializeAgent> classes) {
            this.classes = classes;
        }
    }
}
