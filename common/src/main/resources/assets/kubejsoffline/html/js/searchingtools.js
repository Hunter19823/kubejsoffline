function dataFilter() {
	let output = {};
	output.results = {'classes': [], 'fields': [], 'methods': [], 'parameters': []};

	output._classFilters = [];
	output._fieldFilters = [];
	output._methodFilters = [];
	output._paramFilters = [];

	function attributeMatcher(attribute, query, exact = false, includes = true) {
		console.log(`attributeMatcher(${attribute}, ${query}, ${exact}, ${includes})`);
		let modifier = (p) => p;
		let comparator = (a, b) => (a == b);
		if (!exact) {
			modifier = (p) => (String(p)).toLowerCase();
		}
		if (includes) {
			comparator = (a, b) => (String(a).includes(b));
		}

		return (subject) => {
			if (typeof (subject[attribute]) === 'function') {
				let subject_attribute = subject[attribute]();
				if (subject_attribute) {
					return comparator(
							modifier(subject_attribute),
							modifier(query)
					);
				}
			}
			return false;
		}
	}

	function fieldTypeAttributeMatcher(attribute, query, exact = false, includes = true) {
		const MATCHER = attributeMatcher(attribute, query, exact, includes);
		return (field) => {
			return MATCHER(getClass(field.type()));
		}
	}

	function methodTypeAttributeMatcher(attribute, query, exact = false, includes = true) {
		const MATCHER = attributeMatcher(attribute, query, exact, includes);
		return (method) => {
			return MATCHER(getClass(method.returnType()));
		}
	}

	function parameterTypeAttributeMatcher(attribute, query, exact = false, includes = true) {
		const MATCHER = attributeMatcher(attribute, query, exact, includes);
		return (parameter) => {
			return MATCHER(getClass(parameter.type()));
		}
	}

	output.withClassFilter = function (filter) {
		console.log(`Added class filter: ${filter.toString()}`);
		this._classFilters.push(filter);
		return this;
	}

	output.withFieldFilter = function (filter) {
		console.log(`Added field filter: ${filter.toString()}`);
		this._fieldFilters.push(filter);
		return this;
	}

	output.withMethodFilter = function (filter) {
		console.log(`Added method filter: ${filter.toString()}`);
		this._methodFilters.push(filter);
		return this;
	}

	output.withParamFilter = function (filter) {
		console.log(`Added param filter: ${filter.toString()}`);
		this._paramFilters.push(filter);
		return this;
	}

	output.withAny = function (query, exact = false, includes = true) {
		this.withClassAny(query, exact, includes);
		this.withFieldAny(query, exact, includes);
		this.withMethodAny(query, exact, includes);
		this.withMethodParameterAny(query, exact, includes);
		return this;
	}

	// Any

	output.withClassAny = function (query, exact = false, includes = true) {
		const TYPE_FILTER = attributeMatcher('type', query, exact, includes);

		return this.withClassFilter((subject) => {
			return TYPE_FILTER(subject);
		});
	}

	output.withFieldAny = function (query, exact = false, includes = true) {
		const TYPE_FILTER = attributeMatcher('type', query, exact, includes);
		const FIELD_TYPE_FILTER = fieldTypeAttributeMatcher('type', query, exact, includes);
		return this.withFieldFilter((subject) => {
			return TYPE_FILTER(subject) || FIELD_TYPE_FILTER(subject);
		});
	}

	output.withMethodAny = function (query, exact = false, includes = true) {
		const NAME_FILTER = attributeMatcher('name', query, exact, includes);
		const METHOD_TYPE_FILTER = methodTypeAttributeMatcher('type', query, exact, includes);
		return this.withMethodFilter((subject) => {
			return NAME_FILTER(subject) || METHOD_TYPE_FILTER(subject);
		});
	}

	output.withMethodParameterAny = function (query, exact = false, includes = true) {
		const NAME_FILTER = attributeMatcher('name', query, exact, includes);
		const PARAMETER_TYPE_FILTER = parameterTypeAttributeMatcher('type', query, exact, includes);
		return this.withParamFilter((subject) => {
			return NAME_FILTER(subject) || PARAMETER_TYPE_FILTER(subject);
		});
	}

	// Attribute

	output.withClassAttribute = function (query, attribute, exact = false, includes = true) {
		return this.withClassFilter(attributeMatcher(attribute, query, exact, includes));
	}

	output.withFieldAttribute = function (query, attribute, exact = false, includes = true) {
		return this.withFieldFilter(attributeMatcher(attribute, query, exact, includes));
	}

	output.withMethodAttribute = function (query, attribute, exact = false, includes = true) {
		return this.withMethodFilter(attributeMatcher(attribute, query, exact, includes));
	}

	output.withParameterAttribute = function (query, attribute, exact = false, includes = true) {
		return this.withParamFilter(attributeMatcher(attribute, query, exact, includes));
	}

	// Id

	output.withId = function (query) {
		return this.withClassFilter((subject) => subject.id() == query);
	}

	output.withFieldTypeId = function (query, exact = false, includes = true) {
		return this.withFieldFilter(fieldTypeAttributeMatcher('id', query, exact, includes));
	}

	output.withMethodReturnTypeId = function (query, exact = false, includes = true) {
		return this.withMethodFilter(methodTypeAttributeMatcher('id', query, exact, includes));
	}

	output.withMethodParameterTypeId = function (query, exact = false, includes = true) {
		return this.withParamFilter(parameterTypeAttributeMatcher('id', query, exact, includes));
	}

	// Name

	output.withName = function (query, exact = false, includes = true) {
		return this.withClassAttribute('name', query, exact, includes);
	}

	output.withFieldName = function (query, exact = false, includes = true) {
		return this.withFieldAttribute('name', query, exact, includes);
	}

	output.withFieldTypeName = function (query, exact = false, includes = true) {
		return this.withFieldFilter(fieldTypeAttributeMatcher('name', query, exact, includes));
	}

	output.withMethodName = function (query, exact = false, includes = true) {
		return this.withMethodAttribute('name', query, exact, includes);
	}

	output.withMethodReturnTypeName = function (query, exact = false, includes = true) {
		return this.withMethodFilter(methodTypeAttributeMatcher('name', query, exact, includes));
	}

	output.withMethodParameterName = function (query, exact = false, includes = true) {
		return this.withParameterAttribute('name', query, exact, includes);
	}

	output.withMethodParameterTypeName = function (query, exact = false, includes = true) {
		return this.withParamFilter(parameterTypeAttributeMatcher('name', query, exact, includes));
	}

	// Simple Name

	output.withSimpleName = function (query, exact = false, includes = true) {
		return this.withClassAttribute('simplename', query, exact, includes);
	}

	output.withFieldTypeSimpleName = function (query, exact = false, includes = true) {
		return this.withFieldFilter(fieldTypeAttributeMatcher('simplename', query, exact, includes));
	}

	output.withMethodReturnTypeSimpleName = function (query, exact = false, includes = true) {
		return this.withMethodFilter(methodTypeAttributeMatcher('simplename', query, exact, includes));
	}

	output.withMethodParameterTypeSimpleName = function (query, exact = false, includes = true) {
		return this.withParamFilter(parameterTypeAttributeMatcher('simplename', query, exact, includes));
	}

	// Raw Type

	output.withRawType = function (query, exact = false, includes = true) {
		return this.withClassAttribute('rawtype', query, exact, includes);
	}

	// Type

	output.withType = function (query, exact = false, includes = true) {
		return this.withClassAttribute('type', query, exact, includes);
	}

	output.withFieldTypeTypeName = function (query, exact = false, includes = true) {
		return this.withFieldFilter(fieldTypeAttributeMatcher('type', query, exact, includes));
	}

	output.withMethodReturnTypeTypeName = function (query, exact = false, includes = true) {
		return this.withMethodFilter(methodTypeAttributeMatcher('type', query, exact, includes));
	}

	output.withMethodParameterTypeTypeName = function (query, exact = false, includes = true) {
		return this.withParamFilter(parameterTypeAttributeMatcher('type', query, exact, includes));
	}

	// Package

	output.withPackage = function (query, exact = false, includes = true) {
		return this.withClassAttribute('package', query, exact, includes);
	}

	output.withFieldTypePackage = function (query, exact = false, includes = true) {
		return this.withFieldFilter(fieldTypeAttributeMatcher('package', query, exact, includes));
	}

	output.withMethodReturnTypePackage = function (query, exact = false, includes = true) {
		return this.withMethodFilter(methodTypeAttributeMatcher('package', query, exact, includes));
	}

	output.withMethodParameterTypePackage = function (query, exact = false, includes = true) {
		return this.withParamFilter(parameterTypeAttributeMatcher('package', query, exact, includes));
	}

	// Parameter Count

	output.withMethodParameterCount = function (count, exact = false, includes = true) {
		return this.withMethodFilter((method) => method.parameters().length === count);
	}


	// Filters
	output.matchesClass = function (data) {
		if (this._classFilters.length === 0) {
			return true;
		}
		for (let filter of this._classFilters) {
			if (!filter(data)) {
				return false;
			}
		}
		return true;
	}

	output.matchesFiled = function (data) {
		if (this._fieldFilters.length === 0) {
			return true;
		}
		for (let filter of this._fieldFilters) {
			if (!filter(data)) {
				return false;
			}
		}
		return true;
	}

	output.matchesMethod = function (data) {
		if (this._methodFilters.length === 0) {
			return true;
		}
		for (let filter of this._methodFilters) {
			if (!filter(data)) {
				return false;
			}
		}
		return true;
	}

	output.matchesParam = function (data) {
		if (this._paramFilters.length === 0) {
			return true;
		}
		for (let filter of this._paramFilters) {
			if (!filter(data)) {
				return false;
			}
		}
		return true;
	}

	// Collector

	output.findAllThatMatch = function () {
		applyToAllClasses((subject) => {
			if (!subject) {
				console.error("subject is null!");
			}
			if (this.matchesClass(subject)) {
				this.results.classes.push(subject.id());
			}
			if (this._fieldFilters.length !== 0) {
				for (let field of subject.fields(true)) {
					let f = getField(field);
					if (this.matchesFiled(f)) {
						this.results.fields.push(field);
					}
				}
			}
			if (this._methodFilters.length !== 0) {
				for (let method of subject.methods(true)) {
					let m = getMethod(method);
					if (this.matchesMethod(m)) {
						this.results.methods.push(method);
					} else {
						if (this._paramFilters.length !== 0) {
							for (let param of m.parameters()) {
								if (this.matchesParam(getParameter(param))) {
									this.results.parameters.push(method);
									break;
								}
							}
						}
					}
				}
			}
		});
		return this;
	}

	// Results

	output.getResults = function () {
		return this.results;
	}

	return output;
}

