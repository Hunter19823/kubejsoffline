function createPagedTable(title, table_id, data, addRowAction, ...headers) {
    if (data.length === 0) {
        return;
    }

    return new PageableSortableTable(
            title,
            table_id,
            ...headers
    ).setRowAction(addRowAction).setData(data);
}

function hasAttribute(element, attribute) {
    if (!exists(element)) {
        return false;
    }
    if (!exists(element[attribute])) {
        return false;
    }
    if (typeof element[attribute] === 'function') {
        return exists(element[attribute]());
    } else {
        return exists(element[attribute]);
    }
}

function attributeComparator(attribute, comparator, mutator = (a) => a) {

    function mapAttribute(element, attribute) {
        if (typeof element[attribute] === 'function') {
            return mutator(element[attribute]());
        } else {
            return mutator(element[attribute]);
        }
    }

    return (a, b) => {
        if (!hasAttribute(a, attribute) && !hasAttribute(b, attribute)) {
            return 0;
        }
        if (!hasAttribute(a, attribute)) {
            return -1;
        }
        if (!hasAttribute(b, attribute)) {
            return 1;
        }
        return comparator(mapAttribute(a, attribute), mapAttribute(b, attribute));
    };
}

function sortByModifier(a, b) {
    // If a is public and b is not
    if (MODIFIER.isPublic(a) && !MODIFIER.isPublic(b)) {
        // a goes before b
        return -1;
    }
    // If b is public and a is not
    if (MODIFIER.isPublic(b) && !MODIFIER.isPublic(a)) {
        // b goes before a
        return 1;
    }

    // If a is protected and b is not
    if (MODIFIER.isProtected(a) && !MODIFIER.isProtected(b)) {
        // a goes before b
        return -1;
    }
    // If b is protected and a is not
    if (MODIFIER.isProtected(b) && !MODIFIER.isProtected(a)) {
        // b goes before a
        return 1;
    }

    // If a is private and b is not
    if (MODIFIER.isPrivate(a) && !MODIFIER.isPrivate(b)) {
        // a goes before b
        return -1;
    }
    // If b is private and a is not
    if (MODIFIER.isPrivate(b) && !MODIFIER.isPrivate(a)) {
        // b goes before a
        return 1;
    }

    // if a is static and b is not
    if (MODIFIER.isStatic(a) && !MODIFIER.isStatic(b)) {
        // a goes before b
        return -1;
    }
    // if b is static and a is not
    if (MODIFIER.isStatic(b) && !MODIFIER.isStatic(a)) {
        // b goes before a
        return 1;
    }
    // if a and b are both static
    return 0;
}

function sortByName(a, b) {
    return a.localeCompare(b);
}

function defaultSort(a, b) {
    let modSort = sortByModifier(a.getModifiers(), b.getModifiers());
    if (modSort !== 0) {
        return modSort;
    }
    return sortByName(a.getName(), b.getName());
}

function wrapComparator(comparator, wrapper) {
    return (a, b) => {
        return comparator(wrapper(a), wrapper(b));
    };
}

