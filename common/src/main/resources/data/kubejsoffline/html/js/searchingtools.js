function dataFilter() {
    // TODO: Fix the data-filter
    let output = {};
    output.results = {'classes': [], 'fields': [], 'methods': [], 'parameters': []};

    output._classFilters = [];
    output._fieldFilters = [];
    output._methodFilters = [];
    output._paramFilters = [];

    /**
     * Matches an attribute of an object.
     * @template T
     * @param {string} attribute
     * @param {string} query
     * @param {boolean} exact
     * @param {boolean}includes
     * @param {(function(*): T)}transformer
     * @returns {(function(*): (boolean))}
     */
    function attributeMatcher(attribute, query, exact = false, includes = true, transformer = (p) => p) {
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
                if (transformer) {
                    subject_attribute = transformer(subject_attribute);
                }
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

    function classTypeAttributeMatcher(attribute, mutator = (p) => p) {
        return (subject) => {
            if (typeof (subject[attribute]) === 'function') {
                let subject_attribute = subject[attribute]();
                if (!exists(subject_attribute))
                    return false;
                subject_attribute = mutator(subject_attribute);
                if (!exists(subject_attribute))
                    return false;

                return output.matchesClass(subject_attribute);
            }
            return false;
        }
    }

    /**
     * Creates a field type attribute matcher
     * @param {string} attribute
     * @param {string} query
     * @param {boolean} exact
     * @param {boolean} includes
     * @returns {function(Field): boolean}
     */
    function fieldTypeAttributeMatcher(attribute, query, exact = false, includes = true) {
        const MATCHER = attributeMatcher(attribute, query, exact, includes);

        return (field) => {
            return MATCHER(getClass(field.type()));
        }
    }

    /**
     * Creates a method type attribute matcher
     * @param {string} attribute
     * @param {string} query
     * @param {boolean} exact
     * @param {boolean} includes
     * @returns {function(Method): boolean}
     */
    function methodTypeAttributeMatcher(attribute, query, exact = false, includes = true) {
        const MATCHER = attributeMatcher(attribute, query, exact, includes);

        /**
         * @param {Method} method
         * @returns {boolean}
         */
        return (method) => {
            return MATCHER(getClass(method.type()));
        }
    }

    /**
     * Creates a parameter type attribute matcher
     * @param {string} attribute
     * @param {string} query
     * @param {boolean} exact
     * @param {boolean} includes
     * @returns {function(Parameter): boolean}
     */
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
        this.withClassReferenceName(query, exact, includes);
        this.withClassName(query, exact, includes);
        this.withClassSimpleName(query, exact, includes);
        this.withClassPackage(query, exact, includes);
        return this;
    }

    output.withFieldAny = function (query, exact = false, includes = true) {
        this.withFieldReferenceName(query, exact, includes);
        this.withFieldName(query, exact, includes);
        this.withFieldTypeName(query, exact, includes);
        this.withFieldTypeSimpleName(query, exact, includes);

        return this;
    }

    output.withMethodAny = function (query, exact = false, includes = true) {
        this.withMethodReferenceName(query, exact, includes);
        this.withMethodName(query, exact, includes);
        this.withMethodReturnTypeName(query, exact, includes);
        this.withMethodReturnTypeSimpleName(query, exact, includes);

        return this;
    }

    output.withMethodParameterAny = function (query, exact = false, includes = true) {
        this.withMethodParameterReferenceName(query, exact, includes);
        this.withMethodParameterName(query, exact, includes);
        this.withMethodParameterTypeName(query, exact, includes);
        this.withMethodParameterTypeSimpleName(query, exact, includes);
        return this;
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

    // Id / ReferenceName

    output.withClassId = function (query) {
        return this.withClassFilter((subject) => subject.id() == query);
    }

    output.withClassReferenceName = function (query, exact = false, includes = true) {
        return this.withClassFilter(attributeMatcher(CLASS_ATTRIBUTES.REFERENCE_NAME, query, exact, includes));
    }

    output.withFieldTypeId = function (query, exact = false, includes = true) {
        return this.withFieldFilter(fieldTypeAttributeMatcher(CLASS_ATTRIBUTES.REFERENCE_NAME, query, exact, includes));
    }

    output.withFieldReferenceName = output.withFieldTypeId;

    output.withMethodReturnTypeId = function (query, exact = false, includes = true) {
        return this.withMethodFilter(methodTypeAttributeMatcher(CLASS_ATTRIBUTES.REFERENCE_NAME, query, exact, includes));
    }

    output.withMethodReferenceName = output.withMethodReturnTypeId;

    output.withMethodParameterTypeId = function (query, exact = false, includes = true) {
        return this.withParamFilter(parameterTypeAttributeMatcher(CLASS_ATTRIBUTES.REFERENCE_NAME, query, exact, includes));
    }

    output.withMethodParameterReferenceName = output.withMethodParameterTypeId;

    // Name

    output.withName = function (query, exact = false, includes = true) {
        this.withClassName(query, exact, includes);
        this.withFieldName(query, exact, includes);
        this.withMethodName(query, exact, includes);
        this.withMethodParameterName(query, exact, includes);
        this.withFieldTypeName(query, exact, includes);
        this.withMethodReturnTypeName(query, exact, includes);
        this.withMethodParameterTypeName(query, exact, includes);
        return this;
    }

    output.withClassName = function (query, exact = false, includes = true) {
        return this.withClassAttribute(CLASS_ATTRIBUTES.TYPED_NAME, query, exact, includes);
    }

    output.withFieldName = function (query, exact = false, includes = true) {
        return this.withFieldAttribute(FIELD_ATTRIBUTES.NAME, query, exact, includes);
    }

    output.withFieldTypeName = function (query, exact = false, includes = true) {
        return this.withFieldFilter(fieldTypeAttributeMatcher(CLASS_ATTRIBUTES.TYPED_NAME, query, exact, includes));
    }

    output.withMethodName = function (query, exact = false, includes = true) {
        return this.withMethodAttribute(METHOD_ATTRIBUTES.NAME, query, exact, includes);
    }

    output.withMethodReturnTypeName = function (query, exact = false, includes = true) {
        return this.withMethodFilter(methodTypeAttributeMatcher(CLASS_ATTRIBUTES.TYPED_NAME, query, exact, includes));
    }

    output.withMethodParameterName = function (query, exact = false, includes = true) {
        return this.withParameterAttribute(PARAMETER_ATTRIBUTES.NAME, query, exact, includes);
    }

    output.withMethodParameterTypeName = function (query, exact = false, includes = true) {
        return this.withParamFilter(parameterTypeAttributeMatcher(CLASS_ATTRIBUTES.TYPED_NAME, query, exact, includes));
    }

    // Simple Name

    output.withSimpleName = function (query, exact = false, includes = true) {
        this.withClassSimpleName(query, exact, includes);
        this.withFieldTypeSimpleName(query, exact, includes);
        this.withMethodReturnTypeSimpleName(query, exact, includes);
        this.withMethodParameterTypeSimpleName(query, exact, includes);
        this.withFieldName(query, exact, includes);
        this.withMethodParameterName(query, exact, includes);
        return this;
    }

    output.withClassSimpleName = function (query, exact = false, includes = true) {
        return this.withClassAttribute(CLASS_ATTRIBUTES.SIMPLE_NAME, query, exact, includes);
    }

    output.withFieldTypeSimpleName = function (query, exact = false, includes = true) {
        return this.withFieldFilter(fieldTypeAttributeMatcher(CLASS_ATTRIBUTES.SIMPLE_NAME, query, exact, includes));
    }

    output.withMethodReturnTypeSimpleName = function (query, exact = false, includes = true) {
        return this.withMethodFilter(methodTypeAttributeMatcher(CLASS_ATTRIBUTES.SIMPLE_NAME, query, exact, includes));
    }

    output.withMethodParameterTypeSimpleName = function (query, exact = false, includes = true) {
        return this.withParamFilter(parameterTypeAttributeMatcher(CLASS_ATTRIBUTES.SIMPLE_NAME, query, exact, includes));
    }

    // Raw Type

    output.withRawType = function (query, exact = false, includes = true) {
        const CLASS_RAW_TYPE_FILTER = classTypeAttributeMatcher(CLASS_ATTRIBUTES.RAW_TYPE, getClass);

        return this.withClassFilter((subject) => {
            return CLASS_RAW_TYPE_FILTER(subject);
        });
    }

    output.withClassRawType = function (query, exact = false, includes = true) {
        return this.withClassAttribute(CLASS_ATTRIBUTES.RAW_TYPE, query, exact, includes);
    }

    // Package

    output.withPackage = function (query, exact = false, includes = true) {
        this.withClassPackage(query, exact, includes);
        this.withFieldTypePackage(query, exact, includes);
        this.withMethodReturnTypePackage(query, exact, includes);
        this.withMethodParameterTypePackage(query, exact, includes);

        return this;
    }

    output.withClassPackage = function (query, exact = false, includes = true) {
        return this.withClassAttribute(CLASS_ATTRIBUTES.PACKAGE, query, exact, includes);
    }

    output.withFieldTypePackage = function (query, exact = false, includes = true) {
        return this.withFieldFilter(fieldTypeAttributeMatcher(CLASS_ATTRIBUTES.PACKAGE, query, exact, includes));
    }

    output.withMethodReturnTypePackage = function (query, exact = false, includes = true) {
        return this.withMethodFilter(methodTypeAttributeMatcher(CLASS_ATTRIBUTES.PACKAGE, query, exact, includes));
    }

    output.withMethodParameterTypePackage = function (query, exact = false, includes = true) {
        return this.withParamFilter(parameterTypeAttributeMatcher(CLASS_ATTRIBUTES.PACKAGE, query, exact, includes));
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

    output.withRawClassOnly = function () {
        return this.withClassFilter((subject) => subject.isRawClass());
    }

    output.withNonRawClassOnly = function () {
        return this.withClassFilter((subject) => !subject.isRawClass());
    }

    output.withTypeVariableOnly = function () {
        return this.withClassFilter((subject) => subject.isTypeVariable());
    }

    output.withNonTypeVariableOnly = function () {
        return this.withClassFilter((subject) => !subject.isTypeVariable());
    }

    output.withWildcardOnly = function () {
        return this.withClassFilter((subject) => subject.isWildcard());
    }

    output.withNonWildcardOnly = function () {
        return this.withClassFilter((subject) => !subject.isWildcard());
    }


    // Filters
    output.matchesClass = function (data) {
        if (this._classFilters.length === 0) {
            return true;
        }
        // Only accept the data if it passes all the filters
        return !this._classFilters.some((filter) => !filter(data));
    }

    output.matchesField = function (data) {
        if (this._fieldFilters.length === 0) {
            return true;
        }
        // Only accept the data if it passes all the filters
        return !this._fieldFilters.some((filter) => !filter(data));
    }

    output.matchesMethod = function (data) {
        if (this._methodFilters.length === 0) {
            return true;
        }
        // Only accept the data if it passes all the filters
        return !this._methodFilters.some((filter) => !filter(data));
    }

    output.matchesParam = function (data) {
        if (this._paramFilters.length === 0) {
            return true;
        }
        // Only accept the data if it passes all the filters
        return !this._paramFilters.some((filter) => !filter(data));
    }

    // Collector

    output.findAllThatMatch = function () {
        console.debug(`Performing search with ${this._classFilters.length} class filters, ${this._fieldFilters.length} field filters, ${this._methodFilters.length} method filters, and ${this._paramFilters.length} param filters.`);
        applyToAllClasses((subject) => {
            if (!subject) {
                console.error("subject is null!");
            }
            if (this._classFilters.length !== 0) {
                if (this.matchesClass(subject)) {
                    this.results.classes.push(subject);
                }
            }
            // TODO: Constructor Support.
            if (this._fieldFilters.length !== 0) {
                for (let field of subject.fields(true)) {
                    if (this.matchesField(field)) {
                        this.results.fields.push(field);
                    }
                }
            }
            if (this._methodFilters.length !== 0 || this._paramFilters.length !== 0) {
                if (this._methodFilters.length === 0) {
                    for (let method of subject.methods(true)) {
                        for (let param of method.parameters()) {
                            if (this.matchesParam(param)) {
                                this.results.parameters.push(method);
                                break;
                            }
                        }
                    }
                } else {
                    for (let method of subject.methods(true)) {
                        if (this._paramFilters.length === 0) {
                            if (this.matchesMethod(method)) {
                                this.results.methods.push(method);
                            }
                            continue;
                        }
                        for (let param of method.parameters()) {
                            if (this.matchesParam(param) && this.matchesMethod(method)) {
                                this.results.methods.push(method);
                                this.results.parameters.push(method);
                                break;
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

    output.sortResults = function () {
        this.results.classes.sort((a, b) => {
            return a.simplename().localeCompare(b.simplename());
        });
        this.results.fields.sort((a, b) => {
            return a.name().localeCompare(b.name());
        });
        this.results.methods.sort((a, b) => {
            return a.name().localeCompare(b.name());
        });
        this.results.parameters.sort((a, b) => {
            return a.name().localeCompare(b.name());
        });
        return this;
    }

    return output;
}

function searchFromParameters(parameters) {
    wipePage();

    setToast("Please wait while we process your query...");
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

    if (!_last_search_parameters || !compareSearchParameters(_last_search_parameters, parameters)) {
        // The search parameters have changed or don't exist, so we need to create a new filter
        console.log("Creating new filter either because the search parameters have changed or don't exist");
        _last_search_parameters = parameters;
        _last_filter = dataFilter();
        const INCLUSIVE = parameters.has('inclusive') ? parameters.get('inclusive') === 'true' : true;
        const EXACT = parameters.has('exact') ? parameters.get('exact') === 'true' : false;

        for (const key in NEW_QUERY_TERMS) {
            let value = NEW_QUERY_TERMS[key];
            if (_last_search_parameters.has(key)) {
                _last_filter[value](_last_search_parameters.get(key), EXACT, INCLUSIVE);
                continue;
            }
            let key_normalized = key.replaceAll(/[^a-z0-9-_]+/g, '');
            if (_last_search_parameters.has(key_normalized)) {
                _last_filter[value](_last_search_parameters.get(key_normalized), EXACT, INCLUSIVE);
            }
        }

        console.log("Finding all matches...");
        _last_filter.findAllThatMatch();
        console.log("Done finding all matches");
        // Sort the results
        console.log("Sorting results...");
        _last_filter.sortResults();
        console.log("Done sorting results");
    }

    loadSearchResults(page, page_size);
    clearToast();
}

function loadSearchResults(page_number, page_size) {

    let results = _last_filter.getResults();

    createPagedTable("Matching Classes", 'class-table', results.classes, (table, classData) => {
        addClassToTable(table, classData.id());
    }, 'Link', 'ID', 'Class Name', 'Package', 'Qualified Name');

    createPagedTable("Matching Fields", 'field-table', results.fields, (table, fieldData) => {
        addFieldToTable(table, fieldData.getDeclaringClass(), fieldData, fieldData.type());
    }, 'Link', 'Declared In', 'Field Signature', 'Declaration Class');

    createPagedTable("Matching Methods", 'method-table', results.methods, (table, methodData) => {
        addMethodToTable(table, methodData.getDeclaringClass(), methodData);
    }, 'Link', 'Declared In', 'Method Signature', 'Declaration Class');

    createPagedTable("Matching Parameters", 'parameter-table', results.parameters, (table, methodData) => {
        addMethodToTable(table, methodData.getDeclaringClass(), methodData);
    }, 'Link', 'Declared In', 'Method Signature', 'Declaration Class');
}

function createSearchBar() {
    const decodedURLParams = DecodeURL().params;
    // Create one div that contains the search bar
    let searchDiv = document.createElement('div');
    searchDiv.append(document.createElement('br'));

    // Create two elements, one for a search type, the other for search input
    let searchType = document.createElement('select');
    let searchInput = document.createElement('input');
    searchInput.type = 'text';
    searchInput.placeholder = 'Search...';


    // Add check box for exact search
    let exact = document.createElement('input');
    exact.type = 'checkbox';
    // Set it based on whether the URL has the exact search parameter
    exact.checked = decodedURLParams.has('exact') ? decodedURLParams.get('exact') === 'true' : false;
    let exactLabel = document.createElement('label');
    exactLabel.htmlFor = 'exact-search';
    exactLabel.innerText = 'Exact Search';

    // Add check box for inclusive search
    let inclusive = document.createElement('input');
    inclusive.type = 'checkbox';
    inclusive.checked = decodedURLParams.has('inclusive') ? decodedURLParams.get('inclusive') === 'true' : true;
    let inclusiveLabel = document.createElement('label');
    inclusiveLabel.htmlFor = 'inclusive-search';
    inclusiveLabel.innerText = 'Inclusive Search';

    // Use the enter key to change the URL
    searchInput.onkeydown = function (e) {
        if (e.key === 'Enter') {
            const type = searchType.value;
            const value = searchInput.value;
            const exactValue = exact.checked;
            const inclusiveValue = inclusive.checked;
            changeURL(`#?${type}=${value}&inclusive=${inclusiveValue}&exact=${exactValue}`);
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
    searchDiv.append(inclusive);
    searchDiv.append(inclusiveLabel);
    searchDiv.append(exact);
    searchDiv.append(exactLabel);
    document.body.append(searchDiv);
    return searchDiv;
}