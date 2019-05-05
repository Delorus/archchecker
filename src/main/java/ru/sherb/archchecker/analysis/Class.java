package ru.sherb.archchecker.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Какой-то абстрактный класс, имеет название и список классов от которых он зависит.
 * <br/>
 * Физически может быть как классом, так и структурой или любым другим элементом,
 * определенным в конкретном языке программирования, который может иметь название и зависимости.
 *
 * @author maksim
 * @since 05.05.19
 */
public final class Class {

    /**
     * Имя класса, ДОЛЖНО БЫТЬ уникальным.
     */
    private final String name;

    /**
     * Классы, зависимые от текущего класса.
     * <p/>
     * Например, если текущий класс это Foo, и от него зависит класс Bar, в java-коде это может выглядеть так:
     * <pre>
     * import Foo;
     *
     * class Bar {
     *     private Foo foo = new Foo();
     * }
     * </pre>
     */
    private final Set<Class> dependents = new HashSet<>();

    /**
     * Зависимости текущего класса.
     * <p/>
     * Например, в java-коде это классы, перечисленные в {@code import}
     */
    private final Set<Class> dependencies = new HashSet<>();

    private Module module;

    public Class(String name) {
        this.name = name;
    }

    public void addDependency(Class dependency) {
        dependencies.add(dependency);
        dependency.addDependent(this);
    }

    public void addAllDependencies(Collection<Class> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    private void addDependent(Class dependent) {
        dependents.add(dependent);
    }

    public String name() {
        return name;
    }

    public Set<Class> dependencies() {
        return new HashSet<>(dependencies);
    }

    public Set<Class> dependents() {
        return new HashSet<>(dependents);
    }

    public Module module() {
        return module;
    }

    void setModule(Module module) {
        this.module = module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return name.equals(aClass.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
