package ru.sherb.archchecker.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Абстрактный модуль, имеет название и содержит список "классов".
 * <br/>
 * Физически может представлять из себя как пакет, так и проект или какую-то другую единицу,
 * определенную в языке программирования, для которого проводится анализ.
 *
 * @author maksim
 * @see ru.sherb.archchecker.analysis.Class
 * @since 05.05.19
 */
public final class Module {

    private final String name;

    private final Set<Class> classes = new HashSet<>();

    public Module(String name) {
        this.name = name;
    }

    public void addClass(Class cls) {
        classes.add(cls);
        cls.setModule(this);
    }

    public void addAllClasses(Collection<Class> classes) {
        this.classes.addAll(classes);
    }

    public String name() {
        return name;
    }

    public Set<Class> classes() {
        return classes;
    }

    public Optional<Class> findClassByName(String className) {
        return classes.stream()
                      .filter(cls -> cls.name().equals(className))
                      .findAny();
    }

    public boolean dependsOn(Module another) {
        for (Class cls : classes) {
            var dependencies = cls.dependencies();
            dependencies.retainAll(another.classes());
            if (!dependencies.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return name.equals(module.name) &&
                classes.equals(module.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, classes);
    }

    public Set<Class> allDependents() {
        return classes.stream()
                      .map(Class::dependents)
                      .flatMap(Collection::stream)
                      .collect(Collectors.toSet());
    }

    public Set<Class> allDependencies() {
        return classes.stream()
                      .map(Class::dependencies)
                      .flatMap(Collection::stream)
                      .collect(Collectors.toSet());
    }
}
