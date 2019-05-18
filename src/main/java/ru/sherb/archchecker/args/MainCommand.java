package ru.sherb.archchecker.args;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author maksim
 * @since 18.05.19
 */
@Command(mixinStandardHelpOptions = true, abbreviateSynopsis = true, helpCommand = true)
public final class MainCommand {

    @Parameters(description = "Path to project", converter = PathConverter.class)
    public Path root;

    @Option(names = "-modules", split = ",", description = "List of modules that can be include in diagram")
    public Set<String> modules = new HashSet<>();

    @Option(names = "-depth", defaultValue = "1",
            description = "Level of dependency display, only direct dependencies are displayed at level 1")
    public int depth;
}
