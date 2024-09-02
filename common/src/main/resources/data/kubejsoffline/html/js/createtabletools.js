function createMethodTable(id, typeVariableMap = {}) {
    let target = getClass(id);
    let methods = target.methods();
    if (!(methods && GLOBAL_SETTINGS.showMethods)) {
        return;
    }
    methods = methods.filter((method) => {
        if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(method.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(method.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showMethodsInherited === false && method.getDeclaringClass() != id) {
            return false;
        }
        return true;
    });
    if (methods.length === 0) {
        return;
    }
    /**
     * Adds a method to a table
     * @param table{HTMLTableElement} The table to add the method to.
     * @param method{Method} The method to add
     */
    const addToTable = (table, method) => {
        try {
            appendAttributesToMethodTableRow(
                addRow(
                    table,
                    createMethodSignature(method, typeVariableMap),
                    span(MODIFIER.toString(method.getModifiers())),
                    createFullSignature(method.type()),
                    span(method.name()),
                    tagJoiner(
                        method.getParameters(),
                        ", ",
                        (param) => createParameterSignature(param, param.getTypeVariableMap())
                    ),
                    tagJoiner(
                        method.getTypeVariables(),
                        ", ",
                        (typeVariable) => createFullSignature(typeVariable, method.getTypeVariableMap())
                    ),
                    createFullSignature(method.getDeclaringClass())
                ),
                    method.getDeclaringClass(),
                    method,
                    target.id()
            );
        } catch (e) {
            console.error("Failed to create method entry for ", id, " method: ", method, " Error: ", e);
        }
    }
    createPagedTable('Methods', 'methods', methods, addToTable,
        'Link',
        'Signature',
        'Access Modifiers',
        'Return Type',
        'Method Name',
        'Parameters',
        'Type Variables',
        'Declared In'
    )
            .sortableByMethod((a) => a)
            .create();
}

function createFieldTable(id, typeVariableMap = {}) {
    let target = getClass(id);
    let fields = target.fields();
    if (!(fields && GLOBAL_SETTINGS.showFields)) {
        return;
    }
    fields = fields.filter((field) => {
        if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(field.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(field.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showFieldsInherited === false && field.getDeclaringClass() != id) {
            return false;
        }
        return true;
    });
    if (fields.length === 0) {
        return;
    }
    const addToTable = (table, field) => {
        try {
            appendAttributesToFieldTableRow(
                addRow(
                    table,
                    createFieldSignature(field, typeVariableMap),
                    span(MODIFIER.toString(field.modifiers())),
                    createFullSignature(field.type()),
                    span(field.getName()),
                    createFullSignature(field.getDeclaringClass())
                ),
                    field.getDeclaringClass(),
                    field,
                    target.id()
            );
        } catch (e) {
            console.error("Failed to create field entry for ", id, " field: ", field, " Error: ", e);
        }
    }
    createPagedTable('Fields', 'fields', fields, addToTable, 'Link', 'Signature', 'Access Modifiers', 'Type', 'Field Name', 'Declaring Class')
            .sortableByField((a) => a)
            .create();
}

function createConstructorTable(id, typeVariableMap = {}) {
    let target = getClass(id);
    let constructors = target.constructors();
    if (!(constructors && GLOBAL_SETTINGS.showConstructors)) {
        return;
    }
    constructors = [...constructors].filter((constructor) => {
        if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(constructor.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(constructor.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showConstructorsInherited === false && constructor.getDeclaringClass() !== id) {
            return false;
        }
        return true;
    });
    if (constructors.length === 0) {
        return;
    }
    const addToTable = (table, constructor) => {
        try {
            appendAttributesToConstructorTableRow(
                addRow(
                    table,
                    createConstructorSignature(constructor, id, typeVariableMap),
                    span(MODIFIER.toString(constructor.modifiers())),
                    tagJoiner(
                        constructor.getParameters(),
                        ", ",
                        (param) => createParameterSignature(param, param.getTypeVariableMap())
                    ),
                    tagJoiner(
                        constructor.getTypeVariables(),
                        ", ",
                        (typeVariable) => createFullSignature(typeVariable, constructor.getTypeVariableMap())
                    ),
                    createFullSignature(constructor.getDeclaringClass())
                ),
                    constructor.getDeclaringClass(),
                    constructor,
                    target.id()
            );
        } catch (e) {
            console.error("Failed to create constructor table for ", target.id(), " Constructor: ", constructor, " Error: ", e);
        }
    }
    createPagedTable('Constructors', 'constructors', constructors, addToTable,
        'Link',
        'Constructors',
        'Access Modifiers',
        'Parameters',
        'Type Variables',
        'Declared In'
    )
            .create();
}

function createRelationshipTable(id, typeVariableMap = {}) {
    let data = getClass(id);
    if (!GLOBAL_SETTINGS.showRelationships) {
        return;
    }
    const relationships = getAllRelations(data.id());
    if (relationships.size === 0) {
        return;
    }
    const addToTable = (table, [to, relations]) => {
        try {
            let row = addRow(
                table,
                createFullSignature(to, typeVariableMap),
                span(relations.join(","))
            );
            appendAttributesToRelationshipToTableRow(row, to, relations, data.id())
        } catch (e) {
            console.error("Failed to create relationship entry for ", data.id(), " To: ", to, " Relations: ", relations, " Error: ", e);
        }
    };
    createPagedTable('Relationships', 'relations', [...relationships.entries()], addToTable,
        'Links',
        'RelatedClass',
        'Relationships'
    )
        .sortableByRelation()
        .create();
}

function createClassTable(title, table_id, classes) {

    const addToTable = (table, subject) => {
        try {
            let row = addRow(
                table,
                createFullSignature(subject),
                span(MODIFIER.toString(subject.modifiers())),
                span(subject.getPackage()),
                span(subject.getSimpleName()),
                tagJoiner(
                    subject.getTypeVariables(),
                    ", ",
                    (typeVariable) => createFullSignature(typeVariable, subject.getTypeVariableMap())
                )
            );
            appendAttributesToClassTableRow(row, subject)
        } catch (e) {
            console.error(`Failed to create entry for `, table_id, " Class: ", subject, " Error: ", e);
        }
    };

    createPagedTable(title, table_id, classes, addToTable,
        'Link',
        'Signature',
        'Modifiers',
        'Package',
        'Name',
        'Type Variables'
    )
        .sortableByClass((a) => a)
        .create();
}
