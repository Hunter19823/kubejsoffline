package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonObject;

public enum JSONProperty {
    // Compression Tag Properties
    @Deprecated
    VERSION("v"),

    // NEW DATA TAG Properties
    @Deprecated
    TYPE_NAME("type_name"),
    TYPE_VARIABLES("v"),
    TYPE_VARIABLE_NAME("V"),
    TYPE_VARIABLE_BOUNDS("b"),

    @Deprecated
    ARRAY_TYPE("array_type"),
    @Deprecated
    ARRAY_NAME("array_name"),

    WILDCARD_TYPE("w"),
    WILDCARD_UPPER_BOUNDS("u"),
    WILDCARD_LOWER_BOUNDS("l"),

    @Deprecated
    CLASS_REFERENCE("class_reference"),
    @Deprecated
    CLASS_PARAMETERIZED_ARGUMENTS("class_parameterized_arguments"),
    CLASS_NAME("n"),


    // DATA TAG Properties
    @Deprecated
    TYPE_ID("type_id"),
    @Deprecated
    TYPE_IDENTIFIER("type_identifier"),
    RAW_CLASS("r"),
    @Deprecated
    OUTER_CLASS("outer_class"),
    SUPER_CLASS("s"),
    @Deprecated
    GENERIC_SUPER_CLASS("S"),
    INTERFACES("i"),
    @Deprecated
    GENERIC_INTERFACES("I"),
    PACKAGE_NAME("P"),
    ANNOTATIONS("a"),
    MODIFIERS("M"),
    CONSTRUCTORS("c"),
    FIELDS("f"),
    METHODS("m"),
    PARAMETERS("p"),
    @Deprecated
    VALUE("raw_value"),

    @Deprecated
    ARRAY_DEPTH("depth"),
    @Deprecated
    PARAMETERIZED_ARGUMENTS(PARAMETERS.jsName),
    RAW_PARAMETERIZED_TYPE("r"),
    OWNER_TYPE("o"),

    PARAMETER_NAME("n"),
    PARAMETER_TYPE("t"),
    @Deprecated
    PARAMETER_ANNOTATIONS("parameter_annotations"),

    @Deprecated
    CONSTRUCTOR_ANNOTATIONS(ANNOTATIONS.jsName),

    METHOD_NAME("n"),
    @Deprecated
    METHOD_ANNOTATIONS(ANNOTATIONS.jsName),
    METHOD_RETURN_TYPE("t"),

    FIELD_NAME("n"),
    @Deprecated
    FIELD_ANNOTATIONS(ANNOTATIONS.jsName),
    FIELD_TYPE("t"),
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

    ANNOTATION_TYPE("t"),
    ANNOTATION_STRING("s"),

    BINDING_TYPE("btype"),

    BINDING_TYPE_CLASS("class"),
    BINDING_TYPE_ENUM("enum"),
    BINDING_TYPE_MAP("map"),
    BINDING_TYPE_PRIMITIVE("primitive"),
    BINDING_STRING("s"),
    BINDING_FUNCTION("f"),
    BINDING_OBJECT("o"),
    EXCEPTIONS("e"),
    INNER_CLASSES("I"),
    ENCLOSING_CLASS("E"),

    DECLARING_CLASS("D");

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
