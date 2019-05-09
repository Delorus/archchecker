package ru.sherb.archchecker.uml;

/**
 * @author maksim
 * @since 04.05.19
 */
public enum ArrowStyle {
    EMPTY(""),
    ARROW(">"),
    EMPTY_ARROW("|>"),
    EMPTY_DIAMOND("o"),
    DIAMOND("*"),
    SQUARE("#"),
    CROSS("x"),
    TRIANGLE("{"),
    PLUS("+");

    private final String sign;

    ArrowStyle(String sign) {
        this.sign = sign;
    }

    public void renderTo(StringBuilder builder) {
        builder.append(sign);
    }
}
