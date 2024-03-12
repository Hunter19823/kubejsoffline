package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Parameters;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.NavigableMap;

public class ParametersWrapper extends WrapperBase<ParameterID, ParameterData> implements Parameters {

    public ParametersWrapper() {
        super(ParameterIdentifier::new);
    }

    @Override
    public NavigableMap<ParameterID, ParameterData> getAllParameters() {
        return this.indexToValueMap;
    }

    @Override
    public synchronized ParameterID addParameter(ParameterData data) {
        return this.addValue(data);
    }

    @Override
    public ParameterID getID(ParameterData data) {
        return this.valueToIndexMap.get(data);
    }

    @Override
    public ParameterData getParameter(ParameterID id) {
        return this.indexToValueMap.get(id);
    }

    public static class ParameterIdentifier extends IdentifierBase implements ParameterID {
        public ParameterIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
