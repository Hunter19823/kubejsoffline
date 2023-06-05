function searchByClassName(class_name) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'ID', 'Class Name', 'Package', 'Qualified Name');

	let lower_class_name = class_name.toLowerCase();
	applyToAllClasses((subject) => {
		if (subject.name().toLowerCase().includes(lower_class_name)) {
			addClassToTable(table, subject.id());
		}
	});
}

function searchByFieldName(field_name) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID', 'Field Signature', 'Field-Type', 'Declared In');

	let lower_field_name = field_name.toLowerCase();
	applyToAllClasses((subject) => {
		let fields = subject.fields(true);
		if (!fields)
			return;
		for (let f of fields) {
			let field = getField(f);
			if (field.name().toLowerCase().includes(lower_field_name)) {
				addFieldToTable(table, subject.id(), field, subject.id());
			}
		}
	});
}


function searchByFieldType(field_type) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID', 'Field Signature', 'Declared In');
	let lower_field_type = field_type.toLowerCase();
	applyToAllClasses((subject) => {
		let fields = subject.fields(true);
		if (!fields)
			return;
		for (let f of fields) {
			let field = getField(f);
			if (getClass(field.type()).name().toLowerCase().includes(lower_field_type)) {
				addFieldToTable(table, subject.id(), field, subject.id());
			}
		}
	});
}

function searchByMethodName(method_name) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID', 'Method Signature', 'Declared In');

	let lower_method_name = method_name.toLowerCase();
	applyToAllClasses((subject) => {
		let methods = subject.methods(true);
		if (!methods)
			return;
		for (let m of methods) {
			let method = getMethod(m);
			if (method.name().toLowerCase().includes(lower_method_name)) {
				addMethodToTable(table, subject.id(), method, subject.id());
			}
		}
	});
}

function searchByMethodReturnType(method_type) {

	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID', 'Method Signature', 'Declared In');

	let lower_method_type = method_type.toLowerCase();

	applyToAllClasses((subject) => {
		let methods = subject.methods(true);
		if (!methods)
			return;
		for (let m of methods) {
			let method = getMethod(m);
			if (getClass(method.returnType()).type().toLowerCase().includes(lower_method_type)) {
				addMethodToTable(table, subject.id(), method, subject.id());
			}
		}
	});

}


function searchByMethodParameterType(param_type) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID', 'Method Signature', 'Declared In');

	let lower_param_type = param_type.toLowerCase();

	applyToAllClasses((subject) => {
		let methods = subject.methods(true);
		if (!methods)
			return;
		for (let m of methods) {
			let method = getMethod(m);
			if (!method.parameters()) {
				continue;
			}
			for (let param of method.parameters()) {
				if (getClass(getParameter(param).type()).name().toLowerCase().includes(lower_param_type)) {
					addMethodToTable(table, subject.id(), method, subject.id());
				}
			}
		}
	});
}

function searchByName(query) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID');

	let lower_query = query.toLowerCase();

	applyToAllClasses((subject) => {
		if (subject.name().toLowerCase().includes(lower_query)) {
			addClassToTable(table, subject.id());
		}

		let fields = subject.fields(true);
		if (fields) {
			for (let f of fields) {
				let field = getField(f);
				if (field.name().toLowerCase().includes(lower_query)) {
					addFieldToTable(table, subject.id(), field, subject.id());
				}
			}
		}

		let methods = subject.methods(true);
		if (methods) {
			for (let m of methods) {
				let method = getMethod(m);
				if (method.name().toLowerCase().includes(lower_query)) {
					addMethodToTable(table, subject.id(), method, subject.id());
				}
			}
		}
	});
}

function searchByPackage(query) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID');

	let lower_query = query.toLowerCase();

	applyToAllClasses((subject) => {
		if (subject.package().toLowerCase().includes(lower_query)) {
			addClassToTable(table, subject.id());
		}
	});
}

function searchByReturnType(query) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID');

	let lower_query = query.toLowerCase();

	applyToAllClasses((subject) => {
		if (subject.type().toLowerCase().includes(lower_query)) {
			addClassToTable(table, subject.id());
		}

		let fields = subject.fields(true);
		if (fields) {
			for (let f of fields) {
				let field = getField(f);
				if (getClass(field.type()).type().toLowerCase().includes(lower_query)) {
					addFieldToTable(table, subject.id(), field, subject.id());
				}
			}
		}

		let methods = subject.methods(true);
		if (methods) {
			for (let m of methods) {
				let method = getMethod(m);
				if (getClass(method.returnType()).type().toLowerCase().includes(lower_query)) {
					addMethodToTable(table, subject.id(), method, subject.id());
				}
			}
		}
	});
}


