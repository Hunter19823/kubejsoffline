// This class is dedicated to calculating the relationships between each class.

/**
 * Adds a relationshipType, a source, and a list of targets to the relationship graph.
 * Also handles initializing parts of the relationship graph if they don't exist.
 *
 * @param relationshipType {string}
 * @param from {number}
 * @param to {number}
 */
function addToRelationshipGraph(relationshipType, from, to) {
    if (!RELATIONSHIP_GRAPH.has(relationshipType)) {
        RELATIONSHIP_GRAPH.set(relationshipType, new Map());
    }
    const relationshipMap = RELATIONSHIP_GRAPH.get(relationshipType);
    if (!relationshipMap.has(from)) {
        relationshipMap.set(from, new Set());
    }
    relationshipMap.get(from).add(to);
}

function invertRelationship(relationshipType, newRelationshipType) {
    if (!RELATIONSHIP_GRAPH.has(relationshipType)) {
        return;
    }
    RELATIONSHIP_GRAPH.get(relationshipType).forEach((targets, from) => {
        targets.forEach(to => {
            addToRelationshipGraph(newRelationshipType, to, from);
        });
    });
}

function mergeRelationships(relationshipType, newRelationshipType) {
    if (!RELATIONSHIP_GRAPH.has(relationshipType)) {
        return;
    }
    RELATIONSHIP_GRAPH.get(relationshipType).forEach((targets, from) => {
        targets.forEach(to => {
            addToRelationshipGraph(newRelationshipType, from, to);
        });
    });
}

function markRelationship(from, targets, relationshipTypes, inverseRelationshipTypes) {
    if (!exists(targets)) {
        return;
    }
    // Assume source is already a wrapped class.
    if (targets.length === 0) {
        return;
    }

    const uniqueTargets = new Set(targets);
    uniqueTargets.forEach(to => {
        if (typeof to === 'number') {
            relationshipTypes.forEach(relationshipType => {
                addToRelationshipGraph(relationshipType, from, to);
            });
            inverseRelationshipTypes.forEach(inverseRelationshipType => {
                addToRelationshipGraph(inverseRelationshipType, to, from);
            });
        }
    });
}


/**
 * This function marks all known relationships between itself and other classes
 * using the data provided in the class data.
 * @param target {number} the id of the class
 */
async function indexClass(target) {
    const classType = getClass(target);
    classType._follow_inheritance((parent, index) => {
        if (index === target) {
            return;
        }
        markRelationship(
                target,
                [getClass(parent).id()],
                [RELATIONSHIP.INHERITS, RELATIONSHIP.REFERENCES],
                [RELATIONSHIP.INHERITS, RELATIONSHIP.REFERENCES]
        );
    })
    markRelationship(
            target,
            getAsArray(classType.getSuperClass()),
            [RELATIONSHIP.SUPER_CLASS],
            []
    );
    markRelationship(
            target,
            classType.constructors(true).flatMap((constructorData) => constructorData.getParameters().map((parameterData) => parameterData.getType())),
            [RELATIONSHIP.CONSTRUCTOR_PARAMETER_TYPE, RELATIONSHIP.PARAMETER_TYPE, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            classType.fields(true).map((fieldData) => fieldData.getType()),
            [RELATIONSHIP.FIELD_TYPE, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            classType.methods(true).map((methodData) => methodData.getType()),
            [RELATIONSHIP.METHOD_RETURN_TYPE, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            classType.methods(true).flatMap((methodData) => methodData.getParameters().map((parameterData) => parameterData.getType())),
            [RELATIONSHIP.METHOD_PARAMETER_TYPE, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            getAsArray(classType.getDeclaringClass()),
            [RELATIONSHIP.DECLARING_CLASS, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.DECLARES_CLASS, RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            getAsArray(classType.getEnclosingClass()),
            [RELATIONSHIP.ENCLOSING_CLASS, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.ENCLOSES_CLASS, RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            classType.getInnerClasses(),
            [RELATIONSHIP.ENCLOSES_CLASS, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.ENCLOSING_CLASS, RELATIONSHIP.REFERENCED_BY]
    );
    markRelationship(
            target,
            classType.getTypeVariables().map((typeVariableData) => getClass(typeVariableData)).map((typeVariable) => typeVariable.id()),
            [RELATIONSHIP.TYPE_VARIABLE_OF, RELATIONSHIP.REFERENCES],
            [RELATIONSHIP.COMPONENT_OF, RELATIONSHIP.REFERENCED_BY]
    );
}

async function optimizeDataSearch() {
    DATA._optimized = true;
    DATA._wildcard_types = [];
    DATA._parameterized_types = [];
    DATA._raw_types = [];
    DATA._type_variables = [];
    const indexPromises = [];

    for (let i = 0; i < DATA.types.length; i++) {
        const typeData = getTypeData(i);
        if (!exists(typeData)) {
            console.error("Invalid type data in export: ", i);
            continue;
        }
        typeData._id = i;
        const subject = getClass(i);
        indexPromises.push(indexClass(subject));
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
    await Promise.all(indexPromises);
}

async function indexAllClasses() {
    const classCount = DATA.types.length;
    const logFrequency = Math.max(1, Math.floor(classCount / 100));
    let timeStart = Date.now();
    for (let i = 0; i < DATA.types.length; i++) {
        await (indexClass(i));
        if (i % logFrequency === 0) {
            console.debug(`Indexed ${i} out of ${classCount} classes`);
        }
    }
    let timeEnd = Date.now();
    console.log(`Indexing took ${timeEnd - timeStart}ms`);
    console.debug(`Finished Indexing ${classCount} out of ${classCount} classes`);
}