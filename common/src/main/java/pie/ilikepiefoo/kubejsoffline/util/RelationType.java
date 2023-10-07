package pie.ilikepiefoo.kubejsoffline.util;

import com.google.gson.JsonArray;

public enum RelationType {
    UNKNOWN,
    COMPONENT_OF,
    INNER_TYPE_OF,
    SUPER_CLASS_OF,
    GENERIC_SUPER_CLASS_OF,
    IMPLEMENTATION_OF,
    GENERIC_IMPLEMENTATION_OF,
    NEST_MEMBER_OF,
    NEST_HOST_OF,
    ANNOTATION_OF,
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
    CONSTRUCTOR_GENERIC_EXCEPTION_TYPE_OF;

    public static JsonArray getRelationTypeData() {
        JsonArray out = new JsonArray();
        for (RelationType relationType : RelationType.values()) {
            out.add(relationType.name());
        }
        return out;
    }

    public String getKeyName() {
        return String.valueOf(this.ordinal());
    }
}
