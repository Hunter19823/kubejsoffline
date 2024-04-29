function createPagedTable(title, table_id, data, addRowAction, ...headers) {
    if (data.length === 0) {
        return;
    }

    const PARAMETER_PAGE_NUMBER = `${table_id}-page`;
    const PARAMETER_PAGE_SIZE = `${table_id}-page-size`;
    const PARAMETER_EXPANDED = `${table_id}-expanded`;
    const PARAMETER_FOCUS = 'focus';
    const TABLE_HEADER = `${table_id}-header`;

    const decodeURL = DecodeURL();

    if (!decodeURL.params.has(PARAMETER_PAGE_NUMBER)) {
        decodeURL.params.set(PARAMETER_PAGE_NUMBER, "0");
    }
    if (!decodeURL.params.has(PARAMETER_PAGE_SIZE)) {
        decodeURL.params.set(PARAMETER_PAGE_SIZE, `${GLOBAL_SETTINGS.defaultSearchPageSize}`);
    }
    if (!decodeURL.params.has(PARAMETER_EXPANDED)) {
        decodeURL.params.set(PARAMETER_EXPANDED, 'false');
    }
    let page = parseInt(decodeURL.params.get(PARAMETER_PAGE_NUMBER));
    let page_size = parseInt(decodeURL.params.get(PARAMETER_PAGE_SIZE));
    let expand = decodeURL.params.get(PARAMETER_EXPANDED) === 'true';

    function addPaginationHeader() {
        // console.log("Adding search details for "+title+" with focus "+focus+" and page number "+page_number+" and page size "+page_size);
        const decodeURL = DecodeURL();
        let div = document.createElement('h2');

        let headerTitle = document.createElement('h3');
        headerTitle.innerText = title;
        headerTitle.id = TABLE_HEADER;
        headerTitle.style.fontSize = 'revert';
        div.append(headerTitle);

        function linkify(tag) {
            tag.classList.add('link');
        }

        div.classList.add('search-pagination');
        div.classList.add('stick-able');
        decodeURL.params.set(PARAMETER_FOCUS, TABLE_HEADER);

        // If the url has `expand-{table_id}` then add a link to collapse the table
        if (expand) {
            decodeURL.params.set(PARAMETER_EXPANDED, 'false');
            const COLLAPSE_TABLE = decodeURL.hrefHash();
            let collapse = span("Collapse Results");
            div.append(collapse);
            div.append(span("    "));
            collapse.setAttribute('href', `${COLLAPSE_TABLE}`)
            collapse.setAttribute('onclick', 'changeURLFromElement(this);');
            linkify(collapse);
            div.append(span("    "));
            return div;
        } else if (!(page_size >= data.length)) {
            decodeURL.params.set(PARAMETER_EXPANDED, 'true');
            const EXPAND_TABLE = decodeURL.hrefHash();
            let expand = span("Expand All Results");
            div.append(expand);
            div.append(span("    "));
            expand.setAttribute('href', `${EXPAND_TABLE}`)
            expand.setAttribute('onclick', 'changeURLFromElement(this);');
            linkify(expand);
            div.append(span("    "));
        }
        decodeURL.params.set(PARAMETER_EXPANDED, 'false');

        // Add a previous button, if needed
        let lastPage = Math.ceil(data.length / page_size) - 1;
        let currentPage = Math.min(page, lastPage);
        if (currentPage > 0) {
            let prev = span("Previous");
            div.append(prev);
            div.append(span("    "));
            // The Previous button should go to the minimum of the last page and the previous page
            decodeURL.params.set(PARAMETER_PAGE_NUMBER, `${Math.min(currentPage - 1, lastPage)}`);
            decodeURL.params.set(PARAMETER_PAGE_SIZE, `${page_size}`);
            decodeURL.params.set(PARAMETER_FOCUS, TABLE_HEADER);
            const PREV_PAGE = decodeURL.hrefHash();
            prev.setAttribute('href', `${PREV_PAGE}`)
            prev.setAttribute('onclick', 'changeURLFromElement(this);');
            linkify(prev);
        }

        // Add the number of results and how many total results there are
        div.append(span(`Page ${currentPage + 1} of ${lastPage + 1} (${data.length} total results)`));

        // Add a next button, if needed
        if (data.length > (currentPage + 1) * page_size) {
            div.append(span("    "));
            let next = span("Next");
            div.append(next);
            // The Previous button should go to the minimum of the last page and the previous page
            decodeURL.params.set(PARAMETER_PAGE_NUMBER, `${currentPage + 1}`);
            decodeURL.params.set(PARAMETER_PAGE_SIZE, `${page_size}`);
            decodeURL.params.set(PARAMETER_FOCUS, TABLE_HEADER);
            const NEXT_PAGE = decodeURL.hrefHash();
            next.setAttribute('href', `${NEXT_PAGE}`)
            next.setAttribute('onclick', 'changeURLFromElement(this);');
            linkify(next);
        }

        return div;
    }

    // Add the search details
    document.body.append(addPaginationHeader());

    // Create a class table
    let classTable = createTableWithHeaders(createSortableTable(table_id), ...headers);

    // If the table is expanded, add all the rows
    if (expand) {
        for (let i = 0; i < data.length; i++) {
            addRowAction(classTable, data[i]);
        }
        return classTable;
    }

    // Determine the start and end of the page
    let start = Math.max(0, Math.min(page * page_size, data.length - page_size));
    let end = Math.min(start + page_size, data.length);
    for (let i = start; i < end; i++) {
        addRowAction(classTable, data[i]);
    }

    return classTable;
}