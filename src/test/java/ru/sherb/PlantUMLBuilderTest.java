package ru.sherb;

import org.junit.jupiter.api.Test;
import ru.sherb.archchecker.uml.Modifier;
import ru.sherb.archchecker.uml.PlantUMLBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author maksim
 * @since 27.04.19
 */
class PlantUMLBuilderTest {


    @Test
    void simpleComponentDiagram() {
        var builder = PlantUMLBuilder.newComponentDiagram();

        var diagram = builder
                .start()
                .pkg("hello")
                .end();

        assertEquals("@startuml\npackage hello\n@enduml\n", diagram);
    }

    @Test
    void simpleObjectDiagram() {
        var builder = PlantUMLBuilder.newObjectDiagram();

        var actual = builder
                .start()
                .startObject("test")
                .endObject()
                .end();

        assertEquals("@startuml\nobject test\n@enduml\n", actual);
    }

    @Test
    void objectDiagramWithAlias() {
        var builder = PlantUMLBuilder.newObjectDiagram();

        var actual = builder
                .start()
                .startObject("test object")
                    .alias("obj")
                .endObject()
                .end();

        assertEquals("@startuml\nobject \"test object\" as obj\n@enduml\n", actual);
    }

    @Test
    void objectDiagramWithoutAliasAndCompositeName() {
        var builder = PlantUMLBuilder.newObjectDiagram();

        var actual = builder
                .start()
                .startObject("test object");

        assertThrows(IllegalArgumentException.class, () -> {
            actual.endObject()
                  .end();
        });
    }

    @Test
    void objectDiagramWithSeveralFields() {
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object test {\n" +
                "simple field\n" +
                "-private_field\n" +
                "+publicField\n" +
                "~защищенное поле\n" +
                "}\n" +
                "@enduml\n";

        var actual = builder
                .start()
                .startObject("test")
                    .addField("simple field")
                    .addField(Modifier.PRIVATE, "private_field")
                    .addField(Modifier.PUBLIC, "publicField")
                    .addField(Modifier.PROTECTED, "защищенное поле")
                .endObject()
                .end();

        assertEquals(expected, actual);
    }

    @Test
    void objectDiagramWithFieldAndAlias() {
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object \"test object\" as obj {\n" +
                "simple field\n" +
                "}\n" +
                "@enduml\n";

        var actual = builder
                .start()
                .startObject("test object")
                    .alias("obj")
                    .addField("simple field")
                .endObject()
                .end();

        assertEquals(expected, actual);
    }
}