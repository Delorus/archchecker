package ru.sherb.archchecker.uml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author maksim
 * @since 27.04.19
 */
public abstract class PlantUMLBuilder<T extends PlantUMLBuilder> {

    @NotNull
    @Contract(" -> new")
    public static PlantUMLBuilder<ComponentBuilder> newComponentDiagram() {
        return new ComponentBuilder();
    }

    @NotNull
    @Contract(" -> new")
    public static PlantUMLBuilder<ObjectBuilder> newObjectDiagram() {
        return new ObjectBuilder();
    }

    protected final StringBuilder builder = new StringBuilder();

    public T start() {
        builder.append("@startuml\n");
        return self();
    }

    public String end() {
        builder.append("@enduml\n");

        var result = builder.toString();
        reset();
        return result;
    }

    private void reset() {
        builder.setLength(0);
    }

    protected abstract T self();

    public static class ComponentBuilder extends PlantUMLBuilder<ComponentBuilder> {

        public ComponentBuilder pkg(String name) {
            builder.append("package ");
            builder.append(name);
            builder.append("\n");
            return this;
        }

        @Override
        protected ComponentBuilder self() {
            return this;
        }
    }

    public static class ObjectBuilder extends PlantUMLBuilder<ObjectBuilder> {

        public Object startObject(String name) {
            return new Object(this, name);
        }

        @Override
        protected ObjectBuilder self() {
            return this;
        }
    }

}
