package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonObject;

public enum JSONProperty {
	// Compression Tag Properties
	VERSION("v"),


	// DATA TAG Properties
	TYPE_ID("T"),
	TYPE_IDENTIFIER("t"),
	BASE_CLASS_NAME("n"),
	SUPER_CLASS("s"),
	GENERIC_SUPER_CLASS("S"),
	INTERFACES("i"),
	GENERIC_INTERFACES("I"),
	PACKAGE_NAME("P"),
	ANNOTATIONS("a"),
	MODIFIERS("M"),
	CONSTRUCTORS("c"),
	FIELDS("f"),
	METHODS("m"),
	PARAMETERS("p"),

	ARRAY_DEPTH("d"),
	PARAMETERIZED_ARGUMENTS(PARAMETERS.jsName),
	RAW_PARAMETERIZED_TYPE("r"),
	OWNER_TYPE("o"),

	PARAMETER_NAME(BASE_CLASS_NAME.jsName),
	PARAMETER_TYPE(TYPE_ID.jsName),
	PARAMETER_ANNOTATIONS(ANNOTATIONS.jsName),

	CONSTRUCTOR_NAME(BASE_CLASS_NAME.jsName),
	CONSTRUCTOR_ANNOTATIONS(ANNOTATIONS.jsName),

	METHOD_NAME(BASE_CLASS_NAME.jsName),
	METHOD_ANNOTATIONS(ANNOTATIONS.jsName),
	METHOD_RETURN_TYPE(TYPE_ID.jsName),

	FIELD_NAME(BASE_CLASS_NAME.jsName),
	FIELD_ANNOTATIONS(ANNOTATIONS.jsName),
	FIELD_TYPE(TYPE_ID.jsName),

	ANNOTATION_TYPE(TYPE_ID.jsName),
	ANNOTATION_STRING("s"),

	BINDING_TYPE("btype"),

	BINDING_TYPE_CLASS("class"),
	BINDING_TYPE_ENUM("enum"),
	BINDING_TYPE_MAP("map"),
	BINDING_TYPE_PRIMITIVE("primitive"),
	BINDING_STRING("s"),
	BINDING_FUNCTION("f"),
	BINDING_OBJECT("o"),
	;
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
