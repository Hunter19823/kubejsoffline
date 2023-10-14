package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public class ArrayTypeData extends TypeData {
    protected TypeData type;
    protected int depth;

    public ArrayTypeData(String name, TypeData type, int depth) {
        super(name);
        this.type = type;
        this.depth = depth;
    }

    public TypeData getType() {
        return type;
    }

    @Override
    public JsonObject toReference() {
        JsonObject object = type.toReference();
        object.addProperty(JSONProperty.ARRAY_DEPTH.jsName, getDepth());
        return object;
    }

    public int getDepth() {
        return depth;
    }
}
