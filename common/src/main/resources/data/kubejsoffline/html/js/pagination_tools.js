function createPagedTable(title, table_id, data, addRowAction, ...headers) {
    if (data.length === 0) {
        return;
    }

    const decodeURL = DecodeURL();

    if (!decodeURL.params.has('page')) {
        decodeURL.params.set('page', "0");
    }
    if (!decodeURL.params.has('size')) {
        decodeURL.params.set('size', `${GLOBAL_SETTINGS.defaultSearchPageSize}`);
    }
    let page = parseInt(decodeURL.params.get('page'));
    let page_size = parseInt(decodeURL.params.get('size'));
    const focus_header = `${table_id}-header`;

    function addPaginationHeader() {
        // console.log("Adding search details for "+title+" with focus "+focus+" and page number "+page_number+" and page size "+page_size);
        const decodeURL = DecodeURL();
        let div = document.createElement('h2');

        let headerTitle = document.createElement('h3');
        headerTitle.innerText = title;
        headerTitle.id = focus_header;
        headerTitle.style.fontSize = 'revert';
        div.append(headerTitle);

        function linkify(tag) {
            tag.classList.add('link');
        }

        let lastPage = Math.ceil(data.length / page_size) - 1;
        let currentPage = Math.min(page, lastPage);

        // Add a previous button, if needed
        div.classList.add('search-pagination');
        div.classList.add('stick-able');
        if (currentPage > 0) {
            let prev = span("Previous");
            div.append(prev);
            div.append(span("    "));
            // The Previous button should go to the minimum of the last page and the previous page
            decodeURL.params.set('page', `${Math.min(currentPage - 1, lastPage)}`);
            decodeURL.params.set('size', `${page_size}`);
            decodeURL.params.set('focus', focus_header);
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
            decodeURL.params.set('page', `${currentPage + 1}`);
            decodeURL.params.set('size', `${page_size}`);
            decodeURL.params.set('focus', focus_header);
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

    // Determine the start and end of the page
    let start = Math.max(0, Math.min(page * page_size, data.length - page_size));
    let end = Math.min(start + page_size, data.length);
    for (let i = start; i < end; i++) {
        addRowAction(classTable, data[i]);
    }

    return classTable;
}