PageableSortableTable = class {
    static SORTABLE_DEFAULT = ['default', defaultSort];
    static SORTABLE_BY_NAME = ['name', attributeComparator('getName', sortByName, (a) => a.toLowerCase())];
    static SORTABLE_BY_MOD = ['mod', attributeComparator('getModifier', sortByModifier)];
    static SORTABLE_BY_TYPE = ['type', attributeComparator('getType', sortByName, (a) => getClass(a).name().toLowerCase())];
    static SORTABLE_BY_DECLARING_CLASS = ['declaring-class', attributeComparator('getDeclaringClass', sortByName, (a) => getClass(a).name().toLowerCase())];


    /**
     * Creates a pageable and sortable table.
     * @param {string} title
     * @param {string} table_id
     * @param {Node | string} headers
     */
    constructor(title, table_id, ...headers) {
        this.title = title;
        this.table_id = table_id;
        this.headers = headers;
        this.url = DecodeURL();
        this.PARAMETER_PAGE_NUMBER = `${this.table_id}-page`;
        this.PARAMETER_PAGE_SIZE = `${this.table_id}-page-size`;
        this.PARAMETER_EXPANDED = `${this.table_id}-expanded`;
        this.PARAMETER_FOCUS = 'focus';
        this.TABLE_HEADER = `${this.table_id}-header`;
        this.PARAMETER_SORT_BY = `${this.table_id}-sort-by`;
        this.PARAMETER_SORT_DIRECTION = `${this.table_id}-sort-direction`;

        this.data = [];
        this.page = (this.url.params.has(this.PARAMETER_PAGE_NUMBER)) ? parseInt(this.url.params.get(this.PARAMETER_PAGE_NUMBER)) : 0;
        this.page_size = (this.url.params.has(this.PARAMETER_PAGE_SIZE)) ? parseInt(this.url.params.get(this.PARAMETER_PAGE_SIZE)) : GLOBAL_SETTINGS.defaultSearchPageSize;
        this.expand = (this.url.params.has(this.PARAMETER_EXPANDED)) ? this.url.params.get(this.PARAMETER_EXPANDED) === 'true' : false;
        this.sort_by = (this.url.params.has(this.PARAMETER_SORT_BY)) ? this.url.params.get(this.PARAMETER_SORT_BY) : 'default';
        this.sort = (a, b) => a - b;
        this.sort_order = (this.url.params.has(this.PARAMETER_SORT_DIRECTION)) ? parseInt(this.url.params.get(this.PARAMETER_SORT_DIRECTION)) : 1;
        this.sort_options = {};

        this.table_items = [];
        this.table_header_element = null;
        this.table_header_pages_div = null;
        this.table_element = null;
        this.table_header_row = null;
        this.table_body = null;
    }

    setRowAction(rowAction) {
        this.rowAction = rowAction;
        return this;
    }

    setData(data) {
        this.data = data;
        return this;
    }

    setPage(page) {
        this.page = page;
        return this;
    }

    setPageSize(page_size) {
        this.page_size = page_size;
        return this;
    }

    setExpand(expand) {
        this.expand = expand;
        return this;
    }

    setSort(sort) {
        this.sort = sort;
        return this;
    }

    setSortOrder(sort_order) {
        this.sort_order = sort_order;
        return this;
    }

    setSortOptions(sort_options) {
        this.sort_options = sort_options;
        return this;
    }

    getCurrentSort() {
        if (this.sort_options.hasOwnProperty(this.sort_by)) this.setSort(this.sort_options[this.sort_by]);

        return this.sort;
    }

    getCurrentData() {
        let data = this.data;
        let sort = this.getCurrentSort();
        if (exists(sort)) {
            data.sort(sort);
        }
        if (this.sort_order < 0) {
            data.reverse();
        }
        if (this.expand) {
            return data;
        }
        let start = Math.max(
                0,
                Math.min(
                        this.page * this.page_size,
                        data.length - this.page_size
                )
        );
        let end = Math.min(
                start + this.page_size,
                data.length
        );

        data = data.slice(start, end);

        return data;
    }

    addSortOption(option, sort) {
        this.sort_options[option] = sort;
        return this;
    }

    addSortOptionPair([option, sort], wrapper=undefined) {
        if (exists(wrapper)) {
            sort = wrapComparator(sort, wrapper);
        }
        return this.addSortOption(option, sort);
    }

    getTableHeader() {
        return this.table_header_element;
    }

    getTable() {
        return this.table_element;
    }

    getTableHeaderRow() {
        return this.table_header_row;
    }

    getTableBody() {
        return this.table_body;
    }

    getTableDiv() {
        return this.table_items;
    }

    createTableHeader() {
        // Create the pagination header
        this.table_header_element = document.createElement('h2');
        this.table_header_pages_div = document.createElement('div');

        let headerTitle = document.createElement('h3');
        headerTitle.innerText = this.title;
        headerTitle.id = this.TABLE_HEADER;
        headerTitle.style.fontSize = 'revert';
        addLinkToElement(headerTitle, this.TABLE_HEADER);
        this.table_header_element.append(headerTitle);
        this.table_header_element.append(this.table_header_pages_div);

        this.table_header_element.classList.add('search-pagination');
        this.table_header_element.classList.add('stick-able');
        this.url.params.set(this.PARAMETER_FOCUS, this.TABLE_HEADER);

        // If the url has `expand-{table_id}` then add a link to collapse the table
        if (this.expand) {
            this.url.params.set(this.PARAMETER_EXPANDED, 'false');
            const COLLAPSE_TABLE = this.url.hrefHash();
            let collapse = span("Collapse Results");
            this.table_header_pages_div.append(collapse);
            this.table_header_pages_div.append(span("    "));
            collapse.setAttribute('href', `${COLLAPSE_TABLE}`)
            collapse.setAttribute('onclick', 'changeURLFromElement(this);');
            collapse.classList.add('link');
            this.table_header_pages_div.append(span("    "));
            return this;
        } else if (!(this.page_size >= this.data.length)) {
            this.url.params.set(this.PARAMETER_EXPANDED, 'true');
            const EXPAND_TABLE = this.url.hrefHash();
            let expand = span("Expand All Results");
            this.table_header_pages_div.append(expand);
            this.table_header_pages_div.append(span("    "));
            expand.setAttribute('href', `${EXPAND_TABLE}`)
            expand.setAttribute('onclick', 'changeURLFromElement(this);');
            expand.classList.add('link');
            this.table_header_pages_div.append(span("    "));
        }
        this.url.params.set(this.PARAMETER_EXPANDED, 'false');

        // Add a previous button, if needed
        let lastPage = Math.ceil(this.data.length / this.page_size) - 1;
        let currentPage = Math.min(this.page, lastPage);
        if (currentPage > 0) {
            let prev = span("Previous");
            this.table_header_pages_div.append(prev);
            this.table_header_pages_div.append(span("    "));
            // The Previous button should go to the minimum of the last page and the previous page
            this.url.params.set(this.PARAMETER_PAGE_NUMBER, `${Math.min(currentPage - 1, lastPage)}`);
            this.url.params.set(this.PARAMETER_PAGE_SIZE, `${this.page_size}`);
            this.url.params.set(this.PARAMETER_FOCUS, this.TABLE_HEADER);
            const PREV_PAGE = this.url.hrefHash();
            prev.setAttribute('href', `${PREV_PAGE}`)
            prev.setAttribute('onclick', 'changeURLFromElement(this);');
            prev.classList.add('link');
        }

        // Add the number of results and how many total results there are
        this.table_header_pages_div.append(span(`Page ${currentPage + 1} of ${lastPage + 1} (${this.data.length} total results)`));

        // Add a next button, if needed
        if (this.data.length > (currentPage + 1) * this.page_size) {
            this.table_header_pages_div.append(span("    "));
            let next = span("Next");
            this.table_header_pages_div.append(next);
            // The Previous button should go to the minimum of the last page and the previous page
            this.url.params.set(this.PARAMETER_PAGE_NUMBER, `${currentPage + 1}`);
            this.url.params.set(this.PARAMETER_PAGE_SIZE, `${this.page_size}`);
            this.url.params.set(this.PARAMETER_FOCUS, this.TABLE_HEADER);
            const NEXT_PAGE = this.url.hrefHash();
            next.setAttribute('href', `${NEXT_PAGE}`)
            next.setAttribute('onclick', 'changeURLFromElement(this);');
            next.classList.add('link');
        }

        return this;
    }

    // TODO: createTableDiv, which adds a newline before table.

    createTable() {
        // Create a class table
        this.table_element = document.createElement('table');
        this.table_element.id = this.table_id;
        this.table_element.classList.add('sortable-table');

        // Create the table body and rows
        this.table_body = document.createElement('tbody');
        this.table_element.appendChild(this.table_body);

        // Create the table header row
        this.table_header_row = document.createElement("tr");
        this.table_body.appendChild(this.table_header_row);

        // Create the table headers
        for (let i = 0; i < this.headers.length; i++) {
            let th = document.createElement('th');
            this.table_header_row.appendChild(th);
            th.append(this.headers[i]);
        }

        let data = this.getCurrentData();

        // Add the rows
        for (let i = 0; i < data.length; i++) {
            this.rowAction(this.table_body, data[i]);
        }

        return this;
    }

    createDiv() {
        this.table_items = [
            document.createElement('br'),
            this.table_header_element,
            this.table_element
        ];
        return this;
    }

    addToDocument() {
        for (let item of this.table_items) {
            document.body.appendChild(item);
        }
        return this;
    }

    create() {
        this.createTableHeader().createTable().createDiv().addToDocument();
        GLOBAL_DATA[this.table_id] = this;
        return this;
    }

    sortableByClass(mutator=getClass) {
        return this
                .addSortOptionPair(PageableSortableTable.SORTABLE_DEFAULT, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_NAME, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_MOD, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_TYPE, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_DECLARING_CLASS, mutator);
    }

    sortableByMethod(mutator=getMethod) {
        return this
                .addSortOptionPair(PageableSortableTable.SORTABLE_DEFAULT, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_NAME, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_MOD, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_TYPE, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_DECLARING_CLASS, mutator);
    }

    sortableByField(mutator=getField) {
        return this
                .addSortOptionPair(PageableSortableTable.SORTABLE_DEFAULT, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_NAME, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_MOD, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_TYPE, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_DECLARING_CLASS, mutator);
    }

    sortableByParameter(mutator=getParameter) {
        return this
                .addSortOptionPair(PageableSortableTable.SORTABLE_DEFAULT, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_NAME, mutator)
                .addSortOptionPair(PageableSortableTable.SORTABLE_BY_TYPE, mutator);
    }
}