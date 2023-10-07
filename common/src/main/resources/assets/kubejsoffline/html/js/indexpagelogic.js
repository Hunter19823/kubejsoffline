function loadClass(id) {
    console.log("Loading class " + id);
    let data = getClass(id);
    if (!data) {
        console.error("No class data found for id " + id);
        return;
    }
    wipePage();
    let superClass = data.superclass();
    let interfaces = data.interfaces();
    let h1 = document.createElement('h3');
    let _interface = null;
    document.body.append(h1);
    h1.append(createFullSignature(id));

    if (superClass) {
        h1.append(span(" extends "));
        h1.append(createFullSignature(superClass));
    }

    if (interfaces) {
        h1.append(span(" implements "));
        let i = 0;
        for (_interface of interfaces) {
            h1.append(createFullSignature(_interface));
            if (i < interfaces.size - 1) {
                h1.append(', ');
            }
            i++;
        }
    }
    createConstructorTable(id);
    createFieldTable(id);
    createMethodTable(id);
    createRelationshipTable(id);
}

function focusElement(elementId) {
    console.log("Focusing element " + elementId);
    if (!elementId) {
        elementId = "page-header";
    }
    let element = document.getElementById(elementId);
    if (element) {
        for (const e of document.getElementsByClassName("focused")) {
            console.log("UnFocused element " + e.id);
            e.classList.remove("focused");
        }
        element.classList.add("focused");
        console.log("Focused element " + elementId);
        if (element.tagName === "H1" || element.tagName === "H2") {
            element.scrollIntoView();
            console.log("Brought Search element into view: " + elementId);
        } else {
            const elementRect = element.getBoundingClientRect();
            const absoluteElementTop = elementRect.top + window.scrollY;
            const middle = absoluteElementTop - (window.innerHeight / 2);
            window.scrollTo(0, middle);
            console.log("Scrolled to middle of element " + elementId);
        }
    }
}

function scrollToText(text) {
    console.warn("Chrome Scroll to highlighted text is not implemented.");
}

function onHashChange() {
    // If we have a hash on the URL, determine the format:
    // # - Load the index page / home page.
    // #<int|qualifiedClassName|simpleClassName> - Load a specific class
    // Reroute the old way of searching to the new way of searching.

    // Old way of searching:
    // #<search-term>--<search-query> - Search for a term in the search query.
    // New way of searching:
    // #?<search-term>=<search-query> - Search using querystring behind the hash to prevent browser from refreshing.

    // All of these urls can also have :~:text=<url-encoded-text> appended to them to scroll to a specific part of the page.
    // This normally is done automatically by the browser but we do it manually due to the way we load pages.

    // If any of these urls have a query string, we need to parse it so we can use it later.
    // For example, if we have a url like #?focus=<id>, we need to parse the so we can jump to the element with that id on the
    // home page after it loads.


    // If we have a normal query string, just append it to the hash and reload.
    // This is to allow for the back button to work properly.
    let hash = null;
    let queryString = null;
    if (window.location.hash?.length > 0) {
        hash = window.location.hash.substring(1);
    }
    if (window.location.search?.length > 0) {
        queryString = window.location.search.substring(1);
    }
    if (!hash) {
        hash = "#";
    }
    if (hash.startsWith("#")) {
        hash = hash.substring(1);
    }
    if (!queryString) {
        queryString = "";
    }

    if (queryString) {
        console.log("Removing Query string from URL and reloading it as a hash for optimization purposes.");
        window.location.assign(window.location.pathname + "#" + hash + "?" + queryString);
        return;
    }
    if (hash.includes("%E2%80%94")) {
        console.log("Removing %E2%80%94 from URL and reloading it as a hash for optimization purposes.");
        window.location.assign(window.location.pathname + "#" + hash.replace("%E2%80%94", "--"));
        return;
    }


    // Let's quickly check if we have the old way of searching where we use -- instead of ?.
    // If we do, we need to convert it to the new way of searching.
    if (hash.includes("---")) {
        let split = hash.split("---");
        let page = split[0];
        let focus = split[1];
        console.log("Removing old focus format from URL and reloading it as a hash for optimization purposes.");
        window.location.assign(window.location.pathname + "#" + page + "?focus=" + focus);
        return;
    }

    if (hash.includes("--")) {
        let split = hash.split("--");
        let searchTerm = split[0];
        let searchQuery = split[1];
        console.log("Removing old search format from URL and reloading it as a hash for optimization purposes.");
        window.location.assign(window.location.pathname + "#?" + searchTerm + "=" + searchQuery);
        return;
    }

    // Now that we have our re-routing logic out of the way, we can rely on the page decoder to do the rest.
    let decoded = DecodeURL();

    console.log(`Decoded URL created in hash change. Raw Hash: '${window.location.hash}' Decoded Hash: '${decoded.hash}' Params: '${decoded.params.toString()}' Href: '${decoded.href()}' Is Homepage: '${decoded.isHome()}' Is Class: '${decoded.isClass()}' Is Search: '${decoded.isSearch()}' Has Focus: '${decoded.hasFocus()}' Focus: '${decoded.getFocusOrDefaultHeader()}' Parameter Size: '${decoded.getParamSize()}' Safe Parameter Size: '${decoded.getParamSizeSafe()}'`);
    if (!decoded) {
        console.error("Failed to decode URL.");
        return;
    }

    let hasState = false;

    // Is this the home page?
    if (decoded.isHome()) {
        console.log("Loading Homepage.");

        // Load the home page.
        createHomePage();

        hasState = true;

    }

    // Is this a class page?
    if (decoded.isClass()) {
        if (hasState) {
            console.error("Error state in URL detected.Cannot be a class and a homepage at the same time.");
            return;
        }
        console.log("Loading Class from URL.");

        // Load the class.
        loadClass(decoded.hash);

        hasState = true;
    }

    // Is this a search page?
    if (decoded.isSearch()) {
        if (hasState) {
            console.error("Error state in URL detected. Cannot be a search and a class/homepage at the same time.");
            return;
        }
        console.log("Loading search from URL.");

        // Load the search.
        searchFromParameters(DecodeURL().params);

        hasState = true;
    }

    if (!hasState) {
        console.error("Error state in URL detected. Unable to determine what page to load.");
        return;
    }
    if (hasState) {
        // Add sort tables.
        addSortTables();

        // Add link icons.
        addLinkIcons();

        // Focus the element.
        focusElement(decoded.getFocusOrDefaultHeader());

        // Add Sticky Headers.
        handleStickyElements();
    }

    // Now that we've loaded the page, we can scroll to the highlighted text.
    if (decoded.chromeHighlightText) {
        // Decode the text.
        let text = decodeURIComponent(decoded.chromeHighlightText);
        // Scroll to the text.
        scrollToText(text);
    }
}

