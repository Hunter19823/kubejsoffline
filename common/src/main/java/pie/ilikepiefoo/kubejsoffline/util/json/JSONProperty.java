package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonObject;

public enum JSONProperty {
    // Compression Tag Properties
    VERSION("v"),

    // NEW DATA TAG Properties
    TYPE_NAME("type_name"),
    TYPE_VARIABLES("type_variables"),
    TYPE_VARIABLE_NAME("type_variable_name"),
    TYPE_VARIABLE_BOUNDS("type_bounds"),

    ARRAY_TYPE("array_type"),
    ARRAY_NAME("array_name"),

    WILDCARD_TYPE("wildcard_type"),
    WILDCARD_UPPER_BOUNDS("wildcard_type"),
    WILDCARD_LOWER_BOUNDS("wildcard_lower_bounds"),

    CLASS_REFERENCE("class_reference"),
    CLASS_PARAMETERIZED_ARGUMENTS("class_parameterized_arguments"),
    CLASS_NAME("class_name"),


    // DATA TAG Properties
    TYPE_ID("id"),
    TYPE_IDENTIFIER("type_identifier"),
    RAW_CLASS("raw_class"),
    OUTER_CLASS("outer_class"),
    SUPER_CLASS("super_class"),
    GENERIC_SUPER_CLASS("S"),
    INTERFACES("i"),
    GENERIC_INTERFACES("I"),
    PACKAGE_NAME("P"),
    ANNOTATIONS("annotations"),
    MODIFIERS("modifiers"),
    CONSTRUCTORS("constructors"),
    FIELDS("fields"),
    METHODS("methods"),
    PARAMETERS("parameters"),
    VALUE("raw_value"),

    ARRAY_DEPTH("depth"),
    PARAMETERIZED_ARGUMENTS(PARAMETERS.jsName),
    RAW_PARAMETERIZED_TYPE("r"),
    OWNER_TYPE("o"),

    PARAMETER_NAME(RAW_CLASS.jsName),
    PARAMETER_TYPE(TYPE_ID.jsName),
    PARAMETER_ANNOTATIONS(ANNOTATIONS.jsName),

    CONSTRUCTOR_NAME(RAW_CLASS.jsName),
    CONSTRUCTOR_ANNOTATIONS(ANNOTATIONS.jsName),

    METHOD_NAME(RAW_CLASS.jsName),
    METHOD_ANNOTATIONS(ANNOTATIONS.jsName),
    METHOD_RETURN_TYPE(TYPE_ID.jsName),

    FIELD_NAME(RAW_CLASS.jsName),
    FIELD_ANNOTATIONS(ANNOTATIONS.jsName),
    FIELD_TYPE(TYPE_ID.jsName),
    FIELD_VALUE(VALUE.jsName),


    EXECUTABLE_VALUE(VALUE.jsName),
    EXECUTABLE_NAME("name"),
    EXECUTABLE_ANNOTATIONS(ANNOTATIONS.jsName),
    EXECUTABLE_TYPE(TYPE_ID.jsName),

    ANNOTATION_TYPE(TYPE_ID.jsName),
    ANNOTATION_STRING("s"),

    BINDING_TYPE("btype"),

    BINDING_TYPE_CLASS("class"),
    BINDING_TYPE_ENUM("enum"),
    BINDING_TYPE_MAP("map"),
    BINDING_TYPE_PRIMITIVE("primitive"),
    BINDING_STRING("s"),
    BINDING_FUNCTION("f"),
    BINDING_OBJECT("o");

    public final String jsName;

    JSONProperty(final String jsName) {
        this.jsName = jsName;
    }

    public static JsonObject createTranslation() {
        final JsonObject object = new JsonObject();
        for (final JSONProperty property : values()) {
            object.addProperty(property.name(), property.jsName);
        }
        return object;
    }
}
