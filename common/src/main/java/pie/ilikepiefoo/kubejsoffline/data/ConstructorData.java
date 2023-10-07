package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConstructorData extends CommonData {
    public ConstructorData(int modifiers) {
        super(null, modifiers);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("modifiers", getModifiers());
        if (!getParameters().isEmpty()) {
            object.add("parameters", ParameterData.toJSON(getParameters()));
        }
        if (!getAnnotations().isEmpty()) {
            object.add("annotations", AnnotationData.toJSON(getAnnotations()));
        }
        return object;
    }
}
