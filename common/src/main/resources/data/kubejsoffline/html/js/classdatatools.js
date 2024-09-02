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

    if (!exists(DATA.types[id]._id))
        DATA.types[id]._id = id;

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

/**
 * Returns the class with the given name, or null if the class does not exist.
 * @param name {FullTypeName | TypeName | SimplifiedTypeName}
 * @returns {JavaType | null}
 */
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

    const shouldUseReferenceName = containsPackage && containsGeneric;
    const shouldUseFullyQualifiedName = containsPackage && !containsGeneric;
    const shouldUseName = !containsPackage && containsGeneric;
    const shouldUseSimpleName = !containsPackage && !containsGeneric;

    function createFilter(filterName) {
        return function (type) {
            if (shouldUseReferenceName && type.referenceName() === name) {
                console.debug("Found " + filterName + " using referenceName: ", type.referenceName());
                return type;
            }
            if (shouldUseFullyQualifiedName && type.fullyQualifiedName(type.getTypeVariableMap(), false) === name) {
                console.debug("Found " + filterName + " using fully qualified name: ", type.fullyQualifiedName(type.getTypeVariableMap(), false));
                return type;
            }
            if (shouldUseName && type.name() === name) {
                console.debug("Found " + filterName + " using name: ", type.name());
                return type;
            }
            if (shouldUseSimpleName && type.simplename() === name) {
                console.debug("Found " + filterName + " using simple name: ", type.simplename());
                return type;
            }
        }
    }

    const wildCardFilter = createFilter("wildcard type");
    const parameterizedFilter = createFilter("parameterized type");
    const rawFilter = createFilter("raw class type");
    const typeVariableFilter = createFilter("type variable");

    console.debug(
        "Searching for class: ", name,
        "isArray: ", isArray,
        "containsGeneric: ", containsGeneric,
        "containsInnerClass: ", containsInnerClass,
        "isParameterized: ", isParameterized,
        "isWildcard: ", isWildcard,
        "containsPackage: ", containsPackage,
        "shouldUseReferenceName: ", shouldUseReferenceName,
        "shouldUseFullyQualifiedName: ", shouldUseFullyQualifiedName,
        "shouldUseName: ", shouldUseName,
        "shouldUseSimpleName: ", shouldUseSimpleName
    );

    if (isWildcard) {
        return DATA._wildcard_types.map((index) => getClass(index)).find(wildCardFilter) ?? null;
    }

    if (isParameterized) {
        const result = DATA._parameterized_types.map((index) => getClass(index)).find(parameterizedFilter);
        if (exists(result))
            return result;
        return DATA._raw_types.map((index) => getClass(index)).find(rawFilter) ?? null;
    }

    const out = DATA._raw_types.map((index) => getClass(index)).find(rawFilter);
    if (exists(out))
        return out;
    return DATA._type_variables.map((index) => getClass(index)).find(typeVariableFilter) ?? null;
}

/**
 * Looks up and wraps the class with the given id, name, or object, and returns the
 * wrapped class. If the class is not found, then null is returned.
 * @param id {TypeIdentifier | FullTypeName | TypeName | SimplifiedTypeName | IndexedClassData}
 * @returns {JavaType | null}
 */
