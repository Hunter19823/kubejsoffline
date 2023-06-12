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
		const FIELD_TYPE_FILTER = fieldTypeAttributeMatcher('type', query, exact, includes)

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
		let CLASS_NAME_FILTER = attributeMatcher('name', query, exact, includes);
		let FIELD_NAME_FILTER = fieldTypeAttributeMatcher('name', query, exact, includes);
		let METHOD_NAME_FILTER = methodTypeAttributeMatcher('name', query, exact, includes);
		let PARAMETER_NAME_FILTER = parameterTypeAttributeMatcher('name', query, exact, includes);

		this.withClassFilter((subject) => {
			return CLASS_NAME_FILTER(subject);
		});

		this.withFieldFilter((subject) => {
			return FIELD_NAME_FILTER(subject);
		})

		this.withMethodFilter((subject) => {
			return METHOD_NAME_FILTER(subject);
		});

		this.withParamFilter((subject) => {
			return PARAMETER_NAME_FILTER(subject);
		});

		return this;
	}

	output.withClassName = function (query, exact = false, includes = true) {
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
		let CLASS_SIMPLE_NAME_FILTER = attributeMatcher('simplename', query, exact, includes);
		let FIELD_NAME_FILTER = fieldTypeAttributeMatcher('name', query, exact, includes);
		let FIELD_TYPE_SIMPLE_NAME_FILTER = fieldTypeAttributeMatcher('simplename', query, exact, includes);
		let METHOD_NAME_FILTER = methodTypeAttributeMatcher('name', query, exact, includes);
		let METHOD_TYPE_SIMPLE_NAME_FILTER = methodTypeAttributeMatcher('simplename', query, exact, includes);
		let PARAMETER_NAME_FILTER = parameterTypeAttributeMatcher('name', query, exact, includes);
		let PARAMETER_TYPE_SIMPLE_NAME_FILTER = parameterTypeAttributeMatcher('simplename', query, exact, includes);

		this.withClassFilter((subject) => {
			return CLASS_SIMPLE_NAME_FILTER(subject);
		});

		this.withFieldFilter((subject) => {
			return FIELD_NAME_FILTER(subject) || FIELD_TYPE_SIMPLE_NAME_FILTER(subject);
		});

		this.withMethodFilter((subject) => {
			return METHOD_NAME_FILTER(subject) || METHOD_TYPE_SIMPLE_NAME_FILTER(subject);
		});

		this.withParamFilter((subject) => {
			return PARAMETER_NAME_FILTER(subject) || PARAMETER_TYPE_SIMPLE_NAME_FILTER(subject);
		});

		return this;
	}

	output.withClassSimpleName = function (query, exact = false, includes = true) {
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
		let CLASS_RAW_TYPE_FILTER = attributeMatcher('rawtype', query, exact, includes);
		let FIELD_TYPE_RAW_TYPE_FILTER = fieldTypeAttributeMatcher('rawtype', query, exact, includes);
		let METHOD_RETURN_RAW_TYPE_FILTER = methodTypeAttributeMatcher('rawtype', query, exact, includes);
		let METHOD_PARAMETER_RAW_TYPE_FILTER = parameterTypeAttributeMatcher('rawtype', query, exact, includes);

		this.withClassFilter((subject) => {
			return CLASS_RAW_TYPE_FILTER(subject);
		});

		this.withFieldFilter((subject) => {
			return FIELD_TYPE_RAW_TYPE_FILTER(subject);
		});

		this.withMethodFilter((subject) => {
			return METHOD_RETURN_RAW_TYPE_FILTER(subject);
		});

		this.withParamFilter((subject) => {
			return METHOD_PARAMETER_RAW_TYPE_FILTER(subject);
		});

		return this;
	}

	output.withClassRawType = function (query, exact = false, includes = true) {
		return this.withClassAttribute('rawtype', query, exact, includes);
	}

	output.withFieldRawType = function (query, exact = false, includes = true) {
		return this.withFieldFilter(fieldTypeAttributeMatcher('rawtype', query, exact, includes));
	}

	output.withMethodReturnRawType = function (query, exact = false, includes = true) {
		return this.withMethodFilter(methodTypeAttributeMatcher('rawtype', query, exact, includes));
	}

	output.withMethodParameterRawType = function (query, exact = false, includes = true) {
		return this.withParamFilter(parameterTypeAttributeMatcher('rawtype', query, exact, includes));
	}

	// Type

	output.withType = function (query, exact = false, includes = true) {
		let CLASS_TYPE_FILTER = attributeMatcher('type', query, exact, includes);
		let FIELD_TYPE_FILTER = fieldTypeAttributeMatcher('type', query, exact, includes);
		let METHOD_RETURN_TYPE_FILTER = methodTypeAttributeMatcher('type', query, exact, includes);
		let METHOD_PARAMETER_TYPE_FILTER = parameterTypeAttributeMatcher('type', query, exact, includes);

		this.withClassFilter((subject) => {
			return CLASS_TYPE_FILTER(subject);
		});

		this.withFieldFilter((subject) => {
			return FIELD_TYPE_FILTER(subject);
		});

		this.withMethodFilter((subject) => {
			return METHOD_RETURN_TYPE_FILTER(subject);
		});

		this.withParamFilter((subject) => {
			return METHOD_PARAMETER_TYPE_FILTER(subject);
		});

		return this;
	}

	output.withClassType = function (query, exact = false, includes = true) {
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
		let CLASS_PACKAGE_FILTER = attributeMatcher('package', query, exact, includes);
		let FIELD_PACKAGE_FILTER = fieldTypeAttributeMatcher('package', query, exact, includes);
		let METHOD_PACKAGE_FILTER = methodTypeAttributeMatcher('package', query, exact, includes);
		let PARAMETER_PACKAGE_FILTER = parameterTypeAttributeMatcher('package', query, exact, includes);

		this.withClassFilter((subject) => {
			return CLASS_PACKAGE_FILTER(subject);
		});

		this.withFieldFilter((subject) => {
			return FIELD_PACKAGE_FILTER(subject);
		});

		this.withMethodFilter((subject) => {
			return METHOD_PACKAGE_FILTER(subject);
		});

		this.withParamFilter((subject) => {
			return PARAMETER_PACKAGE_FILTER(subject);
		});

		return this;
	}

	output.withClassPackage = function (query, exact = false, includes = true) {
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

	// Ignore
	output.withIgnoreClasses = function (query, exact = false, includes = true) {
		return this.withClassFilter(() => false);
	}

	output.withIgnoreMethods = function (query, exact = false, includes = true) {
		return this.withMethodFilter(() => false);
	}

	output.withIgnoreFields = function (query, exact = false, includes = true) {
		return this.withFieldFilter(() => false);
	}

	output.withIgnoreParameters = function (query, exact = false, includes = true) {
		return this.withParamFilter(() => false);
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


	'name': 'withName',
	'class-name': 'withClassName',
	'field-name': 'withFieldName',
	'method-name': 'withMethodName',
	'parameter-name': 'withMethodParameterName',

	'field-type-name': 'withFieldTypeName',
	'method-type-name': 'withMethodReturnTypeName',
	'parameter-type-name': 'withMethodParameterTypeName',


	'simplename': 'withSimpleName',
	'class-type-simplename': 'withClassSimpleName',
	'field-type-simplename': 'withFieldTypeSimpleName',
	'method-type-simplename': 'withMethodReturnTypeSimpleName',
	'parameter-type-simple-name': 'withMethodParameterTypeSimpleName',


	'raw-type': 'withRawType',
	'class-raw-type': 'withClassRawType',
	'field-raw-type': 'withFieldRawType',
	'method-raw-type': 'withMethodReturnRawType',
	'parameter-raw-type': 'withMethodParameterRawType',


	'type-name': 'withType',
	'class-type-name': 'withClassType',
	'field-type-name': 'withFieldTypeTypeName',
	'method-type-name': 'withMethodReturnTypeTypeName',
	'parameter-type-name': 'withMethodParameterTypeTypeName',


	'package': 'withPackage',
	'class-package': 'withClassPackage',
	'field-type-package': 'withFieldTypePackage',
	'method-type-package': 'withMethodReturnTypePackage',
	'parameter-type-package': 'withMethodParameterTypePackage',


	'parameter-count': 'withMethodParameterCount',


	'ignore-classes': 'withIgnoreClasses',
	'ignore-fields': 'withIgnoreFields',
	'ignore-methods': 'withIgnoreMethods',
	'ignore-parameters': 'withIgnoreParameters'
}

let _last_filter = null;
let _last_search_parameters = null;

function searchFromParameters(parameters) {
	wipePage();
	if (!parameters.has('page')) {
		parameters.set('page', 0);
	}
	if (!parameters.has('size')) {
		parameters.set('size', GLOBAL_SETTINGS.defaultSearchPageSize);
	}
	let page = parseInt(parameters.get('page'));
	let page_size = parseInt(parameters.get('size'));

	function compareSearchParameters(before, after) {
		// These keys are ignored when comparing search parameters
		let IGNORED_KEYS = new Set(['page', 'size', 'focus']);

		// A set of all the keys in the before and after parameters
		let before_keys = new Set(before.keys());
		let after_keys = new Set(after.keys());

		// Determine which keys were added
		let added_keys = new Set([...after_keys].filter(x => !before_keys.has(x)));
		// Determine which keys were removed
		let removed_keys = new Set([...before_keys].filter(x => !after_keys.has(x)));

		// Check if the added/removed keys are in the ignored keys
		for (let key of IGNORED_KEYS) {
			if (added_keys.has(key)) {
				added_keys.delete(key);
			}
			if (removed_keys.has(key)) {
				removed_keys.delete(key);
			}
		}

		// Check if the added/removed keys are the same
		if (added_keys.size !== 0 || removed_keys.size !== 0) {
			return false;
		}

		// Determine which keys were changed
		let changed_keys = new Set([...before_keys].filter(x => after_keys.has(x)));

		// Remove the ignored keys from the changed keys
		for (let key of IGNORED_KEYS) {
			changed_keys.delete(key);
		}

		// Check if the values of the changed keys are the same
		for (let key of changed_keys) {
			if (before.get(key) !== after.get(key)) {
				return false;
			}
		}

		return true;
	}

	if(!_last_search_parameters || !compareSearchParameters(_last_search_parameters, parameters)) {
		// The search parameters have changed or don't exist, so we need to create a new filter
		console.log("Creating new filter either because the search parameters have changed or don't exist");
		_last_search_parameters = parameters;
		_last_filter = dataFilter();

		for (const key in NEW_QUERY_TERMS) {
			let value = NEW_QUERY_TERMS[key];
			if (_last_search_parameters.has(key)) {
				_last_filter[value](_last_search_parameters.get(key));
				continue;
			}
			let key_normalized = key.replaceAll(/[^a-z0-9-_]+/g, '');
			if (_last_search_parameters.has(key_normalized)) {
				_last_filter[value](_last_search_parameters.get(key_normalized));
			}
		}

		_last_filter.findAllThatMatch();
	}

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
		document.body.append(addSearchDetails("Matching Classes",results.classes, 'class-table-header', page_number, page_size));
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
	}

	if (results.fields.length !== 0) {
		document.body.append(addSearchDetails("Matching Fields", results.fields, 'field-table-header', page_number, page_size));
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
	}

	if (results.methods.length !== 0) {
		document.body.append(addSearchDetails("Matching Methods", results.methods, 'method-table-header', page_number, page_size));
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
	}

	if (results.parameters.length !== 0) {
		document.body.append(addSearchDetails("Matching Parameters", results.parameters, 'parameter-table-header', page_number, page_size));
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
	}
}

function addSearchDetails(title, list, focus, page_number, page_size) {
	let div = document.createElement('h2');

	let headerTitle = document.createElement('h1');
	headerTitle.innerText = title;
	headerTitle.id = focus;
	headerTitle.style.fontSize = 'revert';
	div.append(headerTitle);

	function linkify(tag, callback) {
		tag.onclick = callback;
		tag.style.textDecoration = 'underline';
		tag.style.color = '#8cb4ff';
		tag.style.cursor = 'pointer';
	}

	let lastPage = Math.ceil(list.length / page_size) - 1;

	// Add a previous button, if needed
	div.classList.add('search-pagination');
	if (page_number > 0) {
		let prev = span("Previous");
		div.append(prev);
		div.append(span("    "));
		linkify(prev, () => {
			// The Previous button should go to the minimum of the last page and the previous page
			_last_search_parameters.set('page', Math.min(page_number - 1, lastPage));
			_last_search_parameters.set('size', page_size);
			_last_search_parameters.set('focus', focus);
			changeURL(`#?${_last_search_parameters.toString()}`);
		});
	}

	// Add the number of results and how many total results there are
	div.append(span(`Page ${page_number + 1} of ${lastPage + 1} (${list.length} total results)`));

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