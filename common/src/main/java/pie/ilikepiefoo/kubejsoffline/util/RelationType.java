package pie.ilikepiefoo.kubejsoffline.util;

import com.google.gson.JsonArray;

public enum RelationType {
    UNKNOWN,
    TYPE_VARIABLE_OF,
    COMPONENT_OF,
    INNER_TYPE_OF,
    SUPER_CLASS_OF,
    IMPLEMENTATION_OF,
    ANNOTATION_OF,
    DECLARED_FIELD_TYPE_OF,
    DECLARED_METHOD_RETURN_TYPE_OF,
    DECLARED_METHOD_PARAMETER_TYPE_OF,
    DECLARED_METHOD_EXCEPTION_TYPE_OF,
    CONSTRUCTOR_PARAMETER_TYPE_OF,
    CONSTRUCTOR_EXCEPTION_TYPE_OF,
    ;

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