function getClass(id) {
    let output = {};
    if (!exists(id)) {
        console.error("Invalid class id: " + id);
        return null;
    }
    switch (typeof (id)) {
        case "number":
            if (!exists(getTypeData(id))) {
                console.error("Invalid class data: " + id);
                return null;
            }
            output.data = getTypeData(id);
            break;
        case "object":
            if (exists(id._id)) {
                output.data = getTypeData(id._id);
            } else if (exists(id.data) && exists(id.data._id)) {
                output.data = getTypeData(id.data._id);
            } else if (Array.isArray(id) && id.length === 2) {
                // If it's an array, then assume it's an array of a class.
                // the first index is the array type, the depth is the second index,
                output.data = getTypeData(id[0]);
                output.data._id = id[0];
                output._array_depth = id[1];
            } else {
                throw new Error("Invalid class object: " + id);
            }
            break;
        case "string":
            // See if the string is a number
            let num = parseInt(id);
            if (!isNaN(num)) {
                return getClass(num);
            }
            // Look up the cache to see if the class has already been found before.
            if (LOOK_UP_CACHE.has(id)) {
                return getClass(LOOK_UP_CACHE.get(id));
            }
            // Check if the string matches the java qualified type name regex
            if (!id.match(/([a-zA-Z_$][a-zA-Z\d_$]*\.)*[a-zA-Z_$][a-zA-Z\d_$]*/)) {
                // Class does not match a valid java qualified type name, so return null
                console.error("Invalid class id/search: " + id);
                return null;
            }
            const subject = findClassByName(id);
            if (exists(subject)) {
                LOOK_UP_CACHE.set(id, subject.id());
            }
            return subject;
        default:
            console.error("Unsupported class type provided to getClass: " + id + " (" + typeof (id) + ")");
            return null;
    }

    if (!exists(output.data)) {
        console.error("Invalid class data: ", id, typeof (id));
    }


    output = setModifiers(output);
    output = setAnnotations(output);


    // ========================================
    //        Start of Raw Class Properties
    // ========================================

    /**
     * Whether this type is a Class type.
     * This means the Type extends Class<?> and is not a parameterized type.
     * @returns {*}
     */
    output.isRaw = function () {
        return exists(this.data[PROPERTY.CLASS_NAME])
    }

    output.isRawClass = output.isRaw;

    output.isRawType = output.isRaw;

    output.getInterfaces = function () {
        return getAsArray(this.data[PROPERTY.INTERFACES]);
    }

    output.superclass = function () {
        if (exists(this.data[PROPERTY.SUPER_CLASS])) {
            return this.data[PROPERTY.SUPER_CLASS];
        }

        return null;
    }

    output.getSuperClass = function () {
        return this.data[PROPERTY.SUPER_CLASS];
    }

    output.getEnclosingClass = function () {
        return this.data[PROPERTY.ENCLOSING_CLASS];
    }

    output.getDeclaringClass = function () {
        return this.data[PROPERTY.DECLARING_CLASS];
    }

    output.getInnerClasses = function () {
        return getAsArray(this.data[PROPERTY.INNER_CLASSES]);
    }

    /**
     * Returns all fields of this class.
     * @param shallow {boolean} whether to only get the fields of this class and not its inherited classes.
     * @returns {Field[]} an array of fields
     */
    output.fields = function (shallow = false) {
        const fields = [];

        function addFields(data, declaringClass) {
            if (exists(data.data[PROPERTY.FIELDS])) {
                for (let i = 0; i < data.data[PROPERTY.FIELDS].length; i++) {
                    fields.push(getField(data.data[PROPERTY.FIELDS][i], output.getTypeVariableMap()));
                }
            }
        }

        if (shallow) {
            addFields(this, this.id());
        } else {
            this._follow_inheritance((data, index) => {
                addFields(data, index);
            });
        }
        if (fields.length === 0) {
            return [];
        }

        for (let i = 0; i < fields.length; i++) {
            fields[i].data._dataIndex = i;
        }

        return fields;
    }

    output.getFields = output.fields;

    /**
     * Returns all methods of this class.
     * @param shallow {boolean} whether to only get the methods of this class and not its inherited classes.
     * @returns {Method[]}
     */
    output.methods = function (shallow = false) {
        const methods = [];

        function addMethods(data, index) {
            if (exists(data.data[PROPERTY.METHODS])) {
                for (let i = 0; i < data.data[PROPERTY.METHODS].length; i++) {
                    methods.push(getMethod(data.data[PROPERTY.METHODS][i], output.getTypeVariableMap()));
                }
            }
        }

        if (shallow) {
            addMethods(this, this.id());
        } else {
            this._follow_inheritance((data, index) => {
                addMethods(data, index);
            });
        }
        if (methods.length === 0) {
            return [];
        }
        for (let i = 0; i < methods.length; i++) {
            methods[i].data._dataIndex = i;
        }

        return methods;
    }

    output.getMethods = output.methods;

    output.constructors = function () {
        const constructors = [];
        if (exists(this.data[PROPERTY.CONSTRUCTORS])) {
            for (let i = 0; i < this.data[PROPERTY.CONSTRUCTORS].length; i++) {
                constructors.push(getConstructor(this.data[PROPERTY.CONSTRUCTORS][i], output.getTypeVariableMap()));
            }
        }

        if (constructors.length === 0) {
            return [];
        }

        for (let i = 0; i < constructors.length; i++) {
            constructors[i].data._dataIndex = i;
        }

        return constructors;
    }

    output.getConstructors = output.constructors;

    // ========================================
    //        End of Raw Class Properties
    // ========================================


    // ========================================
    //  Start of Parameterized Type Properties
    // ========================================

    /**
     * Whether this type is a parameterized type.
     * This means the Type extends ParameterizedType or a subclass of ParameterizedType.
     * @returns {*}
     */
    output.isParameterized = function () {
        return exists(this.data[PROPERTY.RAW_PARAMETERIZED_TYPE]);
    }

    output.isParameterizedType = output.isParameterized;

    output.getRawType = function () {
        return this.data[PROPERTY.RAW_PARAMETERIZED_TYPE];
    }

    output.getOwnerType = function () {
        return this.data[PROPERTY.OWNER_TYPE];
    }

    output = setTypeVariables(output);

    // ========================================
    //   End of Parameterized Type Properties
    // ========================================


    // ========================================
    //    Start of Wildcard Type Properties
    // ========================================

    /**
     * Whether this type is a wildcard type.
     * This means the Type extends WildcardType.
     * @returns {boolean|*}
     */
    output.isWildcard = function () {
        return Object.keys(this.data).filter((key) => key.indexOf("_") !== 0).length === 0 || exists(this.data[PROPERTY.WILDCARD_LOWER_BOUNDS]) || exists(this.data[PROPERTY.WILDCARD_UPPER_BOUNDS])
    }

    output.isWildcardType = output.isWildcard;

    output.getLowerBound = function () {
        return getAsArray(this.data[PROPERTY.WILDCARD_LOWER_BOUNDS]);
    }

    output.getUpperBound = function () {
        return getAsArray(this.data[PROPERTY.WILDCARD_UPPER_BOUNDS]);
    }


    // ========================================
    //     End of Wildcard Type Properties
    // ========================================

    // ========================================
    //    Start of TypeVariable Properties
    // ========================================

    /**
     * Whether this type is a type variable.
     * This means the Type extends TypeVariable.
     * @returns {*}
     */
    output.isTypeVariable = function () {
        return exists(this.data[PROPERTY.TYPE_VARIABLE_NAME]);
    }

    output.getTypeVariableBounds = function () {
        return getAsArray(this.data[PROPERTY.TYPE_VARIABLE_BOUNDS]);
    }

    // ========================================
    //     End of TypeVariable Properties
    // ========================================

    output.getTypeVariableMap = function () {
        if (!exists(this.data._type_variable_map)) {
            this.data._type_variable_map = createTypeVariableMap(this.id());
        }
        return this.data._type_variable_map;
    }

    /**
     * Returns a TypeIdentifier for this class.
     * @returns {TypeIdentifier}
     */
    output.id = function () {
        return this.data._id;
    }
    output.getId = output.id;

    output.referenceName = function (typeVariableMap = {}) {
        return this.fullyQualifiedName(typeVariableMap, true);
    }
    output.getReferenceName = output.referenceName;

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
    output.fullName = output.fullyQualifiedName;
    output.getFullyQualifiedName = output.fullyQualifiedName;
    output.getFullName = output.fullyQualifiedName;


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
    output.getName = output.name;

    output.simplename = function (typeVariableMap = {}) {
        if (this.isWildcard()) {
            return "?" + "[]".repeat(this.getArrayDepth());
        }
        if (this.isTypeVariable()) {
            return decompressString(this.data[PROPERTY.TYPE_VARIABLE_NAME]) + "[]".repeat(this.getArrayDepth());
        }
        if (this.isParameterizedType()) {
            const rawName = getClass(this.getRawType()).getSimpleName(typeVariableMap);
            const ownerPrefix = this.getOwnerType() ? getClass(this.getOwnerType()).getSimpleName(typeVariableMap) + "$" : "";
            return ownerPrefix + rawName + "[]".repeat(this.getArrayDepth());
        }
        return decompressString(this.data[PROPERTY.CLASS_NAME]) + "[]".repeat(this.getArrayDepth());
    }
    output.simpleName = output.simplename;
    output.getSimpleName = output.simplename;

    output.getArrayDepth = function () {
        return (exists(this._array_depth) ? this._array_depth : 0);
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
    output.package = output.getPackageName;
    output.getPackage = output.getPackageName;

    output.getParameterizedArgs = function () {
        return getAsArray(this.data[PROPERTY.PARAMETERIZED_ARGUMENTS]);
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
            const currentClass = getClass(current);
            seen.add(currentClass.id());
            action(currentClass, current);
            unprocessed.push(currentClass.getSuperClass());
            unprocessed.push(...currentClass.getInterfaces());
            if (currentClass.isParameterized()) {
                unprocessed.push(currentClass.getRawType());
            }
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
                    return [...getRelation(RELATIONSHIP.SUPER_CLASS, this.id())]
                case "INNER_TYPE_OF":
                    return [...getRelation(RELATIONSHIP.ENCLOSING_CLASS, this.id())]
                case "COMPONENT_OF":
                    return [...getRelation(RELATIONSHIP.COMPONENT_OF, this.id())]
                case "IMPLEMENTATION_OF":
                    return [...getRelation(RELATIONSHIP.INHERITS, this.id())]
                case "DECLARED_FIELD_TYPE_OF":
                    return [...getRelation(RELATIONSHIP.FIELD_TYPE, this.id())]
                case "DECLARED_METHOD_RETURN_TYPE_OF":
                    return [...getRelation(RELATIONSHIP.METHOD_RETURN_TYPE, this.id())]
                case "DECLARED_METHOD_PARAMETER_TYPE_OF":
                    return [...getRelation(RELATIONSHIP.METHOD_PARAMETER_TYPE, this.id())]
                case "TYPE_VARIABLE_OF":
                    return [...getRelation(RELATIONSHIP.TYPE_VARIABLE_OF, this.id())]
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
        let url = CURRENT_URL.clone();
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
    output = setTypeVariableMap(output);

    output.id = function () {
        return getClass(this.getType()).fullyQualifiedName(this.getTypeVariableMap()) + " " + this.name();
    }

    output.getId = output.id;


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
        let url = CURRENT_URL.clone();
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
        let url = CURRENT_URL.clone();
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
    output = setTypeVariables(output);
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
        let url = CURRENT_URL;
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

/**
 * Returns a relationship wrapper object with the given relationship data.
 *
 * @param {TypeIdentifier} to The type identifier of the class that this relationship is to.
 * @param {Array<Relationship>} relations The relations between the two classes.
 * @returns {Relationship} The relationship wrapper object.
 */
function getRelationship([to, relations]) {
    /**
     * @type {JavaType}
     */
    let output = getClass(to);
    output.data = JSON.parse(JSON.stringify(output.data));
    output._relations = relations;
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
