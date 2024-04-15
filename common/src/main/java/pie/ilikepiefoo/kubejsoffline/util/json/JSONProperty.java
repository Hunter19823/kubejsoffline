package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonObject;

public enum JSONProperty {
    TYPE("t"),
    NAME("N"),
    TYPE_VARIABLES("v"),
    TYPE_VARIABLE_NAME("V"),
    TYPE_VARIABLE_BOUNDS("b"),

    WILDCARD_UPPER_BOUNDS("u"),
    WILDCARD_LOWER_BOUNDS("l"),

    CLASS_NAME(NAME.jsName),


    SUPER_CLASS("s"),
    INTERFACES("i"),
    PACKAGE_NAME("P"),
    ANNOTATIONS("a"),
    MODIFIERS("M"),
    CONSTRUCTORS("c"),
    FIELDS("f"),
    METHODS("m"),
    PARAMETERS("p"),
    RAW_PARAMETERIZED_TYPE("r"),
    OWNER_TYPE("o"),

    PARAMETER_NAME(NAME.jsName),
    PARAMETER_TYPE("t"),

    METHOD_NAME(NAME.jsName),
    METHOD_RETURN_TYPE(TYPE.jsName),

    FIELD_NAME(NAME.jsName),
    FIELD_TYPE(TYPE.jsName),

    ANNOTATION_TYPE(TYPE.jsName),
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
