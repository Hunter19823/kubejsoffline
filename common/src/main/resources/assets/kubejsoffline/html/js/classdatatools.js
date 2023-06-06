function getAnySuperClass(id) {
	let data = null;
	if (id === null || id === undefined) {
		return null;
	}

	switch (typeof (id)) {
		case "number":
			data = getClass(id).data;
			break;
		case "object":
			if (id['data'] !== null && id['data'] !== undefined) {
				data = id.data;
			} else {
				data = id;
			}
			break;
		default:
			console.error("Invalid class id passed to getAnySuperClass: " + id);
			return null;
	}

	if (data === null || data === undefined) {
		return null;
	}

	if (data[PROPERTY.SUPER_CLASS] !== null && data[PROPERTY.SUPER_CLASS] !== undefined) {
		return data[PROPERTY.SUPER_CLASS];
	}

	if (data[PROPERTY.GENERIC_SUPER_CLASS] !== null && data[PROPERTY.GENERIC_SUPER_CLASS] !== undefined) {
		return data[PROPERTY.GENERIC_SUPER_CLASS];
	}

	return null;
}
const LOOK_UP_CACHE = new Map();
function getClass(id) {
	let output = {};
	if (id === null || id === undefined) {
		console.error("Invalid class id: " + id);
		return null;
	}
	switch (typeof (id)) {
		case "number":
			if (id < 0 || id >= DATA.length) {
				console.error("Invalid class id: " + id);
				return null;
			}
			if (DATA[id] === null || DATA[id] === undefined) {
				console.error("Invalid class data: " + id);
				return null;
			}
			output.data = DATA[id];
			break;
		case "object":
			if (id['data'] !== null && id['data'] !== undefined) {
				output.data = id.data;
			} else if (id['id'] !== null && id['id'] !== undefined) {
				output.data = DATA[id.id];
			}
			break;
		case "string":
			// See if the string is a number
			let num = parseInt(id);
			if (!isNaN(num)) {
				return getClass(num);
			}
			// Assume it's a search query
			if (id.match(/[a-zA-Z-]+--.+/)) {
				let split = id.split("--");
				if (split.length !== 2) {
					console.error("Invalid search query: " + id);
					return null;
				}
				let search = split[0];
				let page = split[1];
				searchForTerms(search, page);
				return null;
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
				let lower = DATA[i][PROPERTY.TYPE_IDENTIFIER]?.toLowerCase();
				LOOK_UP_CACHE.set(lower, i);
				if (lowerID === lower) {
					return getClass(i);
				}
			}
			// See if the string is a class name
			for (let i = 0; i < DATA.length; i++) {
				if (lowerID === DATA[i][PROPERTY.BASE_CLASS_NAME]?.toLowerCase()) {
					return getClass(i);
				}
			}
			// See if the string is a class simple name
			for (let i = 0; i < DATA.length; i++) {
				if (lowerID === getClass(i)?.simplename()?.toLowerCase()) {
					return getClass(i);
				}
			}
			// See if the string is included in a class name
			for (let i = LOOK_UP_CACHE.size; i < DATA.length; i++) {
				let lower = DATA[i][PROPERTY.TYPE_IDENTIFIER]?.toLowerCase();
				if (lowerID.includes(lower)) {
					return getClass(i);
				}
			}
			break;
		default:
			console.error("Unsupported class type provided to getClass: " + id + " (" + typeof (id) + ")");
			return null;
	}

	output.id = function () {
		return this.data[PROPERTY.TYPE_ID];
	}

	output.name = function () {
		return this.data[PROPERTY.BASE_CLASS_NAME];
	}

	output.type = function () {
		return this.data[PROPERTY.TYPE_IDENTIFIER];
	}

	output.rawtype = function () {
		return this.data[PROPERTY.RAW_PARAMETERIZED_TYPE];
	}

	output.simplename = function () {
		let fullName = this.type();
		if (this.data._cachedSimpleName) {
			return this.data._cachedSimpleName;
		}
		// Remove any Generics
		let index = fullName.indexOf("<");
		if (index !== -1) {
			fullName = fullName.substring(0, index);
		}
		// Remove package name
		index = fullName.lastIndexOf(".");
		if (index !== -1) {
			fullName = fullName.substring(index + 1);
		}
		// Remove any array brackets
		index = fullName.indexOf("[");
		if (index !== -1) {
			fullName = fullName.substring(0, index);
		}
		this.data._cachedSimpleName = fullName;
		return fullName;
	}

	output.package = function () {
		let pkg = this.data[PROPERTY.PACKAGE_NAME];
		if (this.data._cachedPackageName) {
			return this.data._cachedPackageName;
		}
		if (pkg === null || pkg === undefined) {
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
				pkg = fullName.substring(0, index);
			}
			this.data._cachedPackageName = pkg;
			return fullName;
		}
		return pkg;
	}

	output.paramargs = function () {
		let args = this.data[PROPERTY.PARAMETERIZED_ARGUMENTS];
		if (args === null || args === undefined || args.length === 0) {
			return null;
		}
		return args;
	}

	output.arrayDepth = function () {
		let depth = this.data[PROPERTY.ARRAY_DEPTH];
		if (depth === null || depth === undefined) {
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
			if (data[PROPERTY.INTERFACES] !== null && data[PROPERTY.INTERFACES] !== undefined) {
				for (let i = 0; i < data[PROPERTY.INTERFACES].length; i++) {
					data[PROPERTY.INTERFACES][i].declaringClass = data[PROPERTY.TYPE_ID];
					interfaces.add(data[PROPERTY.INTERFACES][i]);
				}
			}
			if (data[PROPERTY.GENERIC_INTERFACES] !== null && data[PROPERTY.GENERIC_INTERFACES] !== undefined) {
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
			if (data[PROPERTY.FIELDS] !== null && data[PROPERTY.FIELDS] !== undefined) {
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
			if (data[PROPERTY.METHODS] !== null && data[PROPERTY.METHODS] !== undefined) {
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
		if (this.data[PROPERTY.CONSTRUCTORS] !== null && this.data[PROPERTY.CONSTRUCTORS] !== undefined) {
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
			if (data[PROPERTY.ANNOTATIONS] !== null && data[PROPERTY.ANNOTATIONS] !== undefined) {
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
		while (current !== null && current !== undefined && !seen.has(current)) {
			action(DATA[current]);
			seen.add(current);
			current = getAnySuperClass(current);
		}

	}

	output.relation = function (index) {
		if (index === null || index === undefined) {
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
		return window.location.pathname + '#' + this.type();
	}

	return output;
}

function getParameter(paramData) {
	let output = {};
	output.data = paramData;

	output.name = function () {
		return this.data[PROPERTY.PARAMETER_NAME];
	}

	output.type = function () {
		return this.data[PROPERTY.PARAMETER_TYPE];
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.PARAMETER_ANNOTATIONS];
		if (annotations === null || annotations === undefined || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.dataIndex = function () {
		return this.data.dataIndex;
	}

	return output;
}

function getMethod(methodData) {
	let output = {};
	output.data = methodData;

	output.name = function () {
		return this.data[PROPERTY.METHOD_NAME];
	}

	output.returnType = function () {
		return this.data[PROPERTY.METHOD_RETURN_TYPE];
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.METHOD_ANNOTATIONS];
		if (annotations === null || annotations === undefined || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.parameters = function () {
		let parameters = this.data[PROPERTY.PARAMETERS];
		if (parameters === null || parameters === undefined || parameters.length === 0) {
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
		let out = `// KJSODocs: ${parent.hrefLink()}\n$${parent.simplename().toUpperCase()}.${this.name()}(`;
		for (let i = 0; i < this.parameters().length; i++) {
			out += getParameter(this.parameters()[i]).name();
			if (i < this.parameters().length - 1) {
				out += ", ";
			}
		}
		out += `);`;
		return out;
	}

	return output;
}

function getField(fieldData) {
	let output = {};
	output.data = fieldData;

	output.name = function () {
		return this.data[PROPERTY.FIELD_NAME];
	}

	output.type = function () {
		return this.data[PROPERTY.FIELD_TYPE];
	}

	output.modifiers = function () {
		return this.data[PROPERTY.MODIFIERS];
	}

	output.annotations = function () {
		let annotations = this.data[PROPERTY.FIELD_ANNOTATIONS];
		if (annotations === null || annotations === undefined || annotations.length === 0) {
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
		if (annotations === null || annotations === undefined || annotations.length === 0) {
			return null;
		}
		return new Set(annotations);
	}

	output.parameters = function () {
		let parameters = this.data[PROPERTY.PARAMETERS];
		if (parameters === null || parameters === undefined || parameters.length === 0) {
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
		let out = `// KJSODocs: ${parent.hrefLink()}\nlet ${parent.simplename()} = new $${parent.simplename().toUpperCase()}(`;
		for (let i = 0; i < this.parameters().length; i++) {
			out += getParameter(this.parameters()[i]).name();
			if (i < this.parameters().length - 1) {
				out += ", ";
			}
		}
		out += ");";
		return out;
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
		return this.data[PROPERTY.ANNOTATION_STRING];
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
