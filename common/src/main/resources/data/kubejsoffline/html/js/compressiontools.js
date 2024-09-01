function cachedFunction(func) {
    const cache = {};

    return function (...args) {
        const key = JSON.stringify(args);
        if (!(key in cache)) {
            cache[key] = func(...args);
        }
        return cache[key];
    };
}


function clearAllCaches() {
    LOOK_UP_CACHE.clear();
    RELATIONSHIP_GRAPH.clear();
    for (let i = 0; i < DATA.types.length; i++) {
        delete DATA.types[i]._name_cache;
        delete DATA.types[i]._cachedInheritedClasses;
        delete DATA.types[i]._cachedPackageName;
        delete DATA.types[i]._id;
    }
    DATA._eventsIndexed = false;
    DATA._optimized = false;
}

function decompressString(compressedString) {
    return DATA.names[compressedString];
}

function deobfuscateData(data) {
    if (typeof data === 'number') {
        data = getClass(data).data;
    }
    if (Array.isArray(data)) {
        return data.map((content) => {
            return deobfuscateData(content);
        });
    }
    let deobfuscatedData = {};
    for (let prop of Object.entries(PROPERTY)) {
        if (exists(data[prop[1]])) {
            deobfuscatedData[prop[0]] = data[prop[1]];
            if (Array.isArray(deobfuscatedData[prop[0]])) {
                deobfuscatedData[prop[0]] = deobfuscatedData[prop[0]].map((content) => {
                    if (typeof content === 'number')
                        return getClass(content).data;
                    return deobfuscateData(content);
                });
            }
        }
    }
    return deobfuscatedData;
}


function joiner(values, separator, transformer = (a) => a, prefix = "", suffix = "") {
    if (!exists(transformer)) {
        transformer = (a) => a;
    }
    let output = prefix;
    for (let i = 0; i < values.length; i++) {
        output += transformer(values[i]);
        // If not the last element, add the separator
        if (i < values.length - 1) {
            output += separator;
        }
    }
    output += suffix;
    return output;

}

/**
 * Creates a TypeVariableMap from a given class.
 * @param type {number} the id of the type
 */
function createTypeVariableMap(type, existingMap = {}) {
    if (!getClass(type).isRawClass()) {
        return existingMap;
    }
    const typeVariableMap = existingMap;
    // Treat like a stack
    const unprocessedTypes = [type];
    while (unprocessedTypes.length > 0) {
        const currentType = unprocessedTypes.pop();
        if (currentType === null) {
            console.error("Current Type is null! Invalid state has been reached.");
            throw new Error("Invalid state has been reached.");
        }
        const currentClass = getClass(currentType);
        if (!currentClass.isRawClass()) {
            console.error("Current Type is not a raw class! Invalid state has been reached.");
            throw new Error("Invalid state has been reached.");
        }
        if (currentClass.superclass() !== null) {
            const superClassId = currentClass.superclass();
            const superClass = getClass(superClassId);
            if (superClass.isRawClass()) {
                unprocessedTypes.push(superClassId);
            } else {
                if (superClass.isParameterizedType()) {
                    unprocessedTypes.push(superClass.getRawType());
                    remapTypeVariables(typeVariableMap, superClassId);
                }
            }
        }
        const interfaces = currentClass.getInterfaces();
        for (let i = 0; i < interfaces.length; i++) {
            const interfaceId = interfaces[i];
            const interfaceClass = getClass(interfaceId);
            if (interfaceClass.isRawClass()) {
                unprocessedTypes.push(interfaceId);
            } else {
                if (interfaceClass.isParameterizedType()) {
                    unprocessedTypes.push(interfaceClass.getRawType());
                    remapTypeVariables(typeVariableMap, interfaceId);
                }
            }
        }

    }

    return typeVariableMap;
}

function remapTypeVariables(typeVariableMap, parameterizedType) {
    const classType = getClass(parameterizedType);
    if (!classType.isParameterizedType()) {
        console.error("Type is not a parameterized type. Cannot remap type variables.");
        throw new Error("Invalid state has been reached.");
    }
    const rawTypeId = classType.getRawType();
    const rawType = getClass(rawTypeId);
    if (!rawType.isRawClass()) {
        console.error("Raw type is not a raw class. Cannot remap type variables.");
        throw new Error("Invalid state has been reached.");
    }
    const typeVariables = rawType.getTypeVariables();
    const actualTypes = classType.getTypeVariables();
    TYPE_LOOP:
        for (let i = 0; i < typeVariables.length; i++) {
            if (exists(typeVariableMap[typeVariables[i]])) {
                continue;
            }
            let actualTypeId = actualTypes[i];
            let actualType = getClass(actualTypeId);
            if (!actualType.isTypeVariable()) {
                typeVariableMap[typeVariables[i]] = actualTypes[i];
                continue;
            }
            let iterationCounter = 0;
            while (exists(typeVariableMap[actualTypeId]) && iterationCounter < 10) {
                const remappedTypeId = typeVariableMap[actualTypeId];
                const remappedType = getClass(remappedTypeId);
                iterationCounter++;
                if (remappedType.isTypeVariable()) {
                    actualTypeId = remappedTypeId;
                    actualType = remappedType;
                } else {
                    typeVariableMap[typeVariables[i]] = remappedTypeId;
                    continue TYPE_LOOP;
                }
            }
            if (iterationCounter >= 1000) {
                throw new Error("Infinite Loop Detected. Cannot remap type variables.");
            }

            typeVariableMap[typeVariables[i]] = actualTypeId;
        }
}

