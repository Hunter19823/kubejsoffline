function splitModifyCollect(input, separator, modifier) {
    let parts = input.split(separator);
    let output = [];
    for (let i = 0; i < parts.length; i++) {
        output.push(modifier(parts[i]));
    }
    return output.join(separator);
}

function compressFirstHalf(input, separator, modifier) {
    let lastIndexOf = input.lastIndexOf(separator);
    if (lastIndexOf === -1) {
        return modifier(input);
    }
    let start = modifier(input.substring(0, lastIndexOf));
    let end = modifier(input.substring(lastIndexOf + 1));
    return start + separator + end;
}

function mapUncompress(part) {
    return STRING_COMPRESSION_DATA[part];
}

function uncompressString(compressedString) {
    return DATA.names[compressedString];
    // return splitModifyCollect(
    // 		compressedString,
    // 		'<',
    // 		(a) => splitModifyCollect(
    // 				a,
    // 				'>',
    // 				(b) => splitModifyCollect(
    // 						b,
    // 						'$',
    // 						(c) => compressFirstHalf(
    // 								c,
    // 								'.',
    // 								mapUncompress
    // 						)
    // 				)
    // 		)
    // );
}

function deobfuscateData(data) {
    if (typeof data === 'number') {
        data = getClass(data).data;
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
function createTypeVariableMap(type) {
    if (!getClass(type).isRawClass()) {
        console.log("Type is not a raw class. Cannot make type variable map for this type.");
        return;
    }
    const typeVariableMap = {};
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
                    unprocessedTypes.push(superClass.rawtype());
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
                    unprocessedTypes.push(interfaceClass.rawtype());
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
    const rawTypeId = classType.rawtype();
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

function getGenericDefinition(type, typeVariableMap) {
    return getGenericDefinitionLogic(type, typeVariableMap, false);
}

function getGenericName(type, typeVariableMap) {
    return getGenericDefinitionLogic(type, typeVariableMap, true);
}

function getGenericDefinitionLogic(type, typeVariableMap, isDefiningTypeVariable) {
    type = getClass(type);
    if (type.isTypeVariable()) {
        type = getClass(exists(typeVariableMap[type]) ? typeVariableMap[type] : type);
    }
    if (type.isRawClass()) {
        return uncompressString(type.data[PROPERTY.CLASS_NAME])
    }
    if (type.isTypeVariable()) {
        const typeVariableName = uncompressString(type.data[PROPERTY.TYPE_VARIABLE_NAME]);
        if (isDefiningTypeVariable) {
            return typeVariableName;
        }
        const bounds = type.getTypeVariableBounds();
        if (bounds.length === 0) {
            return typeVariableName;
        }
        return typeVariableName + joiner(bounds, " & ", (bound) => getGenericName(bound, typeVariableMap), " extends ");
    }
    if (type.isWildcard()) {
        const name = "?";
        const lowerBounds = type.getLowerBound();
        if (lowerBounds.length !== 0) {
            return name + joiner(
                    lowerBounds,
                    " & ",
                    (bound) => getGenericDefinitionLogic(bound, typeVariableMap, isDefiningTypeVariable),
                    " super "
            );
        }
        const upperBounds = type.getUpperBound();
        if (upperBounds.length !== 0) {
            return name + joiner(
                    upperBounds,
                    " & ",
                    (bound) => getGenericDefinitionLogic(bound, typeVariableMap, isDefiningTypeVariable),
                    " extends "
            );
        }
        return name;
    }
    if (type.isParameterizedType()) {
        const actualTypes = type.getTypeVariables();
        const genericArguments = joiner(
                actualTypes,
                ", ",
                (actualType) => getGenericDefinitionLogic(actualType, typeVariableMap, isDefiningTypeVariable),
                "<",
                ">"
        );
        const rawTypeName = getGenericDefinitionLogic(type.rawtype(), typeVariableMap, isDefiningTypeVariable);
        return rawTypeName + genericArguments;
    }

    console.error("Unknown Type! Cannot get generic definition for: ", type.id(), type.data);
    return "Unknown Type";


}