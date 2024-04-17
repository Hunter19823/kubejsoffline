function exists(thing) {
    return thing !== null && thing !== undefined;
}

/**
 * Returns the value as an array, returns
 * an empty array if the value is null or undefined,
 * and returns the value if the value is already an Array.
 * @param {T | T[]}value
 * @template T
 * @returns {T[]}
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

function optimizeDataSearch() {
    DATA._optimized = true;
    DATA._wildcard_types = [];
    DATA._parameterized_types = [];
    DATA._raw_types = [];
    DATA._type_variables = [];

    for (let i = 0; i < DATA.types.length; i++) {
        const typeData = getTypeData(i);
        if (!exists(typeData)) {
            console.error("Invalid type data in export: ", i);
            continue;
        }
        typeData._id = i;
        const subject = getClass(i);
        if (subject.isWildcard()) {
            DATA._wildcard_types.push(i);
        } else if (subject.isParameterizedType()) {
            DATA._parameterized_types.push(i);
        } else if (subject.isTypeVariable()) {
            DATA._type_variables.push(i);
        } else {
            DATA._raw_types.push(i);
            const typeData = subject.data;
            // Set declaring class on all fields, methods, and constructors
            if (exists(typeData[PROPERTY.FIELDS])) {
                typeData[PROPERTY.FIELDS].forEach((field) => {
                    field._declaringClass = i;
                });
            }
            if (exists(typeData[PROPERTY.METHODS])) {
                typeData[PROPERTY.METHODS].forEach((method) => {
                    method._declaringClass = i;
                    // Assign the declaring class to the parameters
                    if (exists(method[PROPERTY.PARAMETERS])) {
                        method[PROPERTY.PARAMETERS].forEach((parameter) => {
                            parameter._declaringClass = i;
                        });
                    }
                });
            }
            if (exists(typeData[PROPERTY.CONSTRUCTORS])) {
                typeData[PROPERTY.CONSTRUCTORS].forEach((constructor) => {
                    constructor._declaringClass = i;
                    // Assign the declaring class to the parameters
                    if (exists(constructor[PROPERTY.PARAMETERS])) {
                        constructor[PROPERTY.PARAMETERS].forEach((parameter) => {
                            parameter._declaringClass = i;
                        });
                    }
                });
            }
        }
    }
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

function findClassByName(name) {
    let isArray = name.endsWith("[]");
    if (isArray) {
        return findClassByName(name.substring(0, name.length - 2));
    }
    const containsGeneric = name.includes("<") && name.includes(">");
    const containsInnerClass = name.includes("$");
    const isParameterized = containsGeneric & name.endsWith(">") || containsInnerClass;
    const isWildcard = name.startsWith("?");
    const containsPackage = name.includes(".");

    console.debug("Searching type: ", name, "Contains package: ", containsPackage, "Contains generic: ", containsGeneric, "Contains inner class: ", containsInnerClass, "Is parameterized: ", isParameterized, "Is wildcard: ", isWildcard);

    if (isWildcard) {
        return DATA._wildcard_types.map((index) => getClass(index)).find((type) => {
            if (containsPackage && type.referenceName() === name) {
                console.debug("Found wildcard type using reference name: ", type.referenceName());
                return type;
            }
            if (type.name() === name) {
                console.debug("Found wildcard type using name: ", type.name());
                return type;
            }
        }) ?? null;
    }

    if (isParameterized) {
        return DATA._parameterized_types.map((index) => getClass(index)).find((type) => {
            if (containsPackage && type.referenceName() === name) {
                console.debug("Found parameterized type using reference name: ", type.referenceName());
                return type;
            }
            if (type.name() === name) {
                console.debug("Found parameterized type using name: ", type.name());
                return type;
            }
        }) ?? null;
    }

    return DATA._raw_types.map((index) => getClass(index)).find((type) => {
        if (containsPackage && type.referenceName() === name) {
            console.debug("Found raw type using reference name: ", type.referenceName());
            return type;
        }
        if (type.name() === name) {
            console.debug("Found raw type using name: ", type.name());
            return type;
        }
    }) ?? DATA._type_variables.map((index) => getClass(index)).find((type) => {
        if (containsPackage && type.referenceName() === name) {
            console.debug("Found type variable using reference name: ", type.referenceName());
            return type;
        }
        if (type.name() === name) {
            console.debug("Found type variable using name: ", type.name());
            return type;
        }
    }) ?? null;
}

function getClass(id) {
    let output = {};
    if (!exists(id)) {
        console.error("Invalid class id: " + id);
        return null;
    }
    if (!DATA._optimized) {
        optimizeDataSearch();
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
            return findClassByName(id);
        default:
            console.error("Unsupported class type provided to getClass: " + id + " (" + typeof (id) + ")");
            return null;
    }

    if (!exists(output.data)) {
        console.error("Invalid class data: ", id, typeof (id));
    }


    output = setModifiers(output);
    output = setTypeVariables(output);

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
        if (!exists(this.data._id)) {
            console.error("Invalid class data: ", this.data);
        }
        return this.data._id;
    }

    output.referenceName = function (typeVariableMap = {}) {
        return this.fullyQualifiedName(typeVariableMap, true);
    }

    output.fullyQualifiedName = function (typeVariableMap = {}, includeGenerics = true) {
        if (this.isRawClass()) {
            const name = getGenericDefinition(this.id(), typeVariableMap, includeGenerics);
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
            return decompressString(this.data[PROPERTY.TYPE_VARIABLE_NAME]) + "[]".repeat(this.getArrayDepth());
        }
        if (this.isParameterizedType()) {
            const rawName = getClass(this.getRawType()).simplename(typeVariableMap);
            const ownerPrefix = this.getOwnerType() ? getClass(this.getOwnerType()).simplename(typeVariableMap) + "." : "";
            return ownerPrefix + rawName + "[]".repeat(this.getArrayDepth());
        }
        return decompressString(this.data[PROPERTY.CLASS_NAME]) + "[]".repeat(this.getArrayDepth());
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
                    interfaces.add(data[PROPERTY.INTERFACES][i]);
                }
            }
        });
        if (interfaces.size === 0) {
            return null;
        }
        return interfaces;
    }

    /**
     * Returns all fields of this class.
     * @param shallow {boolean} whether to only get the fields of this class and not its inherited classes.
     * @returns {Field[]} an array of fields
     */
    output.fields = function (shallow = false) {
        const fields = [];

        function addFields(data, declaringClass) {
            if (exists(data[PROPERTY.FIELDS])) {
                for (let i = 0; i < data[PROPERTY.FIELDS].length; i++) {
                    fields.push(getField(data[PROPERTY.FIELDS][i], output.getTypeVariableMap()));
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
        if (fields.length === 0) {
            return [];
        }

        for (let i = 0; i < fields.length; i++) {
            fields[i]._dataIndex = i;
        }

        return fields;
    }

    /**
     * Returns all methods of this class.
     * @param shallow {boolean} whether to only get the methods of this class and not its inherited classes.
     * @returns {Method[]}
     */
    output.methods = function (shallow = false) {
        const methods = [];

        function addMethods(data, index) {
            if (exists(data[PROPERTY.METHODS])) {
                for (let i = 0; i < data[PROPERTY.METHODS].length; i++) {
                    methods.push(getMethod(data[PROPERTY.METHODS][i], output.getTypeVariableMap()));
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
        if (methods.length === 0) {
            return [];
        }
        for (let i = 0; i < methods.length; i++) {
            methods[i]._dataIndex = i;
        }

        return methods;
    }

    output.constructors = function () {
        const constructors = [];
        if (exists(this.data[PROPERTY.CONSTRUCTORS])) {
            for (let i = 0; i < this.data[PROPERTY.CONSTRUCTORS].length; i++) {
                this.data[PROPERTY.CONSTRUCTORS][i]._dataIndex = i;
                constructors.push(getConstructor(this.data[PROPERTY.CONSTRUCTORS][i], output.getTypeVariableMap()));
            }
        }

        if (constructors.length === 0) {
            return [];
        }
        return constructors;
    }

    output.annotations = function () {
        const annotations = [];
        this._follow_inheritance((data) => {
            if (exists(data[PROPERTY.ANNOTATIONS])) {
                for (let i = 0; i < data[PROPERTY.ANNOTATIONS].length; i++) {
                    data[PROPERTY.ANNOTATIONS][i]._dataIndex = i;
                    annotations.push(getAnnotation(data[PROPERTY.ANNOTATIONS][i], output.getTypeVariableMap()));
                }
            }
        });


        return annotations;
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
        // return [];
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
                            return getClass(field.type()).id() === this.id();
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
                            return getClass(method.type()).id() === this.id();
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
                            return method.parameters().some((param) => {
                                return getClass(param.type()).id() === this.id();
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

    output.getHrefLink = output.hrefLink;

    return output;
}


/**
 * Returns a parameter wrapper object with the given parameter data.
 *
 * @param {ParameterIdentifier} parameterID The blob of parameter data
 * @param {TypeVariableMap} typeVariableMap The type variable map to use for this parameter.
 * @returns {Parameter}
 */
function getParameter(parameterID, typeVariableMap = {}) {
    if (typeof parameterID !== "number") {
        console.error("Invalid parameter type for parameter:", parameterID);
        throw new Error("Invalid parameter type for parameter: " + parameterID);
    }
    const paramData = getParameterData(parameterID);

    let output = {};
    output.data = paramData;
    output._type_variable_map = typeVariableMap;

    output = setBasicName(output);
    output = setRemapType(output);
    output = setModifiers(output);
    output = setAnnotations(output);
    output = setDataIndex(output);
    output = setTypeBasedID(output);
    output = setTypeVariableMap(output);


    return output;
}

/**
 * Returns a method wrapper object with the given method data.
 *
 * @param methodData The blob of method data
 * @param typeVariableMap The type variable map to use for this method.
 * @returns {Method}
 */
function getMethod(methodData, typeVariableMap = {}) {
    if (!exists(methodData)) {
        throw new Error("Invalid method data: " + methodData);
    }

    let output = {};
    output.data = methodData;
    output._type_variable_map = typeVariableMap;

    output = setBasicName(output);
    output = setRemapType(output);
    output = setModifiers(output);
    output = setAnnotations(output);
    output = setParameters(output);
    output = setDataIndex(output);
    output = setDeclaringClass(output);
    output = setTypeVariables(output);
    output = setTypeVariableMap(output);

    output.toKubeJSStaticCall = function () {
        let parent = getClass(this.getDeclaringClass());
        let out = `// KJSODocs: ${this.hrefLink()}\n$${parent.simplename(this.getTypeVariableMap()).toUpperCase()}.${this.name()}(`;
        const params = this.parameters();
        for (let i = 0; i < params.length; i++) {
            out += params[i].name();
            if (i < params.length - 1) {
                out += ", ";
            }
        }
        out += `);`;
        return out;
    }

    output.toKubeJSCode = output.toKubeJSStaticCall;

    output.id = function () {
        // Generate a unique HTML ID for this method
        return getClass(this.getDeclaringClass()).fullyQualifiedName(this.getTypeVariableMap()) + "." + this.name() + "(" + this.parameters().map((param) => {
            return param.id();
        }).join(",") + ")";
    }

    output.getId = output.id;

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.id());
        return url.href();
    }

    output.getHrefLink = output.hrefLink;

    return output;
}

/**
 * Returns a field wrapper object with the given field data.
 *
 * @param fieldData The blob of field data
 * @param typeVariableMap The type variable map to use for this field.
 * @returns {Field}
 */
function getField(fieldData, typeVariableMap = {}) {
    if (!exists(fieldData)) {
        throw new Error("Invalid field data: " + fieldData);
    }

    let output = {};
    output.data = fieldData;
    output._type_variable_map = typeVariableMap;

    output = setBasicName(output);
    output = setRemapType(output);
    output = setModifiers(output);
    output = setAnnotations(output);
    output = setDataIndex(output);
    output = setDeclaringClass(output);
    output = setTypeVariableMap(output);

    output.toKubeJSStaticReference = function () {
        let parent = getClass(this.getDeclaringClass());
        return `// KJSODocs: ${getClass(this.type()).hrefLink()}\n$${parent.simplename(this.getTypeVariableMap()).toUpperCase()}.${this.name()};`;
    }

    output.toKubeJSCode = output.toKubeJSStaticReference;

    output.id = function () {
        // Generate a unique HTML ID for this field
        return getClass(this.getDeclaringClass()).fullyQualifiedName(this.getTypeVariableMap()) + "." + this.name();
    }

    output.getId = output.id;

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.id());
        return url.href();
    }

    output.getHrefLink = output.hrefLink;

    return output;
}

/**
 * Returns a constructor wrapper object with the given constructor data.
 *
 * @param {Object} constructorData The blob of constructor data
 * @param {TypeVariableMap} typeVariableMap The type variable map to use for this constructor.
 * @returns {Constructor}
 */
function getConstructor(constructorData, typeVariableMap = {}) {
    if (!exists(constructorData)) {
        throw new Error("Invalid constructor data: " + constructorData);
    }
    let output = {};
    output.data = constructorData;
    output._type_variable_map = typeVariableMap;

    output = setModifiers(output);
    output = setAnnotations(output);
    output = setParameters(output);
    output = setDataIndex(output);
    output = setDeclaringClass(output);
    output = setTypeVariableMap(output);

    output.toKubeJSStaticCall = function () {
        let parent = getClass(this.getDeclaringClass());
        let out = `// KJSODocs: ${this.hrefLink()}\nlet ${parent.simplename(this.getTypeVariableMap())} = new $${parent.simplename(this.getTypeVariableMap()).toUpperCase()}(`;
        const params = this.parameters();
        for (let i = 0; i < params.length; i++) {
            out += params.name();
            if (i < params.length - 1) {
                out += ", ";
            }
        }
        out += ");";
        return out;
    }

    output.toKubeJSCode = output.toKubeJSStaticCall;

    output.id = function () {
        // Generate a unique HTML ID for this constructor
        return getClass(output.getDeclaringClass()).fullyQualifiedName(output.getTypeVariableMap()) + ".__init__(" + output.parameters().map((param) => {
            return param.id();
        }).join(",") + ")";
    }

    output.getId = output.id;

    output.hrefLink = function () {
        let url = DecodeURL();
        url.params.set("focus", this.id());
        return url.href();
    }

    output.getHrefLink = output.hrefLink;

    return output;
}

/**
 * Returns an annotation wrapper object with the given annotation data.
 *
 * @param {Object} annotationData
 * @param {TypeVariableMap} typeVariableMap
 * @returns {Annotation}
 */
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

    output = setRemapType(output);
    output = setTypeVariableMap(output);
    output = setDataIndex(output);

    output.string = function () {
        if (exists(this.data[PROPERTY.ANNOTATION_STRING])) {
            return (this.data[PROPERTY.ANNOTATION_STRING]);
        } else {
            return "";
        }
    }

    output.getString = output.string;

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
