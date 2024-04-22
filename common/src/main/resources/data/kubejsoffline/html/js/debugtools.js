// This file does not get included in the build, but is used for
// ide Type hinting and debugging.

function countCircularReferences() {
    for (let prop of Object.entries(PROPERTY)) {
        let circularReferences = 0;
        DATA.types.forEach((data, index) => {
            if (data[prop[1]] !== index) {
                return;
            }
            circularReferences++;
        });
        if (circularReferences === 0) continue;
        console.log(`Property '${prop[0]}' has ${circularReferences} Circular References.`);
    }
}


function findBrokenClassNames() {
    let brokenNames = [];
    DATA.types.forEach((data, index) => {
        try {
            getClass(index).name();
        } catch (e) {
            brokenNames.push([data, index, e]);
            if (brokenNames.length > 100) {
                console.log("Breaking after 100 broken names.", brokenNames);
                throw new Error("Too many broken names.");
            }
        }
    })
    console.log("Total Broken Names: " + brokenNames.length);
    return brokenNames;
}

function findInvalidNames() {
    let invalidNames = [];
    for (let i = 0; i < DATA.types.length; i++) {
        try {
            let name = getClass(i).name();
            // If it's blank
            if (name.trim().length === 0) {
                invalidNames.push([name, i]);
            }
            // If it ends with a period
            if (name.trim().endsWith(".")) {
                invalidNames.push([name, i]);
            }
            // If it starts with a period
            if (name.trim().startsWith(".")) {
                invalidNames.push([name, i]);
            }
            // If it contains an empty generic definition
            if (name.trim().includes("<>")) {
                invalidNames.push([name, i]);
            }
        } catch (e) {
            invalidNames.push([e, i]);
        }
    }
    console.log("Total Invalid Names: " + invalidNames.length);
    return invalidNames;
}

function findAllTypeVariablesWithInvalidState() {
    let invalid = [];
    DATA.types.forEach((data, index) => {
        if (!getClass(index).isParameterizedType())
            return;
        if (!exists(data[PROPERTY.TYPE_VARIABLES]))
            return;
        let actualTypes = _getAsArray(data[PROPERTY.TYPE_VARIABLES]);
        let typeVariables = getClass(data[PROPERTY.RAW_PARAMETERIZED_TYPE]).getTypeVariables();
        if (actualTypes.length !== typeVariables.length) {
            invalid.push([data, index, actualTypes, typeVariables]);
        }
    });
    console.log("Total Invalid Type Variables: " + invalid.length);
    return invalid;
}

function findWeirdestNames() {
    let weirdNames = [];
    DATA.types.forEach((data, index) => {
        try {
            let name = getClass(index).name();
            // If the name contains more than 2 periods.
            if (name.split(".").length > 2) {
                weirdNames.push([name, data, index]);
            }
            // If the name contains more than 2 generics.
            if (name.split("<").length > 2) {
                weirdNames.push([name, data, index]);
            }
        } catch (e) {
            console.error(e);
            console.log(data, index);
            throw e;
        }
    });
    console.log("Total Weird Names: " + weirdNames.length);
    return weirdNames.sort((a, b) => {
        return a[0].length - b[0].length;
    });
}

function getRawClasses() {
    let rawClasses = [];
    DATA.types.forEach((data, index) => {
        if (getClass(index).isRawClass()) {
            rawClasses.push([deobfuscateData(data), index]);
        }
    });
    console.log("Total Raw Classes: " + rawClasses.length);
    return rawClasses;
}

function getParameterizedClasses() {
    let parameterizedClasses = [];
    DATA.types.forEach((data, index) => {
        if (getClass(index).isParameterizedType()) {
            parameterizedClasses.push([deobfuscateData(data), index]);
        }
    });
    console.log("Total Parameterized Classes: " + parameterizedClasses.length);
    return parameterizedClasses;
}

function getWildcardClasses() {
    let wildcardClasses = [];
    DATA.types.forEach((data, index) => {
        if (getClass(index).isWildcard()) {
            wildcardClasses.push([deobfuscateData(data), index]);
        }
    });
    console.log("Total Wildcard Classes: " + wildcardClasses.length);
    return wildcardClasses;
}

function getTypeVariables() {
    let typeVariables = [];
    DATA.types.forEach((data, index) => {
        if (getClass(index).isTypeVariable()) {
            typeVariables.push([deobfuscateData(data), index]);
        }
    });
    console.log("Total Type Variables: " + typeVariables.length);
    return typeVariables;
}

function getProperNameOfRawClass(id) {
    const classType = getClass(id);
    if (!classType.isRawClass()) {
        console.error("Type is not a raw class. Cannot get proper name.");
    }
    const typeVariableMap = createTypeVariableMap(id);
    return getGenericDefinitionLogic(classType, typeVariableMap, false);
}