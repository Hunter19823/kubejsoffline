package pie.ilikepiefoo.kubejsoffline.impl.collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.api.collection.Types;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.TypeManager;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.ArrayIdentifier;

import java.util.NavigableMap;
import java.util.TreeMap;

public class TypesWrapper implements Types {
    protected TwoWayMap<TypeOrTypeVariableID, TypeData> data;
    protected NavigableMap<TypeID, RawClassData> rawTypes = new TreeMap<>();
    protected NavigableMap<TypeID, ParameterizedTypeData> parameterizedTypes = new TreeMap<>();
    protected NavigableMap<TypeID, WildcardTypeData> wildcardTypes = new TreeMap<>();
    protected NavigableMap<TypeVariableID, TypeData> typeVariables = new TreeMap<>();


    public TypesWrapper() {
        this.data = new TwoWayMap<>(TypeIdentifier::new);
    }

    @Override
    public NavigableMap<TypeOrTypeVariableID, TypeData> getAllTypes() {
        return this.data.getIndexToValueMap();
    }

    @Override
    public NavigableMap<TypeID, RawClassData> getAllRawTypes() {
        return this.rawTypes;
    }

    @Override
    public NavigableMap<TypeID, ParameterizedTypeData> getAllParameterizedTypes() {
        return this.parameterizedTypes;
    }

    @Override
    public NavigableMap<TypeID, WildcardTypeData> getAllWildcardTypes() {
        return this.wildcardTypes;
    }

    @Override
    public NavigableMap<TypeVariableID, TypeData> getAllTypeVariables() {
        return this.typeVariables;
    }

    @Override
    public synchronized TypeOrTypeVariableID addType(TypeData data) {
        if (this.data.contains(data)) {
            return this.data.get(data);
        }
        var index = this.data.add(data,
                (data.isTypeVariable()) ?
                        TypeVariableIdentifier::new :
                        TypeIdentifier::new
        );
        if (data.isRawType()) {
            this.rawTypes.put(index.asType(), data.asRawType());
        } else if (data.isParameterizedType()) {
            this.parameterizedTypes.put(index.asType(), data.asParameterizedType());
        } else if (data.isWildcardType()) {
            this.wildcardTypes.put(index.asType(), data.asWildcardType());
        } else if (data.isTypeVariable()) {
            this.typeVariables.put(index.asTypeVariable(), data);
        }
        return index;
    }

    @Override
    public boolean contains(TypeData data) {
        return this.data.contains(data);
    }

    @Override
    public TypeOrTypeVariableID getID(TypeData type) {
        if (type.isRawType()) {
            return getID(type.asRawType());
        } else if (type.isParameterizedType()) {
            return getID(type.asParameterizedType());
        } else if (type.isWildcardType()) {
            return getID(type.asWildcardType());
        } else if (type.isTypeVariable()) {
            return getID(type.asTypeVariable());
        }
        throw new UnsupportedOperationException("Unsupported TypeData.");
    }

    @Override
    public TypeID getID(RawClassData type) {
        return this.data.get(type).asType();
    }

    @Override
    public TypeID getID(ParameterizedTypeData type) {
        return this.data.get(type).asType();
    }

    @Override
    public TypeID getID(WildcardTypeData type) {
        return this.data.get(type).asType();
    }

    @Override
    public TypeVariableID getID(TypeVariableData type) {
        return this.data.get(type).asTypeVariable();
    }

    @Override
    public TypeData getType(TypeID id) {
        return this.data.get(id);
    }

    @Override
    public void clear() {
        this.data.clear();
        this.rawTypes.clear();
        this.parameterizedTypes.clear();
        this.wildcardTypes.clear();
        this.typeVariables.clear();
        TypeManager.INSTANCE.clear();
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonArray();
        while (json.size() != this.data.size()) {
            json.add(this.data.get(new TypeIdentifier(json.size())).toJSON());
        }
        return json;
    }

    public static class TypeIdentifier extends ArrayIdentifier implements TypeID {

        public TypeIdentifier(int arrayIndex) {
            super(arrayIndex);
        }

        public TypeIdentifier(int arrayIndex, int arrayDepth) {
            super(arrayIndex, arrayDepth);
        }
    }

    public static class TypeVariableIdentifier extends ArrayIdentifier implements TypeVariableID {

        public TypeVariableIdentifier(int arrayIndex) {
            super(arrayIndex);
        }

        public TypeVariableIdentifier(int arrayIndex, int arrayDepth) {
            super(arrayIndex, arrayDepth);
        }
    }
}
