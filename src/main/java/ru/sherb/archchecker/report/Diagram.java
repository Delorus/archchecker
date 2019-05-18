package ru.sherb.archchecker.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author maksim
 * @since 18.05.19
 */
public final class Diagram {

    //todo may be very large
    private final String diagram;

    Diagram(String diagram) {
        this.diagram = diagram;
    }

    public void saveOn(Path path) throws IOException {
        Files.writeString(path, diagram, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }
}
