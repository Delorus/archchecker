package ru.sherb.archchecker.uml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maksim
 * @since 09.05.19
 */
final class ObjectAutoPositioner {

    private final int lineSize;

    private final Map<String, ObjectWithPosition> objectRefs = new HashMap<>();
    private Point currentPos = new Point(0, 0);

    ObjectAutoPositioner(int lineSize) {
        assert lineSize > 0;

        this.lineSize = lineSize;
    }

    void addObject(Object object) {
        assert !objectRefs.containsKey(object.ref());

        currentPos = currentPos.nextColumn();

        var obj = new ObjectWithPosition(object, currentPos);

        objectRefs.put(obj.ref(), obj);

        if (currentPos.column == lineSize) {
            currentPos = currentPos.newLine();
        }
    }

    RelationDirection directionBetween(Object obj1, Object obj2) {
        assert objectRefs.containsKey(obj1.ref()) && objectRefs.containsKey(obj2.ref());

        var position1 = objectRefs.get(obj1.ref());
        var position2 = objectRefs.get(obj2.ref());

        if (position1.line() == position2.line()) {
            return RelationDirection.HORIZONTAL;
        } else {
            return RelationDirection.VERTICAL;
        }
    }

    private static class ObjectWithPosition {
        private final Object object;
        private final Point position;

        private ObjectWithPosition(Object object, Point position) {
            this.object = object;
            this.position = position;
        }

        String ref() {
            return object.ref();
        }

        public int line() {
            return position.line;
        }
    }

    private static class Point {
        public final int column;
        public final int line;

        Point(int column, int line) {
            this.column = column;
            this.line = line;
        }

        Point nextColumn() {
            return new Point(column + 1, line);
        }

        Point newLine() {
            return new Point(0, line + 1);
        }
    }
}
