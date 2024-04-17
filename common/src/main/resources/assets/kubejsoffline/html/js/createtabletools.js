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
        let c = getConstructor(constructor, typeVariableMap);
        if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(c.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(c.modifiers())) {
            return false;
        }
        if (GLOBAL_SETTINGS.showConstructorsInherited === false && c.getDeclaringClass() !== id) {
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
            cons = getConstructor(constructor, typeVariableMap);
            row = addRow(table, createConstructorSignature(constructor, id, typeVariableMap));
            appendAttributesToConstructorTableRow(row, cons.getDeclaringClass(), cons, target.id());
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
    let table = createTableWithHeaders(createSortableTable('relations'), 'Relationships', 'RelatedClass');
    let seen = new Set();
    let relation = null;
    let row = null;
    for (let i = 0; i < RELATIONS.length; i++) {
        relation = data.relation(i);
        if (!relation) {
            continue;
        }
        for (let j = 0; j < relation.length; j++) {
            try {
                row = addRow(table, span(RELATIONS[i]), createFullSignature(relation[j], typeVariableMap));
                appendAttributesToRelationshipToTableRow(row, relation[j], RELATIONS[i], data.id())
                seen.add(relation[j]);
            } catch (e) {
                console.error("Failed to create relationship entry for ", id, " Relationship: ", relation[j], " Error: ", e);
            }
        }
    }
    if (seen.size === 0) {
        table.parentNode.removeChild(table);
    }
}
