package ru.sherb.archchecker.java;

import ru.sherb.archchecker.analysis.Class;
import ru.sherb.archchecker.analysis.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author maksim
 * @since 05.05.19
 */
public final class DependencyGraphCreator {

    private final List<ModuleFile> env;

    public DependencyGraphCreator(List<ModuleFile> env) {
        assert env != null;

        this.env = env;
    }

    public List<Module> createDependsGraph() {
        var classes = mappingClassNameToClass();

        fillClassesDependencies(classes);

        var modules = mappingModuleNameToModule();

        fillModulesWithClasses(modules, classes);

        return new ArrayList<>(modules.values());
    }

    private HashMap<String, ClassWrapper> mappingClassNameToClass() {
        return env.stream()
                  .flatMap(this::toFlatClasses)
                  .collect(Collectors.toMap(
                          ClassWrapper::name,
                          constructor -> constructor,
                          (a, b) -> b,
                          HashMap::new));
    }

    private Stream<ClassWrapper> toFlatClasses(ModuleFile module) {
        return ModuleFile.SerializeAgent.from(module)
                .getClasses()
                .stream()
                .map(cls -> new ClassWrapper(cls.fullName, module.name(), cls.imports));
    }

    private void fillClassesDependencies(HashMap<String, ClassWrapper> classes) {
        classes.forEach((__, cls) -> {
            cls.deps().forEach(depName -> {
                var dep = classes.get(depName);
                if (dep != null) {
                    cls.addDependency(dep.unwrap());
                }
            });
        });
    }

    private HashMap<String, Module> mappingModuleNameToModule() {
        return env
                .stream()
                .collect(Collectors.toMap(
                        ModuleFile::name,
                        moduleFile -> new Module(moduleFile.name()),
                        (a, b) -> b,
                        HashMap::new));
    }

    private void fillModulesWithClasses(HashMap<String, Module> modules, HashMap<String, ClassWrapper> classes) {
        classes.values().forEach(cls -> {
            modules.computeIfPresent(cls.moduleName(), (__, module) -> {
                module.addClass(cls.unwrap());
                return module;
            });
        });
    }

    private static class ClassWrapper {
        private final String name;
        private final String moduleName;
        private final List<String> deps;

        private final Class cls;

        private ClassWrapper(String name, String moduleName, List<String> deps) {
            this.name = name;
            this.moduleName = moduleName;
            this.deps = deps;

            cls = new Class(name);
        }

        public String name() {
            return name;
        }

        public String moduleName() {
            return moduleName;
        }

        public List<String> deps() {
            return deps;
        }

        public void addDependency(Class cls) {
            this.cls.addDependency(cls);
        }

        public Class unwrap() {
            return this.cls;
        }
    }
}
