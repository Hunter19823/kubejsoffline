package pie.ilikepiefoo.kubejsoffline.util;

import java.util.Objects;

public record Relation(Class<?> from, RelationType relation, Class<?> to) {
    public Relation {
        Objects.requireNonNull(from);
        Objects.requireNonNull(relation);
        Objects.requireNonNull(to);
    }

    /**
     * Returns a hash code value for the record.
     * Obeys the general contract of {@link Object#hashCode Object.hashCode}.
     * For records, hashing behavior is constrained by the refined contract
     * of {@link Record#equals Record.equals}, so that any two records
     * created from the same components must have the same hash code.
     *
     * @return a hash code value for this record.
     * @implSpec The implicitly provided implementation returns a hash code value derived
     * by combining appropriate hashes from each component.
     * The precise algorithm used in the implicitly provided implementation
     * is unspecified and is subject to change within the above limits.
     * The resulting integer need not remain consistent from one
     * execution of an application to another execution of the same
     * application, even if the hashes of the component values were to
     * remain consistent in this way.  Also, a component of primitive
     * type may contribute its bits to the hash code differently than
     * the {@code hashCode} of its primitive wrapper class.
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(from, relation, to);
    }
}
