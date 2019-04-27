package ru.sherb.archchecker.uml;

import org.jetbrains.annotations.NotNull;

/**
 * @author maksim
 * @since 27.04.19
 */
interface Renderable {

    //TODO come up with new fullName
    void renderTo(@NotNull StringBuilder builder);
}
