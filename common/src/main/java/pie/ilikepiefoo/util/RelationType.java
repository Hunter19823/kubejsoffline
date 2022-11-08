package pie.ilikepiefoo.util;

import com.google.gson.JsonObject;

public enum RelationType {
	UNKNOWN,
	COMPONENT_OF,
	INNER_TYPE_OF,
	SUPER_CLASS_OF,
	PERMITTED_SUBCLASS_OF,
	GENERIC_CLASS_PARAMETER_OF,
	DECLARED_FIELD_TYPE_OF,
	DECLARED_GENERIC_FIELD_TYPE_OF,
	DECLARED_METHOD_RETURN_TYPE_OF,
	DECLARED_METHOD_PARAMETER_TYPE_OF,
	DECLARED_METHOD_EXCEPTION_TYPE_OF,
	DECLARED_GENERIC_METHOD_RETURN_TYPE_OF,
	DECLARED_GENERIC_METHOD_PARAMETER_TYPE_OF,
	DECLARED_GENERIC_METHOD_EXCEPTION_TYPE_OF,
	CONSTRUCTOR_PARAMETER_TYPE_OF,
	CONSTRUCTOR_EXCEPTION_TYPE_OF,
	CONSTRUCTOR_GENERIC_PARAMETER_TYPE_OF,
	CONSTRUCTOR_GENERIC_EXCEPTION_TYPE_OF
	;

	public static JsonObject getRelationTypeData() {
		JsonObject out = new JsonObject();
		for (RelationType relationType : RelationType.values()) {
			out.addProperty(relationType.name(), relationType.ordinal());
		}
		return out;
	}

	public String getKeyName() {
		return this.name();
	}
}
