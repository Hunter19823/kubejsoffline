package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.util.Arrays;
import java.util.List;

public class TypeVariableData extends TypeData {

    protected String variableName;
    protected List<TypeData> bounds;

    public TypeVariableData(String name, String variableName) {
        super(name);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public TypeVariableData addBounds(TypeData... bounds) {
        if (this.bounds == null) {
            this.bounds = new java.util.ArrayList<>();
        }
        this.bounds.addAll(Arrays.asList(bounds));
        return this;
    }

    @Override
    public JsonObject toReference() {
        JsonObject object = super.toReference();
        JsonArray bounds = new JsonArray();
        for (TypeData bound : getBounds()) {
            bounds.add(bound.toReference());
        }
        object.add(JSONProperty.TYPE_VARIABLE_BOUNDS.jsName, bounds);
        return object;
    }

    public List<TypeData> getBounds() {
        if (this.bounds == null) {
            return List.of();
        }
        return bounds;
    }
}
