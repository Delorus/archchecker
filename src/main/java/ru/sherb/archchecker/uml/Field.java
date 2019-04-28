package ru.sherb.archchecker.uml;

/**
 * @author maksim
 * @since 27.04.19
 */
public final class Field {

    private final Modifier mod;
    private final String name;

    Field(Modifier mod, String name) {
        assert mod != null;
        assert name == null || !name.isBlank();

        this.mod = mod;
        this.name = name;
    }

    public void renderTo(StringBuilder builder) {
        mod.renderTo(builder);
        builder.append(name);
        builder.append('\n');
    }
}

