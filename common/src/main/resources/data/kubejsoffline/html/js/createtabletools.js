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
    const addToTable = (table, method) => {
        try {
            appendAttributesToMethodTableRow(
                    addRow(table, createMethodSignature(method, typeVariableMap), createFullSignature(method.type())),
                    method.getDeclaringClass(),
                    method,
                    target.id()
            );
        } catch (e) {
            console.error("Failed to create method entry for ", id, " method: ", method, " Error: ", e);
        }
    }
    createPagedTable('Methods', 'methods', methods, addToTable, 'Link', 'Methods');
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
                    addRow(table, createFieldSignature(field, typeVariableMap), createFullSignature(field.type())),
                    field.getDeclaringClass(),
                    field,
                    target.id()
            );
        } catch (e) {
            console.error("Failed to create field entry for ", id, " field: ", field, " Error: ", e);
        }
    }
    createPagedTable('Fields', 'fields', fields, addToTable, 'Link', 'Fields');
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
                    addRow(table, createConstructorSignature(constructor, id, typeVariableMap)),
                    constructor.getDeclaringClass(),
                    constructor,
                    target.id()
            );
        } catch (e) {
            console.error("Failed to create constructor table for ", target.id(), " Constructor: ", constructor, " Error: ", e);
        }
    }
    createPagedTable('Constructors', 'constructors', constructors, addToTable, 'Link', 'Constructors');
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
            let row = addRow(table, createFullSignature(to, typeVariableMap), span(relations.join(",")));
            appendAttributesToRelationshipToTableRow(row, to, relations, data.id())
        } catch (e) {
            console.error("Failed to create relationship entry for ", data.id(), " To: ", to, " Relations: ", relations, " Error: ", e);
        }
    };
    createPagedTable('Relationships', 'relations', [...relationships.entries()], addToTable, 'RelatedClass', 'Relationships');
}