const NEW_QUERY_TERMS = {
	'any': 'withAny',
	'class-any': 'withClassAny',
	'field-any': 'withFieldAny',
	'method-any': 'withMethodAny',
	'parameter-any': 'withMethodParameterAny',
	'class-id': 'withId',
	'field-id': 'withFieldTypeId',
	'method-id': 'withMethodReturnTypeId',
	'parameter-id': 'withMethodParameterTypeId',
	'class-name': 'withName',
	'field-name': 'withFieldName',
	'field-type-name': 'withFieldTypeName',
	'method-name': 'withMethodName',
	'method-type-name': 'withMethodReturnTypeName',
	'parameter-name': 'withMethodParameterName',
	'parameter-type-name': 'withMethodParameterTypeName',
	'simplename': 'withSimpleName',
	'field-type-simplename': 'withFieldTypeSimpleName',
	'method-type-simplename': 'withMethodReturnTypeSimpleName',
	'parameter-type-simple-name': 'withMethodParameterTypeSimpleName',
	'raw-type': 'withRawType',
	'type-name': 'withType',
	'field-type-name': 'withFieldTypeTypeName',
	'method-type-name': 'withMethodReturnTypeTypeName',
	'parameter-type-name': 'withMethodParameterTypeTypeName',
	'package': 'withPackage',
	'field-type-package': 'withFieldTypePackage',
	'method-type-package': 'withMethodReturnTypePackage',
	'parameter-type-package': 'withMethodParameterTypePackage',
	'parameter-count': 'withMethodParameterCount'
}

