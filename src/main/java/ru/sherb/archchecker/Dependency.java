package ru.sherb.archchecker;

/**
 * @author maksim
 * @since 23.04.19
 */
public final class Dependency {

    private final Class parent;
    private final QualifiedName name;

    public Dependency(Class parent, QualifiedName name) {
        this.parent = parent;
        this.name = name;
    }
}
