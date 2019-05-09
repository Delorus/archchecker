package ru.sherb.archchecker.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 27.04.19
 */
public final class Object implements AutoCloseable {

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
        assert alias != null && !alias.isBlank();

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
        builder.append("object");
        builder.append(' ');

        if (existAlias()) {
            builder.append('"');
            builder.append(name);
            builder.append('"');
            builder.append(" as ");
            builder.append(alias);
        } else {
            builder.append(formatName());
        }

        if (!fields.isEmpty()) {
            builder.append(" {\n");

            fields.forEach(field -> field.renderTo(builder));

            builder.append("}");
        }

        builder.append('\n');

        relations.forEach(relation -> relation.renderTo(builder));
    }

    private static final Pattern illegalChars = Pattern.compile("[ \\-.]");

    private String formatName() {
        return illegalChars.matcher(name).replaceAll("_");
    }

    private String ref() {
        return existAlias() ? alias : formatName();
    }

    private boolean existAlias() {
        return alias != null;
    }

    public Object verticalRelateTo(Object to) {
        var relation = new Relation(ref(), to.ref(), RelationDirection.VERTICAL, ArrowStyle.ARROW);

        relations.add(relation);
        return this;
    }

    public Object horizontalRelateTo(Object to) {
        var relation = new Relation(ref(), to.ref(), RelationDirection.HORIZONTAL, ArrowStyle.ARROW);

        relations.add(relation);
        return this;
    }

    @Override
    public void close() {
        endObject();
    }
}
