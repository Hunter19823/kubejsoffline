package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public abstract class TypeData implements JSONLike {
    protected String name;

    protected TypeData( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected boolean isWildcard() {
        return false;
    }

    protected boolean isTypeVariable() {
        return false;
    }

    protected boolean isArray() {
        return false;
    }

    protected boolean isParameterized() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }

    protected boolean isClass() {
        return false;
    }

    protected boolean isInterface() {
        return false;
    }

    protected boolean isEnum() {
        return false;
    }

    protected boolean isAnnotation() {
        return false;
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

}
