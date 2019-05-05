package ru.sherb.archchecker.uml;

/**
 * @author maksim
 * @since 04.05.19
 */
final class Relation {
    private final String from;
    private final String to;
    private final RelationDirection direction;
    private final ArrowStyle arrowStyle;


    //TODO from, to is instance of Object?
    Relation(String from, String to, RelationDirection direction, ArrowStyle arrowStyle) {
        this.from = from;
        this.to = to;
        this.direction = direction;
        this.arrowStyle = arrowStyle;
    }

    public void renderTo(StringBuilder builder) {
        builder.append(from);
        builder.append(' ');
        direction.renderTo(builder);
        arrowStyle.renderTo(builder);
        builder.append(' ');
        builder.append(to);
        builder.append('\n');
    }
}
