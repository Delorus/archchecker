package ru.sherb.archchecker.java;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.sherb.archchecker.analysis.Class;
import ru.sherb.archchecker.analysis.Module;
import ru.sherb.archchecker.analysis.ModuleAnalyst;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author maksim
 * @since 04.05.19
 */
public class ModuleAnalystTest {

    @Test
    @DisplayName("два модуля, первый зависит от второго")
    public void twoModuleFirstDependsToSecond() {
        // setup
        var first = createModuleWithClass("first", "first.Class1");
        var second = createModuleWithClass("second", "second.Class2");

        join(first).with(second).on("first.Class1", "second.Class2");

        // when
        var analyst = new ModuleAnalyst(List.of(first, second));
        var modulesStability = analyst.countModulesStability();

        // then
        assertEquals(1.0, (double) modulesStability.get(first));
        assertEquals(0.0, (double) modulesStability.get(second));
    }

    @Test
    @DisplayName("три модуля, первый зависит от двух других")
    public void threeModulesOneDependsToTwo() {
        // setup
        var first = createModuleWithClass("first", "first.Class1");
        var second = createModuleWithClass("second", "second.Class2");
        var third = createModuleWithClass("third", "third.Class3");

        join(first).with(second).on("first.Class1", "second.Class2")
                   .with(third).on("first.Class1", "third.Class3");

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
    public void threeModulesSequenceDependency() {
        // setup
        var first = createModuleWithClass("first", "first.Class1");
        var second = createModuleWithClass("second", "second.Class2");
        var third = createModuleWithClass("third", "third.Class3");

        join(first).with(second).on("first.Class1", "second.Class2");
        join(second).with(third).on("second.Class2", "third.Class3");

        // when
        var analyst = new ModuleAnalyst(List.of(first, second, third));
        var modulesStability = analyst.countModulesStability();

        // then
        assertEquals(1.0, (double) modulesStability.get(first));
        assertEquals(0.5, (double) modulesStability.get(second));
        assertEquals(0.0, (double) modulesStability.get(third));
    }

    @Test
    @Disabled
    public void wrongTest() {
        assertTrue(false);
    }

    private Module createModuleWithClass(String moduleName, String className) {
        var module = new Module(moduleName);
        var cls = new Class(className);
        module.addClass(cls);

        return module;
    }

    private Joiner join(Module module) {
        return new Joiner(module);
    }

    private static class Joiner {
        private final Module module;

        private Joiner(Module module) {
            this.module = module;
        }

        public JoinTwoModules with(Module another) {
            return new JoinTwoModules(another);
        }

        private class JoinTwoModules {
            private final Module another;

            private JoinTwoModules(Module another) {
                this.another = another;
            }

            public Joiner on(String firstClass, String secondClass) {
                var first = module.findClassByName(firstClass).get();
                var second = another.findClassByName(secondClass).get();

                first.addDependency(second);

                return Joiner.this;
            }
        }
    }
}