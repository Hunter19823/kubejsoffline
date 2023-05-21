function createMethodTable(id) {
	let methods = getClass(id).methods();
	let table = null;
	let method = null;
	let meth = null;
	let row = null;
	if (methods && GLOBAL_SETTINGS.showMethods) {
		methods = [...methods].filter((method) => {
			let m = getMethod(method);
			if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(m.modifiers())) {
				return false;
			}
			if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(m.modifiers())) {
				return false;
			}
			if (GLOBAL_SETTINGS.showMethodsInherited === false && m.declaringClass() != id) {
				return false;
			}
			return true;
		});
		if (methods.length === 0) {
			return;
		}
		table = createTableWithHeaders(createTable('methods'), 'Methods', 'Return Type');
		for (method of methods) {
			meth = getMethod(method);
			row = addRow(table, createMethodSignature(method), createFullSignature(getMethod(method).returnType()));
			appendAttributesToMethodTableRow(row, meth.declaringClass(), meth);
		}
	}
}

function createFieldTable(id) {
	let fields = getClass(id).fields();
	let table = null;
	let field = null;
	let row = null;
	let data = null;
	if (fields && GLOBAL_SETTINGS.showFields) {
		fields = [...fields].filter((field) => {
			let f = getField(field);
			if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(f.modifiers())) {
				return false;
			}
			if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(f.modifiers())) {
				return false;
			}
			if (GLOBAL_SETTINGS.showFieldsInherited === false && f.declaringClass() != id) {
				return false;
			}
			return true;
		});
		if (fields.length === 0) {
			return;
		}
		table = createTableWithHeaders(createTable('fields'), 'Fields', 'Type');
		for (field of fields) {
			data = getField(field);
			row = addRow(table, createFieldSignature(field), createFullSignature(getField(field).type()));
			appendAttributesToFieldTableRow(row, field.declaringClass(), data);
		}
	}
}
function createConstructorTable(id) {
	let constructors = getClass(id).constructors();
	let table = null;
	let constructor = null;
	let row = null;
	let cons = null;
	if (constructors && GLOBAL_SETTINGS.showConstructors) {
		constructors = [...constructors].filter((constructor) => {
			let c = getConstructor(constructor);
			if (GLOBAL_SETTINGS.showPrivate === false && MODIFIER.isPrivate(c.modifiers())) {
				return false;
			}
			if (GLOBAL_SETTINGS.showProtected === false && MODIFIER.isProtected(c.modifiers())) {
				return false;
			}
			if (GLOBAL_SETTINGS.showConstructorsInherited === false && c.declaringClass() !== id) {
				return false;
			}
			return true;
		});
		if (constructors.length === 0) {
			return;
		}
		table = createTableWithHeaders(createTable('constructors'), 'Constructors');
		for (constructor of constructors) {
			cons = getConstructor(constructor);
			row = addRow(table, createConstructorSignature(constructor, id));
			row.setAttribute('mod', cons.modifiers());
			row.setAttribute('params', cons.parameters());
		}
	}
}

function createRelationshipTable(id) {
	let data = getClass(id);
	if (GLOBAL_SETTINGS.showRelationships === false) {
		return;
	}
	let table = createTableWithHeaders(createTable('relations'), 'Relationships', 'RelatedClass');
	let seen = new Set();
	let relation = null;
	for (let i = 0; i < RELATIONS.length; i++) {
		relation = data.relation(i);
		if (relation) {
			for (let j = 0; j < relation.length; j++) {
				if (!seen.has(relation[j])) {
					addRow(table, span(RELATIONS[i]), createFullSignature(relation[j]));
					seen.add(relation[j]);
				}
			}
		}
	}
	if (seen.size === 0) {
		table.parentNode.removeChild(table);
	}
}