function DecodeURL() {
    let output = {};
    let hash = location.hash;
    if (hash?.length > 0) {
        hash = hash.substring(1);
    }

    hash = decodeURI(hash);

    if (hash.includes(":~:")) {
        let split = hash.split(":~:");
        hash = split[0];
        output.chromeHighlightText = split[1];
    }
    output.params = new URLSearchParams("");

    if (hash.includes("?") && !hash.includes("? extends ")) {
        let split = hash.split("?");
        hash = split[0];
        output.params = new URLSearchParams(split[1]);
    }

    output.hash = hash;

    output.hasFocus = function () {
        return this.params.has("focus");
    }

    output.getFocus = function () {
        return this.params.get("focus");
    }

    output.getFocusOrDefaultHeader = function () {
        if (this.hasFocus()) {
            return this.getFocus();
        }
        return "page-header";
    }

    output.getParamSize = function () {
        return this.params.size;
    }

    output.getParamSizeSafe = function () {
        return [...this.params.keys()].length;
    }

    output.isSearch = function () {
        if (this.getParamSizeSafe() === 0) {
            return false;
        }
        if (this.getParamSizeSafe() !== 1) {
            return true;
        }
        // If thee is no focus, then it's a search as the only parameter must be the search term.
        return !this.hasFocus();
    }

    output.isClass = function () {
        if (this.isSearch()) {
            return false;
        }
        return this.hash?.length > 0;
    }

    output.isHome = function () {
        if (this.isSearch()) {
            return false;
        }
        return this.hash?.length === 0;
    }

    output.href = function () {
        return `${window.location.origin}${window.location.pathname}#${this.hash}?${this.params.toString()}`;
    }

    return output;
}

addEventListener('popstate', (event) => {
    console.log("Popstate.");
    onHashChange();
});


window.onload = () => {
    console.log("Window Loaded.");
    onHashChange();
}

document.onload = () => {
    console.log("Document Loaded.");
    createInPageLog();
}


function createInPageLog() {
    let log = document.createElement("div");
    log.id = "log";
    log.classList.add("refresh-persistent");
    document.body.append(log);
}

if (GLOBAL_SETTINGS.debug) {
    const MESSAGES = [];
    (function () {
        const old = console.log;
        console.log = function () {
            if (!document.getElementById('log')) {
                let logger = document.getElementById('log');
                if (logger) {
                    logger.innerHTML = MESSAGES.join('') + '<br />';
                }
                createInPageLog();
            }
            let logger = document.getElementById('log');
            for (let i = 0; i < arguments.length; i++) {
                if (typeof arguments[i] == 'object') {
                    MESSAGES.push((JSON && JSON.stringify ? JSON.stringify(arguments[i], undefined, 2) : arguments[i]) + '<br />');
                } else {
                    MESSAGES.push(arguments[i] + '<br />');
                }
                logger.innerHTML += MESSAGES[MESSAGES.length - 1];
            }

            old(...arguments);
        }
        console.error = console.log;
        console.warn = console.log;
    })();
}