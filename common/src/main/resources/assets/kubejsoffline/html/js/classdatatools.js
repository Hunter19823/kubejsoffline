function exists(thing) {
	return thing !== null && thing !== undefined;
}

function getAnySuperClass(id) {
	let data = null;
	if (!exists(id)) {
		return null;
	}

	switch (typeof (id)) {
		case "number":
			data = getClass(id).data;
			break;
		case "object":
			if (exists(id['data'])) {
				data = id.data;
			} else {
				data = id;
			}
			break;
		default:
			console.error("Invalid class id passed to getAnySuperClass: " + id);
			return null;
	}

	if (!exists(data)) {
		return null;
	}

	if (exists(data[PROPERTY.SUPER_CLASS])) {
		return data[PROPERTY.SUPER_CLASS];
	}

	if (exists(data[PROPERTY.GENERIC_SUPER_CLASS])) {
		return data[PROPERTY.GENERIC_SUPER_CLASS];
	}

	return null;
}

const LOOK_UP_CACHE = new Map();

function getClass(id) {
	let output = {};
	if (!exists(id)) {
		console.error("Invalid class id: " + id);
		return null;
	}
	switch (typeof (id)) {
		case "number":
			if (id < 0 || id >= DATA.length) {
				console.error("Invalid class id: " + id);
				return null;
			}
			if (!exists(DATA[id])) {
				console.error("Invalid class data: " + id);
				return null;
			}
			output.data = DATA[id];
			break;
		case "object":
			if (exists(id['data'])) {
				output.data = id.data;
			} else if (exists(id[PROPERTY.TYPE_ID])) {
				output.data = DATA[id[PROPERTY.TYPE_ID]];
			}
			break;
		case "string":
			// See if the string is a number
			let num = parseInt(id);
			if (!isNaN(num)) {
				return getClass(num);
			}
			let lowerID = id.toLowerCase();
			if (LOOK_UP_CACHE.has(lowerID)) {
				return getClass(LOOK_UP_CACHE.get(lowerID));
			}
			// Check if the string matches the java qualified type name regex
			if (!id.match(/([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*/)) {
				// Class does not match a valid java qualified type name, so return null
				console.error("Invalid class id/search: " + id);
				return null;
			}
			// See if the string is a class type
			for (let i = LOOK_UP_CACHE.size; i < DATA.length; i++) {
				let lower = getClass(i).type().toLowerCase();
				LOOK_UP_CACHE.set(lower, i);
				if (lowerID === lower) {
					return getClass(i);
				}
			}
			// See if the string is a class name
			for (let i = 0; i < DATA.length; i++) {
				if (lowerID === getClass(i).name().toLowerCase()) {
					return getClass(i);
				}
			}
			// See if the string is a class simple name
			for (let i = 0; i < DATA.length; i++) {
				if (getClass(i)?.simplename()?.toLowerCase() === lowerID) {
					return getClass(i);
				}
			}

			return null;
		default:
			console.error("Unsupported class type provided to getClass: " + id + " (" + typeof (id) + ")");
			return null;
	}

	if (!exists(output.data)) {
		console.error("Invalid class data: ", id, typeof (id));
	}

	output.id = function () {
		return this.data[PROPERTY.TYPE_ID];
	}

	output.name = function () {
		if (!this.data._name_cache) {
			if (exists(this.data[PROPERTY.BASE_CLASS_NAME])) {
				this.data._name_cache = uncompressString(this.data[PROPERTY.BASE_CLASS_NAME]);
			} else {
				let baseClass = this.baseclass();
				if (baseClass === this.id()) {
					this.data._name_cache = this.simplename();
				} else {
					this.data._name_cache = getClass(baseClass).name();
				}
			}
		}
		return this.data._name_cache;
	}

	output.type = function (seen = new Set()) {
		if (!this.data._type_cache) {
			this._loadType(seen);
		}
		return this.data._type_cache;
	}

	output._loadType = function (seen = new Set()) {
		if (seen.has(this.id())) {
			this.data._type_cache = "...";
			console.error("Circular reference detected while loading type for class " + this.name() + " (" + this.id() + ").");
			seen.forEach((id) => {
				console.error(getClass(id).name() + " (" + id + ")");
			});
			return;
		}
		seen.add(this.id());
		if (exists(this.data[PROPERTY.TYPE_IDENTIFIER])) {
			if (exists(this.data[PROPERTY.OWNER_TYPE]) || exists(this.data[PROPERTY.RAW_PARAMETERIZED_TYPE])) {
				this.data["_oldType"] = this.data[PROPERTY.TYPE_IDENTIFIER];
				delete this.data[PROPERTY.TYPE_IDENTIFIER];
				this._loadType();
				return;
			}
			this.data._type_cache = uncompressString(this.data[PROPERTY.TYPE_IDENTIFIER]);
			return;
		}
		// When the type identifier is not present, it means that the class is either a generic type, parameterized type, or array type
		// In these cases, the base type is stored in the RAW_PARAMETERIZED_TYPE property.
		// We need to build the type identifier from the base type, type parameters, and array dimensions.
		let typeName = null;
		if (exists(this.data[PROPERTY.OWNER_TYPE])) {
			if (!exists(this.data[PROPERTY.BASE_CLASS_NAME])) {
				console.error("Unable to load type for class ", this.data, " (" + this.id() + "). It does not contain the name of the subclass.");
				return;
			}
			let ownerType = getClass(this.data[PROPERTY.OWNER_TYPE]).type(seen);
			let simpleName = uncompressString(this.data[PROPERTY.BASE_CLASS_NAME]);
			typeName = ownerType + "$" + simpleName;
		} else if (!exists(this.data[PROPERTY.RAW_PARAMETERIZED_TYPE])) {
			console.error("Unable to load type for class ", this, " (" + this.id() + "). It does not contain a raw parameterized type or owner type!");
			return;
		} else {
			typeName = getClass(this.data[PROPERTY.RAW_PARAMETERIZED_TYPE]).type(seen);
		}
		let paramArgs = this.paramargs();
		if (exists(paramArgs)) {
			typeName += "<";
			for (let i = 0; i < paramArgs.length; i++) {
				if (i > 0) {
					typeName += ",";
				}
				typeName += getClass(paramArgs[i]).type(seen);
			}
			typeName += ">";
		}
		let arrayDimensions = this.arrayDepth();
		if (arrayDimensions > 0) {
			for (let i = 0; i < arrayDimensions; i++) {
				typeName += "[]";
			}
		}

		this.data._type_cache = typeName;
	}

	output.rawtype = function () {
		return this.data[PROPERTY.RAW_PARAMETERIZED_TYPE];
	}

	output.simplename = function () {
		let fullName = this.type();
		if (this.data._cachedSimpleName) {
			return this.data._cachedSimpleName;
		}
		// For every period character, remove all alphanumeric characters before it and the period itself using regex and a while loop
		let index = fullName.indexOf(".");
		while (index !== -1) {
			fullName = fullName.replace(new RegExp("[a-zA-Z0-9_]*\\."), "");
			index = fullName.indexOf(".");
		}
		// Remove any array brackets that may be present on the end of the type.
		index = fullName.indexOf("[");
		while (index !== -1) {
			fullName = fullName.substring(0, index) + fullName.substring(index + 2);
			index = fullName.indexOf("[");
		}
		this.data._cachedSimpleName = fullName;
		return fullName;
	}

	output.package = function () {
		let pkg = this.data[PROPERTY.PACKAGE_NAME];
		if (this.data._cachedPackageName) {
			return this.data._cachedPackageName;
		}

		if (exists(pkg)) {
			this.data._cachedPackageName = loadPackageName(pkg);
			return this.data._cachedPackageName;
		}

		let fullName = this.type();
		// Remove any Generics
		let index = fullName.indexOf("<");
		if (index !== -1) {
			fullName = fullName.substring(0, index);
		}
		// Remove any array brackets
		index = fullName.indexOf("[");
		if (index !== -1) {
			fullName = fullName.substring(0, index);
		}
		// Remove class name
		index = fullName.lastIndexOf(".");
		if (index !== -1) {
			fullName = fullName.substring(0, index);
		}

		this.data._cachedPackageName = fullName;
		return fullName;
	}

	output.paramargs = function () {
		let args = this.data[PROPERTY.PARAMETERIZED_ARGUMENTS];
		if (!exists(args) || args.length === 0) {
			return null;
		}
		return args;
	}

	output.isGeneric = function () {
		return exists(this.data[PROPERTY.PARAMETERIZED_ARGUMENTS]);
	}

	output.isInnerClass = function () {
		return exists(this.data[PROPERTY.OWNER_TYPE]);
	}

	output.outerclass = function () {
		return this.data[PROPERTY.OWNER_TYPE];
	}

	output.baseclass = function () {
		let output = this.id();
		while (getClass(output).isInnerClass()) {
			output = getClass(output).outerclass();
		}
		while (getClass(output).isParameterizedType()) {
			output = getClass(output).rawtype();
		}
		return output;
	}

	output.isParameterizedType = function () {
		return exists(this.data[PROPERTY.RAW_PARAMETERIZED_TYPE]);
	}

	output.isWildcard = function () {
		return this.name().includes("?");
	}

	output.arrayDepth = function () {
		let depth = this.data[PROPERTY.ARRAY_DEPTH];
		if (!exists(depth)) {
			return 0;
		}
		return depth;
	}

	output.superclass = function () {
		return getAnySuperClass(this.data);
	}

	output.interfaces = function () {
		let interfaces = new Set();
		this._follow_inheritance((data) => {
			if (exists(data[PROPERTY.INTERFACES])) {
				for (let i = 0; i < data[PROPERTY.INTERFACES].length; i++) {
					data[PROPERTY.INTERFACES][i].declaringClass = data[PROPERTY.TYPE_ID];
					interfaces.add(data[PROPERTY.INTERFACES][i]);
				}
			}
			if (exists(data[PROPERTY.GENERIC_INTERFACES])) {
				for (let i = 0; i < data[PROPERTY.GENERIC_INTERFACES].length; i++) {
					data[PROPERTY.GENERIC_INTERFACES][i].declaringClass = data[PROPERTY.TYPE_ID];
					interfaces.add(data[PROPERTY.GENERIC_INTERFACES][i]);
				}
			}
		});
		if (interfaces.size === 0) {
			return null;
		}
		return interfaces;
	}

	output.fields = function (shallow = false) {
		let fields = new Set();

		function addFields(data) {
			if (exists(data[PROPERTY.FIELDS])) {
				for (let i = 0; i < data[PROPERTY.FIELDS].length; i++) {
					data[PROPERTY.FIELDS][i].declaringClass = data[PROPERTY.TYPE_ID];
					fields.add(data[PROPERTY.FIELDS][i]);
				}
			}
		}

		if (shallow) {
			addFields(this.data);
		} else {
			this._follow_inheritance((data) => {
				addFields(data);
				getClass(data).interfaces()?.forEach((interfaceId) => {
					let data = DATA[interfaceId];
					addFields(data);
				});
			});
		}
		if (fields.size === 0) {
			return [];
		}

		let out = [...fields]
		for (let i = 0; i < out.length; i++) {
			out[i].dataIndex = i;
		}

		return out;
	}

	output.methods = function (shallow = false) {
		let methods = new Set();

		function addMethods(data) {
			if (exists(data[PROPERTY.METHODS])) {
				for (let i = 0; i < data[PROPERTY.METHODS].length; i++) {
					data[PROPERTY.METHODS][i].declaringClass = data[PROPERTY.TYPE_ID];
					methods.add(data[PROPERTY.METHODS][i]);
				}
			}
		}

		if (shallow) {
			addMethods(this.data);
		} else {
			this._follow_inheritance((data) => {
				addMethods(data);
				getClass(data).interfaces()?.forEach((interfaceId) => {
					let data = DATA[interfaceId];
					addMethods(data);
				});
			});
		}
		if (methods.size === 0) {
			return [];
		}
		let out = [...methods]
		for (let i = 0; i < out.length; i++) {
			out[i].dataIndex = i;
		}

		return out;
	}

	output.constructors = function () {
		let constructors = new Set();
		if (exists(this.data[PROPERTY.CONSTRUCTORS])) {
			for (let i = 0; i < this.data[PROPERTY.CONSTRUCTORS].length; i++) {
				this.data[PROPERTY.CONSTRUCTORS][i].declaringClass = this.data[PROPERTY.TYPE_ID];
				this.data[PROPERTY.CONSTRUCTORS][i].dataIndex = i;
				constructors.add(this.data[PROPERTY.CONSTRUCTORS][i]);
			}
		}

		if (constructors.size === 0) {
			return [];
		}
		return [...constructors];
	}

	output.annotations = function () {
		let annotations = new Set();
		this._follow_inheritance((data) => {
			if (exists(data[PROPERTY.ANNOTATIONS])) {
				for (let i = 0; i < data[PROPERTY.ANNOTATIONS].length; i++) {
					data[PROPERTY.ANNOTATIONS][i].dataIndex = i;
					annotations.add(data[PROPERTY.ANNOTATIONS][i]);
				}
			}
		});


		return annotations;
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output._follow_inheritance = function (action) {
		let seen = new Set();
		let current = this.id();
		while (exists(current) && !seen.has(current)) {
			action(DATA[current]);
			seen.add(current);
			current = getAnySuperClass(current);
		}

	}

	output.relation = function (index) {
		if (!exists(index)) {
			return null;
		}
		if (index >= 0 && index < RELATIONS.length) {
			return this.data["" + index];
		}
	}

	output.toKubeJSLoad_1_18 = function () {
		return `// KJSODocs: ${output.hrefLink()}\nconst $${output.simplename().toUpperCase()} = Java("${output.type()}");`
	}

	output.toKubeJSLoad_1_19 = function () {
		return `// KJSODocs: ${output.hrefLink()}\nconst $${output.simplename().toUpperCase()} = Java.loadClass("${output.type()}");`
	}

	output.toKubeJSLoad = function () {
		if (PROJECT_INFO.minecraft_version.includes("1.18")) {
			return this.toKubeJSLoad_1_18();
		}
		if (PROJECT_INFO.minecraft_version.includes("1.19")) {
			return this.toKubeJSLoad_1_19();
		}
	}

	output.hrefLink = function () {
		let url = DecodeURL();
		url.params.set("focus", this.type());
		return url.href();
	}

	return output;
}

function getParameter(paramData) {
	let output = {};
	output.data = paramData;

	output.name = function () {
		if (!exists(this.data._name_cache)) {
			this.data._name_cache = uncompressString(this.data[PROPERTY.PARAMETER_NAME]);
		}
		return this.data._name_cache;
	}

	output.type = function () {
		return this.data[PROPERTY.PARAMETER_TYPE];
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.PARAMETER_ANNOTATIONS];
		if (!exists(this.data[PROPERTY.PARAMETER_ANNOTATIONS]) || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.dataIndex = function () {
		return this.data.dataIndex;
	}

	output.id = function () {
		return this.type();
	}

	return output;
}

function getMethod(methodData) {
	let output = {};
	output.data = methodData;

	output.name = function () {
		if (!exists(this.data._name_cache)) {
			this.data._name_cache = uncompressString(this.data[PROPERTY.METHOD_NAME]);
		}
		return this.data._name_cache;
	}

	output.returnType = function () {
		return this.data[PROPERTY.METHOD_RETURN_TYPE];
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.METHOD_ANNOTATIONS];
		if (!exists(annotations) || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.parameters = function () {
		let parameters = this.data[PROPERTY.PARAMETERS];
		if (!exists(parameters) || parameters.length === 0) {
			return [];
		}
		return parameters;
	}

	output.declaredIn = function () {
		return this.data.declaringClass;
	}

	output.dataIndex = function () {
		return this.data.dataIndex;
	}

	output.toKubeJSStaticCall = function () {
		let parent = getClass(this.declaredIn());
		let out = `// KJSODocs: ${this.hrefLink()}\n$${parent.simplename().toUpperCase()}.${this.name()}(`;
		for (let i = 0; i < this.parameters().length; i++) {
			out += getParameter(this.parameters()[i]).name();
			if (i < this.parameters().length - 1) {
				out += ", ";
			}
		}
		out += `);`;
		return out;
	}

	output.id = function () {
		// Generate a unique HTML ID for this method
		return getClass(this.declaredIn()).type() + "." + this.name() + "(" + this.parameters().map((param) => {
			return getParameter(param).id();
		}).join(",") + ")";
	}

	output.hrefLink = function () {
		let url = DecodeURL();
		url.params.set("focus", this.id());
		return url.href();
	}

	return output;
}

function getField(fieldData) {
	let output = {};
	output.data = fieldData;

	output.name = function () {
		if (!exists(this.data._name_cache)) {
			this.data._name_cache = uncompressString(this.data[PROPERTY.FIELD_NAME]);
		}
		return this.data._name_cache;
	}

	output.type = function () {
		return this.data[PROPERTY.FIELD_TYPE];
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.FIELD_ANNOTATIONS];
		if (!exists(annotations) || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.declaredIn = function () {
		return this.data.declaringClass;
	}

	output.dataIndex = function () {
		return this.data.dataIndex;
	}

	output.toKubeJSStaticReference = function () {
		let parent = getClass(this.declaredIn());
		return `// KJSODocs: ${getClass(this.type()).hrefLink()}\n$${parent.simplename().toUpperCase()}.${this.name()};`;
	}

	output.id = function () {
		// Generate a unique HTML ID for this field
		return getClass(this.declaredIn()).type() + "." + this.name();
	}

	output.hrefLink = function () {
		let url = DecodeURL();
		url.params.set("focus", this.id());
		return url.href();
	}

	return output;
}

function getConstructor(constructorData) {
	let output = {};
	output.data = constructorData;

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.CONSTRUCTOR_ANNOTATIONS];
		if (!exists(annotations) || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.parameters = function () {
		let parameters = this.data[PROPERTY.PARAMETERS];
		if (!exists(parameters) || parameters.length === 0) {
			return [];
		}
		return parameters;
	}

	output.declaredIn = function () {
		return this.data.declaringClass;
	}

	output.dataIndex = function () {
		return this.data.dataIndex;
	}

	output.toKubeJSStaticCall = function () {
		let parent = getClass(this.declaredIn());
		let out = `// KJSODocs: ${this.hrefLink()}\nlet ${parent.simplename()} = new $${parent.simplename().toUpperCase()}(`;
		for (let i = 0; i < this.parameters().length; i++) {
			out += getParameter(this.parameters()[i]).name();
			if (i < this.parameters().length - 1) {
				out += ", ";
			}
		}
		out += ");";
		return out;
	}

	output.id = function () {
		// Generate a unique HTML ID for this constructor
		return getClass(this.declaredIn()).type() + ".__init__(" + this.parameters().map((param) => {
			return getParameter(param).id();
		}).join(",") + ")";
	}

	output.hrefLink = function () {
		let url = DecodeURL();
		url.params.set("focus", this.id());
		return url.href();
	}

	return output;
}

function getAnnotation(annotationData) {
	let output = {};
	output.data = annotationData;

	output.type = function () {
		return this.data[PROPERTY.ANNOTATION_TYPE];
	}

	output.string = function () {
		if (exists(this.data[PROPERTY.ANNOTATION_STRING])) {
			return uncompressString(this.data[PROPERTY.ANNOTATION_STRING]);
		} else {
			return "";
		}
	}

	return output;
}

function applyToAllClasses(action) {
	for (let i = 0; i < DATA.length; i++) {
		action(getClass(i));
	}
}

function findAllClassesThatMatch(predicate) {
	let output = [];
	applyToAllClasses((class_data) => {
		if (predicate(class_data)) {
			output.push(class_data.id());
		}
	});
	return output;
}
