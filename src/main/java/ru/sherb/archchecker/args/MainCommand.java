package ru.sherb.archchecker.args;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;

/**
 * @author maksim
 * @since 18.05.19
 */
public final class MainCommand {

    @Parameters(description = "Path to project", converter = PathConverter.class)
    public Path root;

    @Option(names = "-modules", split = ",", description = "List of modules that can be include in diagram")
    public List<String> modules;
}