let _last_filter = null;
let _last_search_terms = null;
let _last_search_parameters = null;

function searchFromParameters(parameters) {
	wipePage();
	if (!parameters.has('page')) {
		parameters.set('page', 0);
	}
	if (!parameters.has('size')) {
		parameters.set('size', 100);
	}
	let page = parseInt(parameters.get('page'));
	let page_size = parseInt(parameters.get('size'));

	// Create an array of all the search terms in NEW_QUERY_TERMS that are in the parameters
	function createArrayOfSearchTerms(searchParams) {
		let search_terms = [];
		for (const key in NEW_QUERY_TERMS) {
			let value = NEW_QUERY_TERMS[key];
			if (searchParams.has(key)) {
				search_terms.push({'key': key, 'value': searchParams.get(key)});
				continue;
			}
			let key_normalized = key.replaceAll(/[^a-z0-9-_]+/g, '');
			if (searchParams.has(key_normalized)) {
				search_terms.push({'key': key, 'value': searchParams.get(key_normalized)});
			}
		}
		search_terms.sort();
		return search_terms;
	}

	let search_terms = createArrayOfSearchTerms(parameters);

	if (_last_search_terms !== null) {
		// Check this array against the last one
		if (search_terms.length === _last_search_terms.length) {
			// Check if they are the same
			let same = true;
			for (let i = 0; i < search_terms.length; i++) {
				if (search_terms[i].key !== _last_search_terms[i].key && search_terms[i].value !== _last_search_terms[i].value) {
					same = false;
					break;
				}
			}

			if (same) {
				// Load from cache
				console.log("Loading page from cache...");

				loadSearchResults(page, page_size);
				return;
			}
		}
	}

	// This is a new search
	_last_search_terms = search_terms;
	_last_search_parameters = parameters;

	_last_filter = dataFilter();

	// Create a new filter from the search terms
	for (let i = 0; i < search_terms.length; i++) {
		let term = search_terms[i];
		_last_filter[NEW_QUERY_TERMS[term.key]](term.value);
	}

	// Create a new cache
	_last_filter.findAllThatMatch();

	// Load from cache
	loadSearchResults(page, page_size);
}

