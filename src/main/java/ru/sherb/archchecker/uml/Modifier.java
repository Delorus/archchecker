package ru.sherb.archchecker.uml;

public enum Modifier implements Renderable {
    NONE(""),
    PUBLIC("+"),
    PRIVATE("-"),
    PROTECTED("~");

    private final String sign;

    Modifier(String sign) {
        this.sign = sign;
    }

    @Override
    public void renderTo(StringBuilder builder) {
        builder.append(sign);
    }
}
