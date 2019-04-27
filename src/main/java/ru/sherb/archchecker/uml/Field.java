package ru.sherb.archchecker.uml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author maksim
 * @since 27.04.19
 */
public final class Field implements Renderable {

    private final Modifier mod;
    private final String name;

    @Contract("null, _ -> fail")
    Field(Modifier mod, String name) {
        assert mod != null;
        assert name == null || !name.isBlank();

        this.mod = mod;
        this.name = name;
    }

    @Override
    public void renderTo(@NotNull StringBuilder builder) {
        mod.renderTo(builder);
        builder.append(name);
        builder.append('\n');
    }
}

