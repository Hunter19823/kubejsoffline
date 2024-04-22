package pie.ilikepiefoo.kubejsoffline.api.identifier;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;

public interface Index extends Comparable<Index>, JSONSerializable {
    default int compareTo(Index o) {
        return Integer.compare(getArrayIndex(), o.getArrayIndex());
    }

    int getArrayIndex();
}