function getGenericDefinition(type, typeVariableMap, includeGenerics = true) {
    return cachedGenericDefinition(type,
        new name_parameters()
            .setTypeVariableMap(typeVariableMap)
            .setDefiningTypeVariable(false)
            .setAppendPackageName(true)
            .setIncludeGenerics(includeGenerics)
    );
}

function getGenericName(type, typeVariableMap, includeGenerics = true) {
    return cachedGenericDefinition(type,
        new name_parameters()
            .setTypeVariableMap(typeVariableMap)
            .setDefiningTypeVariable(false)
            .setAppendPackageName(false)
            .setIncludeGenerics(includeGenerics)
    );
}

function getGenerics(actualTypes, config) {
    if (actualTypes.length === 0 || !config.getIncludeGenerics()) {
        return "";
    }
    return joiner(
        actualTypes,
        ", ",
        (actualType) => cachedGenericDefinition(actualType, config),
        "<",
        ">"
    );

}

function getParameterizedName(type, config) {
    // Append the package name as long as the owner type does not exist and appendPackageName is true
    const rawTypeName = cachedGenericDefinition(type.getRawType(), config.setAppendPackageName(config.getAppendPackageName() && !exists(type.getOwnerType())).disableEnclosingName(true));
    const ownerType = type.getOwnerType();
    const ownerPrefix = (exists(ownerType) && (!config.getDefiningParameterizedType()) ? cachedGenericDefinition(ownerType, config.disableEnclosingName(true)) + "$" : "");
    const actualTypes = type.getTypeVariables();
    const genericArguments = getGenerics(actualTypes, config.disableEnclosingName(true));

    return ownerPrefix + rawTypeName + genericArguments;
}

function getWildcardName(type, config) {
    const name = "?";
    const lowerBounds = type.getLowerBound();
    if (lowerBounds.length !== 0) {

        return name + joiner(
            lowerBounds,
            " & ",
            (bound) => cachedGenericDefinition(bound, config),
            " super "
        );
    }
    const upperBounds = type.getUpperBound();
    if (upperBounds.length !== 0) {
        return name + joiner(
            upperBounds,
            " & ",
            (bound) => cachedGenericDefinition(bound, config),
            " extends "
        );
    }
    return name;
}

function getTypeVariableName(type, config) {
    const typeVariableName = decompressString(type.data[PROPERTY.TYPE_VARIABLE_NAME]);
    if (config.getDefiningTypeVariable()) {
        return typeVariableName;
    }
    const bounds = type.getTypeVariableBounds();
    if (bounds.length === 0) {
        return typeVariableName;
    }
    return typeVariableName + joiner(bounds, " & ", (bound) => cachedGenericDefinition(bound, config.setDefiningTypeVariable(true)), " extends ");
}

function getRawClassName(type, config) {
    const name = decompressString(type.data[PROPERTY.CLASS_NAME])
    if (config.getAppendPackageName()) {
        return type.package() + "." + name;
    } else {
        return name;
    }
}

function getGenericDefinitionLogic(type, config) {
    type = getClass(type);
    if (type.isTypeVariable()) {
        type = config.remapType(type);
    }
    if (type.isRawClass()) {
        if (exists(type.getDeclaringClass()) && !config.getDefiningParameterizedType()) {
            return cachedGenericDefinition(type.getDeclaringClass(), config) + "$" + getRawClassName(type, config);
        }
        return getRawClassName(type, config);
    }
    if (type.isTypeVariable()) {
        return getTypeVariableName(type, config);
    }
    if (type.isWildcard()) {
        return getWildcardName(type, config);
    }
    if (type.isParameterizedType()) {
        return getParameterizedName(type, config);
    }

    console.error("Unknown Type! Cannot get generic definition for: ", type.id(), type.data);
    return "Unknown Type";


}


cachedGenericDefinition = cachedFunction(getGenericDefinitionLogic);

