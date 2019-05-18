package ru.sherb.archchecker.uml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author maksim
 * @since 27.04.19
 */
public class ObjectBuilderTest {

    @Test
    @DisplayName("создание простого объекта")
    public void simpleObjectDiagram() {
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
    public void objectDiagramWithAlias() {
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
    @DisplayName("нелегальные символы в имени будут заменены на '_', если не указан псевдоним")
    public void objectDiagramWithoutAliasAndCompositeName() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();

        // when
        var actual = builder
                .start()
                .startObject("test object")
                .endObject()
                .end();

        // then
        assertEquals("@startuml\nobject test_object\n@enduml\n", actual);
    }

    @Test
    @DisplayName("создание сложного объекта с разнотипными полями")
    public void objectDiagramWithSeveralFields() {
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
    public void objectDiagramWithFieldAndAlias() {
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
    @DisplayName("объектная диаграмма с простой связью")
    public void objectDiagramWithSimpleDependency() {
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
        testObj.relateTo(test2);

        testObj.endObject();
        test2.endObject();

        var actual = builder.end();

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("объектная диаграмма с двумя связями")
    public void objectDiagramWithTwoDependency() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram();
        var expected = "@startuml\n" +
                "object test {\n" +
                "simple field\n" +
                "}\n" +
                "test -> test2\n" +
                "object test2\n" +
                "test2 -> test\n" +
                "@enduml\n";

        // when
        var objectDiagram = builder.start();

        var test = objectDiagram
                .startObject("test")
                .addField("simple field");

        var test2 = objectDiagram.startObject("test2");
        test.relateTo(test2);
        test2.relateTo(test);

        test.endObject();
        test2.endObject();

        var actual = builder.end();

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("горизонтальная связь между объектами на одной линии и вертикальная между ними")
    public void objectDiagramHorizontalRelationInOneLineAndVerticalBetweenLines() {
        // setup
        var builder = PlantUMLBuilder.newObjectDiagram().start();
        builder.positioner = new ObjectAutoPositioner(3);

        var expected = "@startuml\n" +
                "object test\n" +
                "test -> test2\n" +
                "test -> test3\n" +
                "test --> test4\n" +
                "object test2\n" +
                "test2 --> test4\n" +
                "object test3\n" +
                "object test4\n" +
                "test4 --> test2\n" +
                "test4 --> test3\n" +
                "@enduml\n";

        // when
        var test = builder.startObject("test");
        var test2 = builder.startObject("test2");
        var test3 = builder.startObject("test3");
        var test4 = builder.startObject("test4");

        test.relateTo(test2);
        test.relateTo(test3);
        test.relateTo(test4);
        test.endObject();

        test2.relateTo(test4);
        test2.endObject();

        test3.endObject();

        test4.relateTo(test2);
        test4.relateTo(test3);
        test4.endObject();

        var actual = builder.end();

        // then
        assertEquals(expected, actual);
    }
}