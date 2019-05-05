package ru.sherb.archchecker.java;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.sherb.archchecker.analysis.Class;
import ru.sherb.archchecker.analysis.Module;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksim
 * @since 05.05.19
 */
class DependencyGraphCreatorTest {

    @Test
    @DisplayName("два модуля, первый зависит от второго")
    void twoModuleFirstDependsToSecond() {
        // setup
        var first = createModuleWithClass("aaaa.Class1", "bbbb.Class2");
        var second = createModuleWithClass("bbbb.Class2");

        // when
        var graphCreator = new DependencyGraphCreator(List.of(first, second));
        var modules = graphCreator.createDependsGraph();
        modules.sort(Comparator.comparing(Module::name));

        // then
        var actualFirst = modules.get(0);
        assertModuleCorrectFilled(first, actualFirst);

        var actualSecond = modules.get(1);
        assertModuleCorrectFilled(second, actualSecond);

        assertModuleDependsOn(actualFirst, actualSecond);
    }

    @Test
    @DisplayName("три модуля, первый зависит от двух других")
    void threeModulesOneDependsToTwo() {
        // setup
        var first = createModuleWithClass("aaaa.Class1", "bbbb.Class2", "cccc.Class3");
        var second = createModuleWithClass("bbbb.Class2");
        var third = createModuleWithClass("cccc.Class3");

        // when
        var graphCreator = new DependencyGraphCreator(List.of(first, second, third));
        var modules = graphCreator.createDependsGraph();
        modules.sort(Comparator.comparing(Module::name));

        // then
        var actualFirst = modules.get(0);
        assertModuleCorrectFilled(first, actualFirst);

        var actualSecond = modules.get(1);
        assertModuleCorrectFilled(second, actualSecond);

        var actualThird = modules.get(2);
        assertModuleCorrectFilled(third, actualThird);

        assertModuleDependsOn(actualFirst, actualSecond);
        assertModuleDependsOn(actualFirst, actualThird);
    }

    @Test
    @DisplayName("три модуля, которые последовательно зависят друг от друга")
    void threeModulesSequenceDependency() {
        // setup
        var first = createModuleWithClass("aaaa.Class1", "bbbb.Class2");
        var second = createModuleWithClass("bbbb.Class2", "cccc.Class3");
        var third = createModuleWithClass("cccc.Class3");

        // when
        var graphCreator = new DependencyGraphCreator(List.of(first, second, third));
        var modules = graphCreator.createDependsGraph();
        modules.sort(Comparator.comparing(Module::name));

        // then
        var actualFirst = modules.get(0);
        assertModuleCorrectFilled(first, actualFirst);

        var actualSecond = modules.get(1);
        assertModuleCorrectFilled(second, actualSecond);

        var actualThird = modules.get(2);
        assertModuleCorrectFilled(third, actualThird);

        assertModuleDependsOn(actualFirst, actualSecond);
        assertModuleDependsOn(actualSecond, actualThird);
    }

    private void assertModuleCorrectFilled(ModuleFile filledFrom, Module module) {
        assertEquals(filledFrom.name(), module.name(), "unexpected module name");

        var classes = module.classes();
        assertClassesCorrectFilled(filledFrom.getClasses(), classes);
    }

    private void assertClassesCorrectFilled(List<ClassFile> filledFrom, Set<Class> classes) {
        assertEquals(filledFrom.size(), classes.size(), "unexpected number of classes in module");

        assertAllClassFileContainsInClasses(filledFrom, classes);
    }

    public void assertAllClassFileContainsInClasses(List<ClassFile> classFiles, Set<Class> classes) {
        var classNames = classes.stream()
                                .map(Class::name)
                                .collect(Collectors.toSet());

        assertTrue(classFiles.stream()
                             .map(ClassFile::fullName)
                             .map(QualifiedName::toString)
                             .allMatch(classNames::contains), "all classes must be found");
    }

    private void assertModuleDependsOn(Module dependent, Module dependence) {
        assertTrue(dependent.dependsOn(dependence), "module should depend on " + dependence.name());
    }

    private ModuleFile createModuleWithClass(String className, String... imports) {
        var name = new QualifiedName(className);

        var module = new ModuleFile(Path.of("test", name.firstPackage()));
        var builder = ClassFile
                .builder()
                .module(module)
                .name(name.simpleName())
                .pkg(name.pkg().toString());

        for (String impt : imports) {
            builder.addImport(impt);
        }

        var cls1 = builder.build();

        module.setClasses(Collections.singletonList(cls1));

        return module;
    }
}