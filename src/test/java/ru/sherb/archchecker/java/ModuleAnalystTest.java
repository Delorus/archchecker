package ru.sherb.archchecker.java;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.sherb.archchecker.analysis.ModuleAnalyst;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO отцепить от java
 *
 * @author maksim
 * @since 04.05.19
 */
class ModuleAnalystTest {

    @Test
    @DisplayName("два модуля, первый зависит от второго")
    void twoModuleFirstDependsToSecond() {
        // setup
        var first = createModuleWithClass("first.Class1", "second.Class2");
        var second = createModuleWithClass("second.Class2");

        // when
        var analyst = new ModuleAnalyst(List.of(first, second));
        var modulesStability = analyst.countModulesStability();

        // then
        assertEquals(1.0, (double) modulesStability.get(first));
        assertEquals(0.0, (double) modulesStability.get(second));
    }

    @Test
    @DisplayName("три модуля, один зависит от двух других")
    void threeModulesOneDependsToTwo() {
        // setup
        var first = createModuleWithClass("first.Class1", "second.Class2", "third.Class3");
        var second = createModuleWithClass("second.Class2");
        var third = createModuleWithClass("third.Class3");

        // when
        var analyst = new ModuleAnalyst(List.of(first, second, third));
        var modulesStability = analyst.countModulesStability();

        // then
        assertEquals(1.0, (double) modulesStability.get(first));
        assertEquals(0.0, (double) modulesStability.get(second));
        assertEquals(0.0, (double) modulesStability.get(third));
    }

    @Test
    @DisplayName("три модуля, которые последовательно зависят друг от друга")
    void threeModulesSequenceDependency() {
        // setup
        var first = createModuleWithClass("first.Class1", "second.Class2");
        var second = createModuleWithClass("second.Class2", "third.Class3");
        var third = createModuleWithClass("third.Class3");

        // when
        var analyst = new ModuleAnalyst(List.of(first, second, third));
        var modulesStability = analyst.countModulesStability();

        // then
        assertEquals(1.0, (double) modulesStability.get(first));
        assertEquals(0.5, (double) modulesStability.get(second));
        assertEquals(0.0, (double) modulesStability.get(third));
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