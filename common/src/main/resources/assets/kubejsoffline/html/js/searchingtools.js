function searchByClassName(class_name) {
	let table = createTableWithHeaders(createTable('matches'), 'ID', 'Class Name', 'Package', 'Qualified Name');

	let lower_class_name = class_name.toLowerCase();
	applyToAllClasses((subject) => {
		if (subject.name().toLowerCase().includes(lower_class_name)) {
			addClassToTable(table, subject.id());
		}
	});
}

function searchByFieldName(field_name) {
	let table = createTableWithHeaders(createTable('matches'), 'Class-ID', 'Field Signature', 'Field-Type', 'Declared In');

	let lower_field_name = field_name.toLowerCase();
	applyToAllClasses((subject) => {
		let fields = subject.fields();
		if (!fields)
			return;
		for (let f of fields) {
			let field = getField(f);
			if (field.name().toLowerCase().includes(lower_field_name)) {
				addFieldToTable(table, subject.id(), field);
			}
		}
	});
}


function searchByFieldType(field_type) {

	let table = createTableWithHeaders(createTable('matches'), 'Class-ID', 'Field Signature', 'Declared In');
	let lower_field_type = field_type.toLowerCase();
	applyToAllClasses((subject) => {
		let fields = subject.fields();
		if (!fields)
			return;
		for (let f of fields) {
			let field = getField(f);
			if (getClass(field.type()).name().toLowerCase().includes(lower_field_type)) {
				addFieldToTable(table, subject.id(), field);
			}
		}
	});
}

function searchByMethodName(method_name) {
	let table = createTableWithHeaders(createTable('matches'), 'Class-ID', 'Method Signature', 'Declared In');

	let lower_method_name = method_name.toLowerCase();
	applyToAllClasses((subject) => {
		let methods = subject.methods();
		if (!methods)
			return;
		for (let m of methods) {
			let method = getMethod(m);
			if (method.name().toLowerCase().includes(lower_method_name)) {
				addMethodToTable(table, subject.id(), method);
			}
		}
	});
}

function searchByMethodReturnType(method_type) {

	let table = createTableWithHeaders(createTable('matches'), 'Class-ID', 'Method Signature', 'Declared In');

	let lower_method_type = method_type.toLowerCase();

	applyToAllClasses((subject) => {
		let methods = subject.methods();
		if (!methods)
			return;
		for (let m of methods) {
			let method = getMethod(m);
			if (getClass(method.returnType()).simplename().toLowerCase().includes(lower_method_type)) {
				addMethodToTable(table, subject.id(), method);
			}
		}
	});

}


function searchByMethodParameterType(param_type) {
	let table = createTableWithHeaders(createTable('matches'), 'Class-ID', 'Method Signature', 'Declared In');

	let lower_param_type = param_type.toLowerCase();

	applyToAllClasses((subject) => {
		let methods = subject.methods();
		if (!methods)
			return;
		for (let m of methods) {
			let method = getMethod(m);
			if (!method.parameters()) {
				continue;
			}
			for (let param of method.parameters()) {
				if (getClass(getParameter(param).type()).name().toLowerCase().includes(lower_param_type)) {
					addMethodToTable(table, subject.id(), method);
				}
			}
		}
	});
}

function searchHelp() {
	// List valid search types
	document.body.append(span("Valid search parameters:"));
	let ul = document.createElement('ul');
	document.body.append(ul);
	ul.append(li('search'));
	ul.append(li('query'));
	document.body.append(br());
	document.body.append(span("Valid search types:"));
	ul = document.createElement('ul');
	document.body.append(ul);
	ul.append(li('class-name'));
	ul.append(li('field-name'));
	ul.append(li('field-type'));
	ul.append(li('method-name'));
	ul.append(li('method-return-type'));
	ul.append(li('method-parameter-type'));

	document.body.append(br());
	document.body.append(span("Query is the term to be searched for in the search type."));
	// ul.append(li('method-parameter-type'));
}