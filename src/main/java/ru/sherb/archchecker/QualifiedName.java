package ru.sherb.archchecker;

import java.util.Arrays;

/**
 * @author maksim
 * @since 25.04.19
 */
public final class QualifiedName {

    private final String[] pieces;

    public QualifiedName(String name) {
        this.pieces = name.split("\\.");
    }

    private QualifiedName(String[] pieces) {
        assert pieces != null && pieces.length != 0;

        this.pieces = pieces;
    }

    public QualifiedName pkg() {
        return new QualifiedName(Arrays.copyOf(pieces, pieces.length - 1));
    }

    public String simpleName() {
        return this.pieces[this.pieces.length - 1];
    }

    public String mainPackage() {
        return pieces[2]; //TODO workaround: only for projects whose groupId consist of two package, e.g. ru.sherb
    }

    public QualifiedName newWith(String name) {
        var newPieces = Arrays.copyOf(pieces, pieces.length + 1);
        newPieces[newPieces.length - 1] = name;
        return new QualifiedName(newPieces);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualifiedName that = (QualifiedName) o;
        return Arrays.equals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pieces);
    }

    @Override
    public String toString() {
        return String.join(".", pieces);
    }
}
