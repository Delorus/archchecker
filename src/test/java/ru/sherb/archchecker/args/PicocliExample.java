package ru.sherb.archchecker.args;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author maksim
 * @since 18.05.19
 */
public class PicocliExample {

    @Test
    @DisplayName("получаем путь к проекту без указания дополнительных параметров")
    public void getRootPathWithoutOptionalParams() {
        // when
        MainCommand params = CommandLine.populateCommand(new MainCommand(), "./path/to/root");

        // then
        assertEquals(Path.of("./path/to/root"), params.root);
    }

    @Test
    @DisplayName("получаем список модулей, разделенных символом ','")
    void getOptionalListParameters() {
        // setup
        String[] args = {"path/to/root", "-modules=npo,npi,qm.params"};

        // when
        var params = CommandLine.populateCommand(new MainCommand(), args);

        // then
        assertEquals(Path.of("path/to/root"), params.root);
        assertArrayEquals(new String[]{"npo", "npi", "qm.params"}, params.modules.toArray(String[]::new));
    }

    @Test
    @DisplayName("получаем один модуль из опционального параметра-списка")
    void getSingularOptionalListParameters() {
        // setup
        String[] args = {"path/to/root", "-modules", "npo"};

        // when
        var params = CommandLine.populateCommand(new MainCommand(), args);

        // then
        assertEquals(Path.of("path/to/root"), params.root);
        assertEquals("npo", params.modules.toArray(String[]::new)[0]);
    }
}
