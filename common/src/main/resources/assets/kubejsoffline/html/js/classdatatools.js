const LOOK_UP_CACHE = new Map();
function exists(thing) {
    return thing !== null && thing !== undefined;
}

/**
 * Returns the value as an array, returns
 * an empty array if the value is null or undefined,
 * and returns the value if the value is already an Array.
 * @param value
 * @returns {[*]}
 * @private
 */
function getAsArray(value) {
    if (!exists(value)) {
        return [];
    }
    if (Array.isArray(value)) {
        return value;
    }
    return [value];
}

function getTypeData(id) {
    if (!exists(id)) {
        throw new Error("Invalid type id: " + id);
    }
    if (typeof id !== "number") {
        throw new Error("Invalid type id type: " + typeof id);
    }
    if (id < 0 || id >= DATA.types.length) {
        throw new Error("Type id not within range: " + id);
    }

    if (!exists(DATA.types[id])) {
        throw new Error("Illegal State: Type data does not exist for id: " + id);
    }

    return DATA.types[id];
}

function getParameterData(id) {
    if (!exists(id)) {
        throw new Error("Invalid parameter id: " + id);
    }
    if (typeof id !== "number") {
        throw new Error("Invalid parameter id type: " + typeof id);
    }
    if (id < 0 || id >= DATA.parameters.length) {
        throw new Error("Parameter id not within range: " + id);
    }

    if (!exists(DATA.parameters[id])) {
        throw new Error("Illegal State: Parameter data does not exist for id: " + id);
    }

    return DATA.parameters[id];
}

function getPackageData(id) {
    if (!exists(id)) {
        throw new Error("Invalid package id: " + id);
    }
    if (typeof id !== "number") {
        throw new Error("Invalid package id type: " + typeof id);
    }
    if (id < 0 || id >= DATA.packages.length) {
        throw new Error("Package id not within range: " + id);
    }

    if (!exists(DATA.packages[id])) {
        throw new Error("Illegal State: Package data does not exist for id: " + id);
    }

    return DATA.packages[id];
}

function getPackageName(id) {
    let parts = getAsArray(getPackageData(id));
    if (parts.length === 1) {
        return parts[0];
    }
    return getPackageName(parts[1]) + "." + parts[0];
}

function getNameData(id) {
    if (!exists(id)) {
        throw new Error("Invalid name id: " + id);
    }
    if (typeof id !== "number") {
        throw new Error("Invalid name id type: " + typeof id);
    }
    if (id < 0 || id >= DATA.names.length) {
        throw new Error("Name id not within range: " + id);
    }

    if (!exists(DATA.names[id])) {
        throw new Error("Illegal State: Name data does not exist for id: " + id);
    }

    return DATA.names[id];
}

function getAnnotationData(id) {
    if (!exists(id)) {
        throw new Error("Invalid annotation id: " + id);
    }
    if (typeof id !== "number") {
        throw new Error("Invalid annotation id type: " + typeof id);
    }
    if (id < 0 || id >= DATA.annotations.length) {
        throw new Error("Annotation id not within range: " + id);
    }

    if (!exists(DATA.annotations[id])) {
        throw new Error("Illegal State: Annotation data does not exist for id: " + id);
    }

    return DATA.annotations[id];
}

