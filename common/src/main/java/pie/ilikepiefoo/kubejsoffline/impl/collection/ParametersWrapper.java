package pie.ilikepiefoo.kubejsoffline.impl.collection;

import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.collection.Parameters;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.NavigableMap;

public class ParametersWrapper implements Parameters {
    protected final TwoWayMap<ParameterID, ParameterData> data = new TwoWayMap<>(ParameterIdentifier::new);

    @Override
    public NavigableMap<ParameterID, ParameterData> getAllParameters() {
        return this.data.getIndexToValueMap();
    }

    @Override
    public synchronized ParameterID addParameter(ParameterData data) {
        var index = this.data.add(data);
        data.setIndex(index);
        return index;
    }

    @Override
    public boolean contains(ParameterData data) {
        return this.data.contains(data);
    }

    @Override
    public ParameterID getID(ParameterData data) {
        return this.data.get(data);
    }

    @Override
    public ParameterData getParameter(ParameterID id) {
        return this.data.get(id);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @Override
    public JsonElement toJSON() {
        return JSONSerializable.of(this.data.getValues());
    }

    public static class ParameterIdentifier extends IdentifierBase implements ParameterID {
        public ParameterIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
