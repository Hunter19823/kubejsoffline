package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public class WildcardData extends TypeData {
    protected TypeData wildcardType;

    public WildcardData( String name, TypeData wildcardType ) {
        super(name);
        this.wildcardType = wildcardType;
    }

    public TypeData getWildcardType() {
        return wildcardType;
    }

    @Override
    protected boolean isWildcard() {
        return true;
    }

	@Override
	public JsonObject toReference() {
		JsonObject object = super.toReference();
		object.add(JSONProperty.WILDCARD_TYPE.jsName, getWildcardType().toReference());
		return object;
	}
}