function getClass(id) {
    let output = {};
    if (!exists(id)) {
        console.error("Invalid class id: " + id);
        return null;
    }
    switch (typeof (id)) {
        case "number":
            if (id < 0 || id >= DATA.types.length) {
                console.error("Invalid class id: " + id);
                return null;
            }
            if (!exists(getTypeData(id))) {
                console.error("Invalid class data: " + id);
                return null;
            }
            output.data = getTypeData(id);
            output.data._id = id;
            break;
        case "object":
            if (exists(id['data'])) {
                output.data = id.data;
            } else if (exists(id._id)) {
                output.data = getTypeData(id._id);
            } else if (Array.isArray(id) && id.length === 2) {
                // If it's an array, then assume it's an array of a class.
                // the first index is the array type, the depth is the second index,
                output.data = getTypeData(id[0]);
                output.data._id = id[0];
                output._array_depth = id[1];
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
            for (let i = 0; i < DATA.types.length; i++) {
                let lower = getClass(i).referenceName().toLowerCase();
                LOOK_UP_CACHE.set(lower, i);
                if (lowerID === lower) {
                    return getClass(i);
                }
            }
            for (let i = 0; i < DATA.types.length; i++) {
                let lower = getClass(i).fullyQualifiedName({}, false).toLowerCase();
                if (lowerID === lower) {
                    return getClass(i);
                }
            }
            // See if the string is a class type
            // See if the string is a class name
            for (let i = 0; i < DATA.types.length; i++) {
                if (lowerID === getClass(i).name({}, true).toLowerCase()) {
                    return getClass(i);
                }
                if (lowerID === getClass(i).name({}, false).toLowerCase()) {
                    return getClass(i);
                }
            }
            // See if the string is a class simple name
            for (let i = 0; i < DATA.types.length; i++) {
                if (getClass(i)?.simplename()?.toLowerCase() === lowerID) {
                    return getClass(i);
                }
            }
            console.log("Class not found: " + id);
            return null;
        default:
            console.error("Unsupported class type provided to getClass: " + id + " (" + typeof (id) + ")");
            return null;
    }

    if (!exists(output.data)) {
        console.error("Invalid class data: ", id, typeof (id));
    }

    /**
     * Whether this type is a Class type.
     * This means the Type extends Class<?> and is not a parameterized type.
     * @returns {*}
     */
    output.isRawClass = function () {
        return exists(this.data[PROPERTY.CLASS_NAME])
    }

    /**
     * Whether this type is a parameterized type.
     * This means the Type extends ParameterizedType or a subclass of ParameterizedType.
     * @returns {*}
     */
    output.isParameterizedType = function () {
        return exists(this.data[PROPERTY.RAW_PARAMETERIZED_TYPE]);
    }

    /**
     * Whether this type is a wildcard type.
     * This means the Type extends WildcardType.
     * @returns {boolean|*}
     */
    output.isWildcard = function () {
        return Object.keys(this.data).filter((key) => key.indexOf("_") !== 0).length === 0 || exists(this.data[PROPERTY.WILDCARD_LOWER_BOUNDS]) || exists(this.data[PROPERTY.WILDCARD_UPPER_BOUNDS])
    }

    /**
     * Whether this type is a type variable.
     * This means the Type extends TypeVariable.
     * @returns {*}
     */
    output.isTypeVariable = function () {
        return exists(this.data[PROPERTY.TYPE_VARIABLE_NAME]);
    }

    output.getTypeVariableMap = function () {
        if (!exists(this.data._type_variable_map)) {
            this.data._type_variable_map = createTypeVariableMap(this.id());
        }
        return this.data._type_variable_map;
    }

    output.id = function () {
        // TODO: Rewrite.
        return this.data._id;
    }

    output.referenceName = function (typeVariableMap = {}) {
        return this.fullyQualifiedName(typeVariableMap, true);
    }

    output.fullyQualifiedName = function (typeVariableMap = {}, includeGenerics = true) {
        if (this.isRawClass()) {
            const name = getGenericDefinition(this.id(), this.getTypeVariableMap(), includeGenerics);
            const typeVariables = this.getTypeVariables();
            let genericSuffix = "";
            if (typeVariables.length > 0 && includeGenerics) {
                genericSuffix = joiner(typeVariables, ", ", (type) => {
                    return getClass(type).fullyQualifiedName(typeVariableMap);
                }, "<", ">");
            }
            return name + genericSuffix + "[]".repeat(this.getArrayDepth());
        } else {
            return getGenericDefinition(this.id(), typeVariableMap, includeGenerics) + "[]".repeat(this.getArrayDepth());
        }
    }


    output.name = function (typeVariableMap = {}, includeGenerics = true) {
        if (this.isRawClass()) {
            const name = getGenericName(this.id(), createTypeVariableMap(this.id()), includeGenerics);
            const typeVariables = this.getTypeVariables();
            let genericSuffix = "";
            if (typeVariables.length > 0 && includeGenerics) {
                genericSuffix = joiner(typeVariables, ", ", (type) => {
                    return getClass(type).name(typeVariableMap);
                }, "<", ">");
            }
            return name + genericSuffix + "[]".repeat(this.getArrayDepth());
        } else {
            return getGenericName(this.id(), typeVariableMap, includeGenerics) + "[]".repeat(this.getArrayDepth());
        }
    }

    output.simplename = function (typeVariableMap = {}) {
        if (this.isWildcard()) {
            return "?" + "[]".repeat(this.getArrayDepth());
        }
        if (this.isTypeVariable()) {
            return uncompressString(this.data[PROPERTY.TYPE_VARIABLE_NAME]) + "[]".repeat(this.getArrayDepth());
        }
        if (this.isParameterizedType()) {
            const rawName = getClass(this.getRawType()).simplename(typeVariableMap);
            const ownerPrefix = this.getOwnerType() ? getClass(this.getOwnerType()).simplename(typeVariableMap) + "." : "";
            return ownerPrefix + rawName + "[]".repeat(this.getArrayDepth());
        }
        return uncompressString(this.data[PROPERTY.CLASS_NAME]) + "[]".repeat(this.getArrayDepth());
    }

    output.getTypeVariables = function () {
        return getAsArray(this.data[PROPERTY.TYPE_VARIABLES]);
    }

    output.getTypeVariableBounds = function () {
        return getAsArray(this.data[PROPERTY.TYPE_VARIABLE_BOUNDS]);
    }

    output.getLowerBound = function () {
        return getAsArray(this.data[PROPERTY.WILDCARD_LOWER_BOUNDS]);
    }

    output.getUpperBound = function () {
        return getAsArray(this.data[PROPERTY.WILDCARD_UPPER_BOUNDS]);
    }

    output.getInterfaces = function () {
        return getAsArray(this.data[PROPERTY.INTERFACES]);
    }

    output.getOwnerType = function () {
        return this.data[PROPERTY.OWNER_TYPE];
    }

    output.getSuperClass = function () {
        return this.data[PROPERTY.SUPER_CLASS];
    }

    output.getArrayDepth = function () {
        return (exists(this._array_depth) ? this._array_depth : 0);
    }

    output.getRawType = function () {
        return this.data[PROPERTY.RAW_PARAMETERIZED_TYPE];
    }

    output.getPackageName = function () {
        if (exists(this.data._cachedPackageName)) {
            return this.data._cachedPackageName;
        }
        const packageName = this.data[PROPERTY.PACKAGE_NAME];
        if (exists(packageName)) {
            this.data._cachedPackageName = getPackageName(packageName);
        } else {
            this.data._cachedPackageName = "";
        }
        return this.data._cachedPackageName;
    }

    output.getParameterizedArgs = function () {
        return getAsArray(this.data[PROPERTY.PARAMETERIZED_ARGUMENTS]);
    }

    output.getModifiers = function () {
        return this.data[PROPERTY.MODIFIERS];
    }

    output.isGeneric = function () {
        return exists(this.data[PROPERTY.PARAMETERIZED_ARGUMENTS]);
    }

    output.isInnerClass = function () {
        return exists(this.data[PROPERTY.OWNER_TYPE]);
    }

    output.getAllInheritedClasses = function () {
        if (exists(this.data._cachedInheritedClasses)) {
            return this.data._cachedInheritedClasses;
        }
        let classes = new Set();
        this._follow_inheritance((data, index) => {
            classes.add(index);
        });
        this.data._cachedInheritedClasses = classes;
        return classes;
    }

    output.rawtype = function () {
        return this.data[PROPERTY.RAW_PARAMETERIZED_TYPE];
    }

    output.package = function () {
        let pkg = this.data[PROPERTY.PACKAGE_NAME];
        if (this.data._cachedPackageName) {
            return this.data._cachedPackageName;
        }

        if (exists(pkg)) {
            this.data._cachedPackageName = getPackageName(pkg);
            return this.data._cachedPackageName;
        }

        this.data._cachedPackageName = "";
        return "";
    }

    output.paramargs = function () {
        let args = this.data[PROPERTY.PARAMETERIZED_ARGUMENTS];
        if (!exists(args) || args.length === 0) {
            return null;
        }
        return args;
    }

    output.outerclass = function () {
        return this.data[PROPERTY.OWNER_TYPE];
    }

    output.arrayDepth = function () {
        let depth = this._array_depth;
        if (!exists(depth)) {
            return 0;
        }
        return depth;
    }

    output.superclass = function () {
        if (exists(this.data[PROPERTY.SUPER_CLASS])) {
            return this.data[PROPERTY.SUPER_CLASS];
        }

        return null;
    }

    output.interfaces = function () {
        let interfaces = new Set();
        this._follow_inheritance((data, index) => {
            if (exists(data[PROPERTY.INTERFACES])) {
                for (let i = 0; i < data[PROPERTY.INTERFACES].length; i++) {
                    data[PROPERTY.INTERFACES][i].declaringClass = index;
                    interfaces.add(data[PROPERTY.INTERFACES][i]);
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

        function addFields(data, declaringClass) {
            if (exists(data[PROPERTY.FIELDS])) {
                for (let i = 0; i < data[PROPERTY.FIELDS].length; i++) {
                    data[PROPERTY.FIELDS][i].declaringClass = declaringClass;
                    fields.add(data[PROPERTY.FIELDS][i]);
                }
            }
        }

        if (shallow) {
            addFields(this.data, this.id());
        } else {
            this._follow_inheritance((data, index) => {
                addFields(data, index);
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

        function addMethods(data, index) {
            if (exists(data[PROPERTY.METHODS])) {
                for (let i = 0; i < data[PROPERTY.METHODS].length; i++) {
                    data[PROPERTY.METHODS][i].declaringClass = getClass(data).id();
                    methods.add(data[PROPERTY.METHODS][i]);
                }
            }
        }

        if (shallow) {
            addMethods(this.data, this.id());
        } else {
            this._follow_inheritance((data, index) => {
                addMethods(data, index);
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
                this.data[PROPERTY.CONSTRUCTORS][i].declaringClass = this.id();
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
        const seen = new Set();
        const unprocessed = [this.id()];
        while (unprocessed.length > 0) {
            const current = unprocessed.pop();
            if (!exists(current)) {
                continue;
            }
            if (seen.has(current)) {
                continue;
            }
            seen.add(current);
            action(DATA.types[current], current);
            unprocessed.push(getClass(current).getSuperClass());
            unprocessed.push(...getClass(current).getInterfaces());
            unprocessed.push(getClass(current).getOwnerType());
        }
    }

    output.relation = function (index) {
        if (!exists(index)) {
            return [];
        }
        if (index >= 0 && index < RELATIONS.length) {
            switch (RELATIONS[index]) {
                case "SUPER_CLASS_OF":
                    // Find all classes that inherit from this class
                    if (exists(this.data._subclasses)) {
                        return this.data._subclasses;
                    }
                    this.data._subclasses = [...new Set(findAllClassesThatMatch((data) => {
                        return this.id() === data.getSuperClass();
                    }))];
                    return this.data._subclasses;
                case "INNER_TYPE_OF":
                    // Find all inner classes of this class
                    if (exists(this.data._innerclasses)) {
                        return this.data._innerclasses;
                    }
                    this.data._innerclasses = [...new Set(findAllClassesThatMatch((data) => {
                        return this.id() === data.getOwnerType();
                    }))];
                    return this.data._innerclasses;
                case "COMPONENT_OF":
                    // Find all classes that this class is a component of
                    if (exists(this.data._components)) {
                        return this.data._components;
                    }
                    this.data._components = [...new Set(findAllClassesThatMatch((data) => {
                        return this.id() === data.getRawType();
                    }))]
                    return this.data._components;
                case "IMPLEMENTATION_OF":
                    // Find all classes that implement this class
                    if (exists(this.data._implementations)) {
                        return this.data._implementations;
                    }
                    this.data._implementations = [...new Set(findAllClassesThatMatch((data) => {
                        return data.getAllInheritedClasses().has(this.id());
                    }))];
                    return this.data._implementations;
                case "DECLARED_FIELD_TYPE_OF":
                    // Find all classes that contain a field with this type
                    if (exists(this.data._fields)) {
                        return this.data._fields;
                    }
                    this.data._fields = [...new Set(findAllClassesThatMatch((data) => {
                        return data.fields().some((field) => {
                            return getClass(getField(field, data.getTypeVariableMap()).type()).id() === this.id();
                        });
                    }))];
                    return this.data._fields;
                case "DECLARED_METHOD_RETURN_TYPE_OF":
                    // Find all classes that contain a method with this return type
                    if (exists(this.data._methods)) {
                        return this.data._methods;
                    }
                    this.data._methods = [...new Set(findAllClassesThatMatch((data) => {
                        return data.methods(true).some((method) => {
                            return getClass(getMethod(method, data.getTypeVariableMap()).returnType()).id() === this.id();
                        });
                    }))];
                    return this.data._methods;
                case "DECLARED_METHOD_PARAMETER_TYPE_OF":
                    // Find all classes that contain a method with this parameter type
                    if (exists(this.data._methods)) {
                        return this.data._methods;
                    }
                    this.data._methods = [...new Set(findAllClassesThatMatch((data) => {
                        return data.methods(true).some((method) => {
                            return getMethod(method, data.getTypeVariableMap()).parameters().some((param) => {
                                return getClass(getParameter(param, data.getTypeVariableMap()).type()).id() === this.id();
                            });
                        });
                    }))];
                    return this.data._methods;
                case "TYPE_VARIABLE_OF":
                    // Find all classes that use this type variable
                    if (exists(this.data._type_variables)) {
                        return this.data._type_variables;
                    }
                    this.data._type_variables = [...new Set(findAllClassesThatMatch((data) => {
                        return data.getTypeVariables().includes(this.id());
                    }))];
                    return this.data._type_variables;

            }
        }
    }

    output.toKubeJSLoad_1_18 = function () {
        return `// KJSODocs: ${output.hrefLink()}\nconst $${output.simplename().toUpperCase()} = Java("${output.fullyQualifiedName()}");`
    }

    output.toKubeJSLoad_1_19 = function () {
        return `// KJSODocs: ${output.hrefLink()}\nconst $${output.simplename().toUpperCase()} = Java.loadClass("${output.fullyQualifiedName()}");`
    }

    output.toKubeJSLoad_1_20 = function () {
        return `// KJSODocs: ${output.hrefLink()}\nconst $${output.simplename().toUpperCase()} = Java.loadClass("${output.fullyQualifiedName()}");`
    }

    output.toKubeJSLoad = function () {
        if (PROJECT_INFO.minecraft_version.includes("1.18")) {
            return this.toKubeJSLoad_1_18();
        }
        if (PROJECT_INFO.minecraft_version.includes("1.19")) {
            return this.toKubeJSLoad_1_19();
        }
        if (PROJECT_INFO.minecraft_version.includes("1.20")) {
            return this.toKubeJSLoad_1_20();
        }
    }

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.fullyQualifiedName());
        return url.href();
    }

    return output;
}

function getParameter(parameterID, typeVariableMap = {}) {
    if (typeof parameterID !== "number") {
        console.error("Invalid parameter type for parameter:", parameterID);
        throw new Error("Invalid parameter type for parameter: " + parameterID);
    }
    const paramData = getParameterData(parameterID);

    let output = {};
    output.data = paramData;
    output._type_variable_map = typeVariableMap;

    output.name = function () {
        if (!exists(this.data._name_cache)) {
            this.data._name_cache = uncompressString(this.data[PROPERTY.PARAMETER_NAME]);
        }
        return this.data._name_cache;
    }

    output.type = function () {
        const paramType = this.data[PROPERTY.PARAMETER_TYPE];
        if (!exists(paramType)) {
            return paramType;
        }
        if (exists(this._type_variable_map[paramType])) {
            return this._type_variable_map[paramType];
        }
        return paramType;
    }

    output.modifiers = function () {
        return this.data[PROPERTY.MODIFIERS];
    }

    output.annotations = function () {
        return getAsArray(this.data[PROPERTY.ANNOTATIONS]);
    }

    output.dataIndex = function () {
        return this.data.dataIndex;
    }

    output.id = function () {
        return this.type();
    }

    output.getTypeVariableMap = function () {
        return this._type_variable_map;
    }

    return output;
}

function getMethod(methodData, typeVariableMap = {}) {
    if (!exists(methodData)) {
        throw new Error("Invalid method data: " + methodData);
    }
    let output = {};
    output.data = methodData;
    output._type_variable_map = typeVariableMap;

    output.name = function () {
        if (!exists(this.data._name_cache)) {
            this.data._name_cache = uncompressString(this.data[PROPERTY.METHOD_NAME]);
        }
        return this.data._name_cache;
    }

    output.returnType = function () {
        const returnType = this.data[PROPERTY.METHOD_RETURN_TYPE];
        if (!exists(returnType)) {
            return returnType;
        }
        if (exists(this._type_variable_map[returnType])) {
            return this._type_variable_map[returnType];
        }
        return returnType;
    }

    output.modifiers = function () {
        return this.data[PROPERTY.MODIFIERS];
    }

    output.annotations = function () {
        return getAsArray(this.data[PROPERTY.ANNOTATIONS]);
    }

    output.parameters = function () {
        return getAsArray(this.data[PROPERTY.PARAMETERS]);
    }

    output.declaredIn = function () {
        return this.data.declaringClass;
    }

    output.dataIndex = function () {
        return this.data.dataIndex;
    }

    output.getTypeVariableMap = function () {
        return this._type_variable_map;
    }

    output.toKubeJSStaticCall = function () {
        let parent = getClass(this.declaredIn());
        let out = `// KJSODocs: ${this.hrefLink()}\n$${parent.simplename().toUpperCase()}.${this.name()}(`;
        for (let i = 0; i < this.parameters().length; i++) {
            out += getParameter(this.parameters()[i], this._type_variable_map).name();
            if (i < this.parameters().length - 1) {
                out += ", ";
            }
        }
        out += `);`;
        return out;
    }

    output.id = function () {
        // Generate a unique HTML ID for this method
        return getClass(this.declaredIn()).fullyQualifiedName(this._type_variable_map) + "." + this.name() + "(" + this.parameters().map((param) => {
            return getParameter(param, this._type_variable_map).id();
        }).join(",") + ")";
    }

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.id());
        return url.href();
    }

    return output;
}

function getField(fieldData, typeVariableMap = {}) {
    if (!exists(fieldData)) {
        throw new Error("Invalid field data: " + fieldData);
    }

    let output = {};
    output.data = fieldData;
    output._type_variable_map = typeVariableMap;

    output.name = function () {
        if (!exists(this.data._name_cache)) {
            this.data._name_cache = uncompressString(this.data[PROPERTY.FIELD_NAME]);
        }
        return this.data._name_cache;
    }

    output.type = function () {
        const fieldType = this.data[PROPERTY.FIELD_TYPE];
        if (!exists(fieldType)) {
            return fieldType;
        }
        if (exists(this._type_variable_map[fieldType])) {
            return this._type_variable_map[fieldType];
        }
        return fieldType;
    }

    output.modifiers = function () {
        return this.data[PROPERTY.MODIFIERS];
    }

    output.annotations = function () {
        return getAsArray(this.data[PROPERTY.ANNOTATIONS]);
    }

    output.declaredIn = function () {
        return this.data.declaringClass;
    }

    output.dataIndex = function () {
        return this.data.dataIndex;
    }

    output.getTypeVariableMap = function () {
        return this._type_variable_map;
    }

    output.toKubeJSStaticReference = function () {
        let parent = getClass(this.declaredIn());
        return `// KJSODocs: ${getClass(this.type()).hrefLink()}\n$${parent.simplename(this._type_variable_map).toUpperCase()}.${this.name()};`;
    }

    output.id = function () {
        // Generate a unique HTML ID for this field
        return getClass(this.declaredIn()).fullyQualifiedName(this._type_variable_map) + "." + this.name();
    }

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.id());
        return url.href();
    }

    return output;
}

function getConstructor(constructorData, typeVariableMap = {}) {
    if (!exists(constructorData)) {
        throw new Error("Invalid constructor data: " + constructorData);
    }
    let output = {};
    output.data = constructorData;
    output._type_variable_map = typeVariableMap;

    output.modifiers = function () {
        return this.data[PROPERTY.MODIFIERS];
    }

    output.annotations = function () {
        return getAsArray(this.data[PROPERTY.ANNOTATIONS]);
    }

    output.parameters = function () {
        return getAsArray(this.data[PROPERTY.PARAMETERS]);
    }

    output.declaredIn = function () {
        return this.data.declaringClass;
    }

    output.dataIndex = function () {
        return this.data.dataIndex;
    }

    output.getTypeVariableMap = function () {
        return this._type_variable_map;
    }

    output.toKubeJSStaticCall = function () {
        let parent = getClass(this.declaredIn());
        let out = `// KJSODocs: ${this.hrefLink()}\nlet ${parent.simplename(this._type_variable_map)} = new $${parent.simplename(this._type_variable_map).toUpperCase()}(`;
        for (let i = 0; i < this.parameters().length; i++) {
            out += getParameter(this.parameters()[i], this._type_variable_map).name();
            if (i < this.parameters().length - 1) {
                out += ", ";
            }
        }
        out += ");";
        return out;
    }

    output.id = function () {
        // Generate a unique HTML ID for this constructor
        return getClass(this.declaredIn()).fullyQualifiedName(this._type_variable_map) + ".__init__(" + this.parameters().map((param) => {
            return getParameter(param, this._type_variable_map).id();
        }).join(",") + ")";
    }

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.id());
        return url.href();
    }

    return output;
}

function getAnnotation(annotationData, typeVariableMap = {}) {
    if (!exists(annotationData)) {
        throw new Error("Invalid annotation data: " + annotationData);
    }
    if (typeof annotationData !== "number") {
        console.error("Invalid annotation type for annotation:", annotationData);
        throw new Error("Invalid annotation type for annotation: " + annotationData);
    }
    let output = {};
    output.data = getAnnotationData(annotationData);
    output._type_variable_map = typeVariableMap;

    output.type = function () {
        const annotationType = this.data[PROPERTY.ANNOTATION_TYPE];
        if (!exists(annotationType)) {
            return annotationType;
        }
        if (exists(this._type_variable_map[annotationType])) {
            return this._type_variable_map[annotationType];
        }
        return annotationType;
    }

    output.string = function () {
        if (exists(this.data[PROPERTY.ANNOTATION_STRING])) {
            return (this.data[PROPERTY.ANNOTATION_STRING]);
        } else {
            return "";
        }
    }

    output.getTypeVariableMap = function () {
        return this._type_variable_map;
    }

    return output;
}

function applyToAllClasses(action) {
    for (let i = 0; i < DATA.types.length; i++) {
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
