package ru.sherb.archchecker.uml;

public enum Modifier {
    NONE(""),
    PUBLIC("+"),
    PRIVATE("-"),
    PROTECTED("~");

    private final String sign;

    Modifier(String sign) {
        this.sign = sign;
    }

    public void renderTo(StringBuilder builder) {
        builder.append(sign);
    }
}