function searchByAny(query) {
	let table = createTableWithHeaders(createSortableTable('matches'), 'Class-ID');

	let lower_query = query.toLowerCase();

	applyToAllClasses((subject) => {
		// First check if the class name matches
		// If so, add it to the table
		if (subject.name().toLowerCase().includes(lower_query)) {
			addClassToTable(table, subject.id());
		}
		// Then check if any of the fields match
		// If so, add them to the table
		let fields = subject.fields(true);
		if (fields) {
			for (let f of fields) {
				let field = getField(f);
				if (field.name().toLowerCase().includes(lower_query)) {
					addFieldToTable(table, subject.id(), field, subject.id());
				}
				if (getClass(field.type()).name().toLowerCase().includes(lower_query)) {
					addFieldToTable(table, subject.id(), field, subject.id());
				}
			}
		}
		// Then check if any of the methods match
		// If so, add them to the table
		let methods = subject.methods(true);
		if (methods) {
			for (let m of methods) {
				let method = getMethod(m);
				if (method.name().toLowerCase().includes(lower_query)) {
					addMethodToTable(table, subject.id(), method, subject.id());
				}
				if (getClass(method.returnType()).name().toLowerCase().includes(lower_query)) {
					addMethodToTable(table, subject.id(), method, subject.id());
				}
				if (method.parameters()) {
					for (let param of method.parameters()) {
						if (getClass(getParameter(param).type()).name().toLowerCase().includes(lower_query)) {
							addMethodToTable(table, subject.id(), method, subject.id());
						}
					}
				}
			}
		}
	});
}

function searchHelp() {
	// List valid search types
	document.body.append(span("Valid search types:"));
	let ul = document.createElement('ul');
	document.body.append(ul);
	ul = document.createElement('ul');
	document.body.append(ul);
	ul.append(href(li('any'), '#any--net.minecraft.server.level.ServerPlayer'));
	ul.append(href(li('name'), '#name--dev.latvian.mods.kubejs'));
	ul.append(href(li('return-type'), '#return-type--dev.latvian.mods.kubejs'));
	ul.append(href(li('package'), '#package--dev.latvian.mods.kubejs'));
	ul.append(href(li('class-name'), '#class-name--dev.latvian.mods.kubejs'));
	ul.append(href(li('field-name'), '#field-name--player'));
	ul.append(href(li('field-type'), '#field-type--dev.latvian.mods.kubejs'));
	ul.append(href(li('method-name'), '#method-name--getEntity'));
	ul.append(href(li('method-return-type'), '#method-return-type--dev.latvian.mods.kubejs'));
	ul.append(href(li('method-parameter-type'), '#method-parameter-type--dev.latvian.mods.kubejs'));

	document.body.append(br());
	document.body.append(span("Query is the term to be searched for in the search type."));
	// ul.append(li('method-parameter-type'));
}


function searchForTerms(query_type, search_term) {
	//searchHelp();
	let query = search_term;
	console.log("Searching for " + search_term + " in " + query);
	switch (query_type) {
		case 'name':
			searchByName(query);
			break;
		case 'package':
			searchByPackage(query);
			break;
		case 'return-type':
			searchByReturnType(query);
			break;
		case 'any':
			searchByAny(query);
			break;
		case 'class-name':
			searchByClassName(query);
			break;
		case 'field-name':
			searchByFieldName(query);
			break;
		case 'field-type':
			searchByFieldType(query);
			break;
		case 'method-name':
			searchByMethodName(query);
			break;
		case 'method-return-type':
			searchByMethodReturnType(query);
			break;
		case 'method-parameter-type':
			searchByMethodParameterType(query);
			break;
		default:
			document.body.append(span("Invalid search type! :("));
			document.body.append(br());
			break;
	}
}

function createSearchBar() {
	// Create one div that contains the search bar
	let searchDiv = document.createElement('div');
	searchDiv.id = 'search-bar';

	// Create two elements, one for a search type, the other for search input
	let searchType = document.createElement('select');
	let searchInput = document.createElement('input');
	searchInput.type = 'text';
	searchInput.placeholder = 'Search...';
	// Use the enter key to change the URL
	searchInput.onkeydown = function (e) {
		if (e.key === 'Enter') {
			changeURL(`#${searchType.value}--${searchInput.value}`);
		}
	}

	// Add the search types
	function addSearchType(type) {
		let option = document.createElement('option');
		option.value = type;
		option.text = type;
		searchType.add(option);
	}

	addSearchType('any');
	addSearchType('name');
	addSearchType('package');
	addSearchType('return-type');
	addSearchType('class-name');
	addSearchType('field-name');
	addSearchType('field-type');
	addSearchType('method-name');
	addSearchType('method-return-type');
	addSearchType('method-parameter-type');
	// Add the search bar to the page
	searchDiv.append(searchType);
	searchDiv.append(searchInput);
	document.body.append(searchDiv);
	return searchDiv;
}