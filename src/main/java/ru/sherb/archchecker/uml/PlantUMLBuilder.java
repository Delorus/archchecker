package ru.sherb.archchecker.uml;

/**
 * @author maksim
 * @since 27.04.19
 */
public abstract class PlantUMLBuilder<T extends PlantUMLBuilder> {

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