name_parameters = class {
    constructor() {
        this.typeVariableMap = {};
        this.isDefiningTypeVariable = false;
        this.appendPackageName = true;
        this.includeGenerics = false;
        this.isDefiningParameterizedType = false;
    }

    clone() {
        return Object.assign(new name_parameters(), this);
    }

    setTypeVariableMap(typeVariableMap) {
        const clone = this.clone();
        clone.typeVariableMap = typeVariableMap;
        return clone;
    }

    setDefiningTypeVariable(isDefiningTypeVariable) {
        const clone = this.clone();
        clone.isDefiningTypeVariable = isDefiningTypeVariable;
        return clone;
    }

    setAppendPackageName(appendPackageName) {
        const clone = this.clone();
        clone.appendPackageName = appendPackageName;
        return clone;
    }

    setIncludeGenerics(includeGenerics) {
        const clone = this.clone();
        clone.includeGenerics = includeGenerics;
        return clone;
    }

    disableEnclosingName(isDefiningParameterizedType) {
        const clone = this.clone();
        clone.isDefiningParameterizedType = isDefiningParameterizedType;
        return clone;
    }

    getTypeVariableMap() {
        return this.typeVariableMap;
    }

    getDefiningTypeVariable() {
        return this.isDefiningTypeVariable;
    }

    getAppendPackageName() {
        return this.appendPackageName;
    }

    getIncludeGenerics() {
        return this.includeGenerics;
    }

    getDefiningParameterizedType() {
        return this.isDefiningParameterizedType;
    }

    remapType(type) {
        if (typeof type === 'number') {
            if (exists(getTypeVariables()[type])) {
                return getClass(getTypeVariables()[type]);
            }else {
                return getClass(type);
            }
        }
        if (exists(type['isTypeVariable'])) {
            if (exists(this.typeVariableMap[type.id()])) {
                return getClass(this.typeVariableMap[type.id()]);
            }else {
                return type;
            }
        }
        return type;
    }
}

/**
 * A helper function to create a span element
 * that consists of a list of elements.
 * @template T
 * @param values{Array<T>} the values to join
 * @param separator{string} the separator to use
 * @param transformer{function(T): HTMLElement} the transformer to use
 * @param prefix{HTMLElement?} the prefix to use
 * @param suffix{HTMLElement?} the suffix to use
 * @returns {HTMLSpanElement} the span element
 */
function tagJoiner(values, separator, transformer = (a) => span(a), prefix, suffix) {
    if (!exists(transformer)) {
        transformer = (a) => span(a);
    }
    const output = span();
    if (prefix) {
        output.append(prefix);
    }
    for (let i = 0; i < values.length; i++) {
        output.append(transformer(values[i]));
        // If not the last element, add the separator
        if (i < values.length - 1) {
            output.append(span(separator));
        }
    }
    if (suffix) {
        output.append(suffix);
    }
    return output;

}

function getRawClassSignature(type, outputSpan, config) {
    const name = decompressString(type.data[PROPERTY.CLASS_NAME])
    if (exists(type.getDeclaringClass())) {
        outputSpan.append(createLinkableSignature(type.getDeclaringClass(), config));
        outputSpan.append(span('$'));
        config = config.setAppendPackageName(false);
    }
    if (config.getAppendPackageName() && type.package() && typeof type.package() === 'string' && type.package().length > 0) {
        outputSpan.append(span(type.package()));
        outputSpan.append(span('.'));
        if (exists(config.getOverrideID())) {
            outputSpan.append(createLink(span(name), config.getOverrideID()));
        } else {
            outputSpan.append(createLink(span(name), type.id()));
        }
        return outputSpan;
    } else {
        if (exists(config.getOverrideID())) {
            outputSpan.append(createLink(span(name), config.getOverrideID()));
        } else {
            outputSpan.append(createLink(span(name), type.id()));
        }
        return outputSpan;
    }
}

function getTypeVariableSignature(type, outputSpan, config) {
    const typeVariableName = decompressString(type.data[PROPERTY.TYPE_VARIABLE_NAME]);
    if (config.getDefiningTypeVariable()) {
        outputSpan.append(createLink(span(typeVariableName), type.id()));
        return outputSpan;
    }
    const bounds = type.getTypeVariableBounds();
    if (bounds.length === 0) {
        outputSpan.append(createLink(span(typeVariableName), type.id()));
        return outputSpan;
    }
    outputSpan.append(createLink(span(typeVariableName), type.id()));
    outputSpan.append(
        tagJoiner(
            bounds,
            " & ",
            (bound) => createLinkableSignature(
                bound,
                config.setDefiningTypeVariable(true)
            ),
            span(" extends ")
        )
    );
    return outputSpan;
}

