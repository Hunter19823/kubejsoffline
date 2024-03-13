package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonObject;

public enum JSONProperty {
    // Compression Tag Properties
    @Deprecated
    VERSION("v"),

    // NEW DATA TAG Properties
    @Deprecated
    TYPE_NAME("type_name"),
    TYPE_VARIABLES("type_variables"),
    TYPE_VARIABLE_NAME("type_variable_name"),
    TYPE_VARIABLE_BOUNDS("type_bounds"),

    @Deprecated
    ARRAY_TYPE("array_type"),
    @Deprecated
    ARRAY_NAME("array_name"),

    WILDCARD_TYPE("wildcard_type"),
    WILDCARD_UPPER_BOUNDS("wildcard_type"),
    WILDCARD_LOWER_BOUNDS("wildcard_lower_bounds"),

    @Deprecated
    CLASS_REFERENCE("class_reference"),
    @Deprecated
    CLASS_PARAMETERIZED_ARGUMENTS("class_parameterized_arguments"),
    CLASS_NAME("class_name"),


    // DATA TAG Properties
    @Deprecated
    TYPE_ID("type_id"),
    @Deprecated
    TYPE_IDENTIFIER("type_identifier"),
    RAW_CLASS("raw_class"),
    @Deprecated
    OUTER_CLASS("outer_class"),
    SUPER_CLASS("super_class"),
    @Deprecated
    GENERIC_SUPER_CLASS("S"),
    INTERFACES("interfaces"),
    @Deprecated
    GENERIC_INTERFACES("I"),
    PACKAGE_NAME("package_name"),
    ANNOTATIONS("annotations"),
    MODIFIERS("modifiers"),
    CONSTRUCTORS("constructors"),
    FIELDS("fields"),
    METHODS("methods"),
    PARAMETERS("parameters"),
    @Deprecated
    VALUE("raw_value"),

    @Deprecated
    ARRAY_DEPTH("depth"),
    @Deprecated
    PARAMETERIZED_ARGUMENTS(PARAMETERS.jsName),
    RAW_PARAMETERIZED_TYPE("raw_parameterized_type"),
    OWNER_TYPE("owner_type"),

    PARAMETER_NAME("parameter_name"),
    PARAMETER_TYPE("parameter_type"),
    @Deprecated
    PARAMETER_ANNOTATIONS("parameter_annotations"),

    @Deprecated
    CONSTRUCTOR_ANNOTATIONS(ANNOTATIONS.jsName),

    METHOD_NAME("method_name"),
    @Deprecated
    METHOD_ANNOTATIONS(ANNOTATIONS.jsName),
    METHOD_RETURN_TYPE("method_return_type"),

    FIELD_NAME("field_name"),
    @Deprecated
    FIELD_ANNOTATIONS(ANNOTATIONS.jsName),
    FIELD_TYPE("field_type"),
    @Deprecated
    FIELD_VALUE(VALUE.jsName),


    @Deprecated
    EXECUTABLE_VALUE(VALUE.jsName),
    @Deprecated
    EXECUTABLE_NAME("name"),
    @Deprecated
    EXECUTABLE_ANNOTATIONS(ANNOTATIONS.jsName),
    @Deprecated
    EXECUTABLE_TYPE(TYPE_ID.jsName),

    ANNOTATION_TYPE("annotation_type"),
    ANNOTATION_STRING("annotation_string"),

    BINDING_TYPE("btype"),

    BINDING_TYPE_CLASS("class"),
    BINDING_TYPE_ENUM("enum"),
    BINDING_TYPE_MAP("map"),
    BINDING_TYPE_PRIMITIVE("primitive"),
    BINDING_STRING("s"),
    BINDING_FUNCTION("f"),
    BINDING_OBJECT("o"),
    EXCEPTIONS("exceptions"),
    INNER_CLASSES("inner_classes"),
    ENCLOSING_CLASS("enclosing_class"),

    DECLARING_CLASS("declaring_class");

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
