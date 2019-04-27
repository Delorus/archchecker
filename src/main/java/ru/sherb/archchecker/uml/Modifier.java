package ru.sherb.archchecker.uml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Modifier implements Renderable {
    NONE(""),
    PUBLIC("+"),
    PRIVATE("-"),
    PROTECTED("~");

    private final String sign;

    @Contract(pure = true)
    Modifier(String sign) {
        this.sign = sign;
    }

    @Override
    public void renderTo(@NotNull StringBuilder builder) {
        builder.append(sign);
    }
}