function getWildcardSignature(type, outputSpan, config) {
    const name = "?";
    outputSpan.append(span(name));
    const lowerBounds = type.getLowerBound();
    if (lowerBounds.length !== 0) {
        outputSpan.append(
            tagJoiner(
                lowerBounds,
                " & ",
                (bound) => createLinkableSignature(
                    bound,
                    config.setDefiningTypeVariable(true)
                ),
                span(" super ")
            )
        );
        return outputSpan;
    }
    const upperBounds = type.getUpperBound();
    if (upperBounds.length !== 0) {
        outputSpan.append(
            tagJoiner(
                upperBounds,
                " & ",
                (bound) => createLinkableSignature(
                    bound,
                    config.setDefiningTypeVariable(true),
                ),
                span(" extends ")
            )
        );
        return outputSpan;
    }
    return outputSpan;
}

function getParameterizedTypeSignature(type, outputSpan, config) {
    const rawTypeName = createLinkableSignature(
        type.getRawType(),
        config
            .setAppendPackageName(config.getAppendPackageName() && !(type.package().length > 0) && !exists(type.getOwnerType()))
            .disableEnclosingName(true)
            .setOverrideID(type.id())
    );
    const ownerType = type.getOwnerType();
    if (exists(ownerType) && !config.getDefiningParameterizedType()) {
        const ownerPrefix = createLinkableSignature(ownerType, config);
        outputSpan.append(ownerPrefix);
        outputSpan.append(span('$'));
        config = config.setAppendPackageName(false);
    }
    outputSpan.append(rawTypeName);
    const actualTypes = type.getTypeVariables();
    if (actualTypes.length === 0) {
        return outputSpan;
    }
    outputSpan.append(
        tagJoiner(
            actualTypes,
            ", ",
            (actualType) => createLinkableSignature(
                actualType,
                config
            ),
            span("<"),
            span(">")
        )
    );
    return outputSpan;
}

function createLinkableSignature(type, config) {
    type = getClass(type);
    const outputSpan = document.createElement('span');
    if (type.isTypeVariable()) {
        if (config.getDefiningTypeVariable()) {
            outputSpan.append(createLink(span(type.name()), type.id()));
            return outputSpan;
        }
        type = config.remapType(type);
    }
    if (type.isRawClass()) {
        return getRawClassSignature(type, outputSpan, config);
    }
    if (type.isTypeVariable()) {
        return getTypeVariableSignature(type, outputSpan, config);
    }
    if (type.isWildcard()) {
        return getWildcardSignature(type, outputSpan, config);
    }
    if (type.isParameterizedType()) {
        return getParameterizedTypeSignature(type, outputSpan, config);
    }

    console.error("Unknown Type! Cannot get generic definition for: ", type.id(), type.data);
    return span("Unknown Type");
}

signature_parameters = class {
    constructor() {
        this.typeVariableMap = {};
        this.isDefiningTypeVariable = false;
        this.appendPackageName = true;
        this.overrideID = null;
        this.isDefiningParameterizedType = false;
    }

    clone() {
        return Object.assign(new signature_parameters(), this);
    }

    setTypeVariableMap(typeVariableMap) {
        const clone = this.clone();
        clone.typeVariableMap = typeVariableMap;
        return clone;
    }

    setDefiningTypeVariable(isDefiningTypeVariable) {
        const clone = this.clone();
        clone.isDefiningTypeVariable = isDefiningTypeVariable;
        return clone;
    }

    setAppendPackageName(appendPackageName) {
        const clone = this.clone();
        clone.appendPackageName = appendPackageName;
        return clone;
    }

    setOverrideID(overrideID) {
        const clone = this.clone();
        clone.overrideID = overrideID;
        return clone;
    }

    disableEnclosingName(isDefiningParameterizedType) {
        const clone = this.clone();
        clone.isDefiningParameterizedType = isDefiningParameterizedType;
        return clone;
    }

    getTypeVariableMap() {
        return this.typeVariableMap;
    }

    getDefiningTypeVariable() {
        return this.isDefiningTypeVariable;
    }

    getAppendPackageName() {
        return this.appendPackageName;
    }

    getOverrideID() {
        return this.overrideID;
    }

    getDefiningParameterizedType() {
        return this.isDefiningParameterizedType;
    }

    remapType(type) {
        if (typeof type === 'number') {
            if (exists(getTypeVariables()[type])) {
                return getClass(getTypeVariables()[type]);
            }else {
                return getClass(type);
            }
        }
        if (exists(type['isTypeVariable'])) {
            if (exists(this.typeVariableMap[type.id()])) {
                return getClass(this.typeVariableMap[type.id()]);
            }else {
                return type;
            }
        }
        return type;
    }
}

