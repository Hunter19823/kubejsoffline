package pie.ilikepiefoo.kubejsoffline.api.identifier;

public interface Index extends Comparable<Index> {
    int getArrayIndex();

    default int compareTo(Index o) {
        return Integer.compare(getArrayIndex(), o.getArrayIndex());
    }
}
