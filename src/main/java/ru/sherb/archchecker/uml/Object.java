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
    private List<Relation> relations = new ArrayList<>();

    Object(PlantUMLBuilder.ObjectBuilder parent, String name) {
        this.parent = parent;

        assert name != null && !name.isBlank();
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

        if (isNameValidRef()) {
            builder.append(name);
        } else {
            builder.append('"');
            builder.append(name);
            builder.append('"');
        }

        if (alias != null && !alias.isBlank()) {
            builder.append(" as ");
            builder.append(alias);
        }

        if (!fields.isEmpty()) {
            builder.append(" {\n");

            fields.forEach(field -> field.renderTo(builder));

            builder.append("}");
        }

        builder.append('\n');

        relations.forEach(relation -> relation.renderTo(builder));
    }

    private void validate() {
        if (!isNameValidRef() && (alias == null || alias.isBlank())) {
            throw new IllegalArgumentException(String.format("Object with composite fullName '%s' must have alias", this.name));
        }
    }

    private boolean isNameValidRef() {
        return !name.contains(" ");
    }

    private String ref() {
        return isNameValidRef() ? name : alias;
    }

    public void verticalRelateTo(Object to) {
        var relation = new Relation(ref(), to.ref(), RelationDirection.VERTICAL, ArrowStyle.ARROW);

        relations.add(relation);
    }

    public Object verticalRelateTo2(String to) {
        var relation = new Relation(ref(), to, RelationDirection.VERTICAL, ArrowStyle.ARROW);

        relations.add(relation);
        return this;
    }

    public Object horizontalRelateTo(Object to) {
        var relation = new Relation(ref(), to.ref(), RelationDirection.HORIZONTAL, ArrowStyle.ARROW);

        relations.add(relation);
        return this;
    }
}
