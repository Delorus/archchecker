package ru.sherb.archchecker.uml;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maksim
 * @since 27.04.19
 */
public final class Object {

    private final PlantUMLBuilder.ObjectBuilder parent;

    private final String name;

    private String alias;
    private List<Field> fields = new ArrayList<>();

    public Object(PlantUMLBuilder.ObjectBuilder parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Object alias(String alias) {
        this.alias = alias;
        return this;
    }

    public Object addField(String name) {
        fields.add(new Field(Modifier.NONE, name));
        return this;
    }

    public Object addField(Modifier mod, String name) {
        fields.add(new Field(mod, name));
        return this;
    }

    public PlantUMLBuilder.ObjectBuilder endObject() {
        renderTo(parent.builder);
        return parent;
    }

    private void renderTo(StringBuilder builder) {
        validate();

        builder.append("object");
        builder.append(' ');

        if (name.contains(" ")) {
            builder.append('"');
            builder.append(name);
            builder.append('"');
        } else {
            builder.append(name);
        }

        if (alias != null && !alias.isBlank()) {
            builder.append(" as ");
            builder.append(alias);
        }

        if (!fields.isEmpty()) {
            builder.append(" {\n");

            for (Field field : fields) {
                field.renderTo(builder);
            }

            builder.append("}");
        }

        builder.append('\n');
    }

    private void validate() {
        if (name.contains(" ") && (alias == null || alias.isBlank())) {
            throw new IllegalArgumentException(String.format("Object with composite fullName '%s' must have alias", this.name));
        }
    }
}
