package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public abstract class TypeData implements JSONLike {
    protected String name;

    protected TypeData(String name) {
        this.name = name;
    }

    @Override
    public JsonElement toJSON() {
        JsonObject object = new JsonObject();

        if (name != null) {
            object.addProperty(JSONProperty.EXECUTABLE_NAME.jsName, name);
        }

        return object;
    }

    public JsonObject toReference() {
        JsonObject object = new JsonObject();
        object.addProperty(JSONProperty.TYPE_NAME.jsName, getName());
        return object;
    }

    public String getName() {
        return name;
    }

    private void populate() {

    }

}