function clearTable(table) {
	let trs = table.getElementsByTagName('tr');
	for (let i = 1; i < trs.length;) {
		trs[i].remove();
	}
}

function loadSearchResults(page_number, page_size) {
	// Remove existing table entries
	wipePage();

	let results = _last_filter.getResults();

	if (results.classes.length !== 0) {
		document.body.append(addSearchDetails(results.fields, 'class-table', page_number, page_size));
		// Create a class table
		let classTable = createTableWithHeaders(createSortableTable('class-table'), 'ID', 'Class Name', 'Package', 'Qualified Name');
		// Determine the start and end of the page
		let start = Math.min(page_number * page_size, results.classes.length);
		let end = Math.min(start + page_size, results.classes.length);
		for (let i = start; i < end; i++) {
			let id = results.classes[i];
			let classData = getClass(id);
			addClassToTable(classTable, classData.id());
		}
		document.body.append(addSearchDetails(results.fields, 'class-table', page_number, page_size));
	}

	if (results.fields.length !== 0) {
		document.body.append(addSearchDetails(results.fields, 'field-table', page_number, page_size));
		// Create a field table
		let fieldTable = createTableWithHeaders(createSortableTable('field-table'), 'Class-ID', 'Field Signature', 'Declared In');
		// Determine the start and end of the page
		let start = Math.min(page_number * page_size, results.fields.length);
		let end = Math.min(start + page_size, results.fields.length);
		for (let i = start; i < end; i++) {
			let field = results.fields[i];
			let fieldData = getField(field);
			addFieldToTable(fieldTable, fieldData.type(), fieldData, fieldData.type());
		}
		document.body.append(addSearchDetails(results.fields, 'field-table', page_number, page_size));
	}

	if (results.methods.length !== 0) {
		document.body.append(addSearchDetails(results.methods, 'method-table', page_number, page_size));
		// Create a method table
		let methodTable = createTableWithHeaders(createSortableTable('method-table'), 'Class-ID', 'Method Signature', 'Declared In');
		// Determine the start and end of the page
		let start = Math.min(page_number * page_size, results.methods.length);
		let end = Math.min(start + page_size, results.methods.length);
		for (let i = start; i < end; i++) {
			let method = results.methods[i];
			let methodData = getMethod(method);
			addMethodToTable(methodTable, methodData.returnType(), methodData, methodData.returnType());
		}
		document.body.append(addSearchDetails(results.methods, 'method-table', page_number, page_size));
	}

	if (results.parameters.length !== 0) {
		document.body.append(addSearchDetails(results.parameters, 'parameter-table', page_number, page_size));
		// Create a parameter table
		let parameterTable = createTableWithHeaders(createSortableTable('parameter-table'), 'Class-ID', 'Method Signature', 'Declared In');
		// Determine the start and end of the page
		let start = Math.min(page_number * page_size, results.parameters.length);
		let end = Math.min(start + page_size, results.parameters.length);
		for (let i = start; i < end; i++) {
			let method = results.parameters[i];
			let methodData = getMethod(method);
			addMethodToTable(parameterTable, methodData.returnType(), methodData, methodData.returnType());
		}
		document.body.append(addSearchDetails(results.parameters, 'parameter-table', page_number, page_size));
	}


	addSortTables();

	if (_last_search_parameters.has('focus')) {
		let focus = _last_search_parameters.get('focus');

		focusElement(focus);
	}
}

