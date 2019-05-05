package ru.sherb.archchecker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.sherb.archchecker.uml.Modifier;
import ru.sherb.archchecker.uml.PlantUMLBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author maksim
 * @since 27.04.19
 */
class ObjectBuilderTest {

    @Test
    @DisplayName("создание простого объекта")
    void simpleObjectDiagram() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();

        // when
        var actual = builder
                .start()
                .startObject("test")
                .endObject()
                .end();

        // then
        assertEquals("@startuml\nobject test\n@enduml\n", actual);
    }

    @Test
    @DisplayName("создание объекта с псевдонимом")
    void objectDiagramWithAlias() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();

        // when
        var actual = builder
                .start()
                .startObject("test object")
                    .alias("obj")
                .endObject()
                .end();

        // then
        assertEquals("@startuml\nobject \"test object\" as obj\n@enduml\n", actual);
    }

    @Test
    @DisplayName("если создать объект с именем, состоящим из нескольких слов без псевдонима, то будет ошибка")
    void objectDiagramWithoutAliasAndCompositeName() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();

        // expect
        assertThrows(IllegalArgumentException.class, () -> {
            builder.start()
                   .startObject("test object")
                   .endObject()
                   .end();
        });
    }

    @Test
    @DisplayName("создание сложного объекта с разнотипными полями")
    void objectDiagramWithSeveralFields() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object test {\n" +
                "simple field\n" +
                "-private_field\n" +
                "+publicField\n" +
                "~защищенное поле\n" +
                "}\n" +
                "@enduml\n";

        // when
        var actual = builder
                .start()
                .startObject("test")
                    .addField("simple field")
                    .addField(Modifier.PRIVATE, "private_field")
                    .addField(Modifier.PUBLIC, "publicField")
                    .addField(Modifier.PROTECTED, "защищенное поле")
                .endObject()
                .end();

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("создание сложного объекта с псевдонимом")
    void objectDiagramWithFieldAndAlias() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object \"test object\" as obj {\n" +
                "simple field\n" +
                "}\n" +
                "@enduml\n";

        // when
        var actual = builder
                .start()
                .startObject("test object")
                    .alias("obj")
                    .addField("simple field")
                .endObject()
                .end();

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("объектная диаграмма с простой вертикальной связью")
    void objectDiagramWithSimpleVerticalDependency() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object test {\n" +
                "simple field\n" +
                "}\n" +
                "test --> test2\n" +
                "object test2\n" +
                "@enduml\n";

        // when
        var objectDiagram = builder.start();

        var testObj = objectDiagram
                .startObject("test")
                .addField("simple field");

        var test2 = objectDiagram.startObject("test2");
        testObj.verticalRelateTo(test2);

        testObj.endObject();
        test2.endObject();

        var actual = builder.end();

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("объектная диаграмма с простой горизонтальной связью")
    void objectDiagramWithSimpleHorizontalDependency() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object test {\n" +
                "simple field\n" +
                "}\n" +
                "test -> test2\n" +
                "object test2\n" +
                "@enduml\n";

        // when
        var objectDiagram = builder.start();

        var testObj = objectDiagram
                .startObject("test")
                .addField("simple field");

        var test2 = objectDiagram.startObject("test2");
        testObj.horizontalRelateTo(test2);

        testObj.endObject();
        test2.endObject();

        var actual = builder.end();

        // then
        assertEquals(expected, actual);
    }
}