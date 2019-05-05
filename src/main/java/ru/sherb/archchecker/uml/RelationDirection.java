package ru.sherb.archchecker.uml;

/**
 * Направление связи, определяет каким символом будет рисоваться линия связи.
 *
 * @author maksim
 * @since 04.05.19
 */
public enum RelationDirection {
    VERTICAL("--"), HORIZONTAL("-");

    private final String sign;

    RelationDirection(String sign) {
        this.sign = sign;
    }

    public void renderTo(StringBuilder builder) {
        builder.append(sign);
    }
}