function addSearchDetails(list, focus, page_number, page_size) {
	document.body.append(br());

	let div = document.createElement('div');

	function linkify(tag, callback) {
		tag.onclick = callback;
		tag.style.textDecoration = 'underline';
		tag.style.color = '#8cb4ff';
		tag.style.cursor = 'pointer';
	}

	// Add a previous button, if needed
	div.classList.add('search-pagination');
	if (page_number > 0) {
		let prev = span("Previous");
		div.append(prev);
		div.append(span("    "));
		linkify(prev, () => {
			_last_search_parameters.set('page', page_number - 1);
			_last_search_parameters.set('size', page_size);
			_last_search_parameters.set('focus', focus);
			changeURL(`#?${_last_search_parameters.toString()}`);
		});
	}

	// Add the number of results and how many total results there are
	div.append(span(`Page ${page_number + 1} of ${Math.ceil(list.length / page_size)} (${list.length} total results)`));

	// Add a next button, if needed
	if (list.length > (page_number + 1) * page_size) {
		div.append(span("    "));
		let next = span("Next");
		div.append(next);
		linkify(next, () => {
			_last_search_parameters.set('page', page_number + 1);
			_last_search_parameters.set('size', page_size);
			_last_search_parameters.set('focus', focus);
			changeURL(`#?${_last_search_parameters.toString()}`);
		});
	}

	return div;
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
			changeURL(`#?${searchType.value}=${searchInput.value}`);
		}
	}

	// Add the search types
	function addSearchType(type) {
		let option = document.createElement('option');
		option.value = type;
		option.text = type;
		searchType.add(option);
	}

	for (const key in NEW_QUERY_TERMS) {
		addSearchType(key);
	}

	// Add the search bar to the page
	searchDiv.append(searchType);
	searchDiv.append(searchInput);
	document.body.append(searchDiv);
	return searchDiv;
}