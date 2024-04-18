function createMethodTable(id, typeVariableMap = {}) {
    let target = getClass(id);
    let methods = target.methods();
    let table = null;
    let method = null;
    let meth = null;
    let row = null;
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
    table = createTableWithHeaders(createSortableTable('methods'), 'Link', 'Methods', 'Return Type');
    for (method of methods) {
        try {
            row = addRow(table, createMethodSignature(method, typeVariableMap), createFullSignature(method.type()));
            appendAttributesToMethodTableRow(row, method.getDeclaringClass(), method, target.id());
        } catch (e) {
            console.error("Failed to create method entry for ", id, " method: ", method, " Error: ", e);
        }
    }
}

function createFieldTable(id, typeVariableMap = {}) {
    let target = getClass(id);
    let fields = target.fields();
    let table = null;
    let data = null;
    let row = null;
    let field = null;
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
    table = createTableWithHeaders(createSortableTable('fields'), 'Link', 'Fields', 'Type');
    for (field of fields) {
        try {
            row = addRow(table, createFieldSignature(field, typeVariableMap), createFullSignature(field.type()));
            appendAttributesToFieldTableRow(row, field.getDeclaringClass(), field, target.id());
        } catch (e) {
            console.error("Failed to create field entry for ", id, " field: ", field, " Error: ", e);
        }
    }
}

function createConstructorTable(id, typeVariableMap = {}) {
    let target = getClass(id);
    let constructors = target.constructors();
    let table = null;
    let constructor = null;
    let row = null;
    let cons = null;
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
    table = createTableWithHeaders(createSortableTable('constructors'), 'Link', 'Constructors');
    for (constructor of constructors) {
        try {
            row = addRow(table, createConstructorSignature(constructor, id, typeVariableMap));
            appendAttributesToConstructorTableRow(row, constructor.getDeclaringClass(), constructor, target.id());
        } catch (e) {
            console.error("Failed to create constructor table for ", target.id(), " Constructor: ", constructor, " Error: ", e);
        }
    }
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
    let table = createTableWithHeaders(createSortableTable('relations'), 'RelatedClass', 'Relationships');
    let row = null;
    [...relationships.entries()].forEach(([to, relations]) => {
        try {
            row = addRow(table, createFullSignature(to, typeVariableMap), span(relations.join(",")));
            appendAttributesToRelationshipToTableRow(row, to, relations, data.id())
        } catch (e) {
            console.error("Failed to create relationship entry for ", data.id(), " To: ", to, " Relations: ", relations, " Error: ", e);
        }
    });
}
