package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;

import java.util.NavigableMap;

public interface Parameters extends JSONSerializable {
    NavigableMap<ParameterID, ParameterData> getAllParameters();

    ParameterID addParameter(ParameterData data);

    boolean contains(ParameterData data);

    ParameterID getID(ParameterData data);

    ParameterData getParameter(ParameterID id);

    void clear();

}
