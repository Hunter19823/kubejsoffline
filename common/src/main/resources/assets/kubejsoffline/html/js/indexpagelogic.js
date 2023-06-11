function loadClass(id) {
	wipePage();
	console.log("Loading class " + id);
	let data = getClass(id);
	if (!data) {
		console.error("No class data found for id " + id);
		return;
	}
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

function loadClassIDWithJump(classID, member = null) {
	if (!classID) {
		console.error("No classID provided (1)");
		return;
	}
	if (classID.startsWith("#")) {
		// Invalid classID.
		console.error("No valid classID provided (2)");
		return;
	}

	wipePage();
	let id = classID;

	if (id?.length > 0) {
		loadClass(id);
		// Clear search string.
		focusElement(member);
	} else {
		console.error("No classID provided (3)");
	}
}

function focusElement(elementId) {
	if (!elementId) {
		return;
	}
	let element = document.getElementById(elementId);
	if (element) {
		element.scrollIntoView();
		for (const e of document.getElementsByClassName("focused")) {
			e.classList.remove("focused");
		}
		element.classList.add("focused");
	}
}

function onHashChange() {
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
	// If we have a hash on the URL, determine the format:
	// # - Load the index page
	// #<int|qualifiedClassName|simpleClassName> - Load a specific class
	// #<int|qualifiedClassName|simpleClassName>---<memberTable> - Load a specific class and scroll to a specific member table.
	// #<int|qualifiedClassName|simpleClassName>---<memberTable>---<memberIndex> - Load a specific class and scroll to a specific member in the member table.
	// Old way of searching:
	// #<search-term>--<search-query> - Search for a term in the search query.
	// New way of searching:
	// #?<search-term>=<search-query> - Search using querystring behind the hash to prevent browser from refreshing.

	// All of these urls can also have :~:text=<url-encoded-text> appended to them to scroll to a specific part of the page.
	// This normally is done automatically by the browser but we do it manually due to the way we load pages.


	// If we have a query string, just append it to the hash and reload.
	// This is to allow for the back button to work properly.
	if (queryString) {
		console.log("Removing Query string from URL and reloading it as a hash for optimization purposes.");
		window.location.assign(window.location.pathname + "#" + hash + "?" + queryString);
		return;
	}

	// If we have a hash, but no query string, we need to parse the hash.
	// First, check if there is a scroll target.
	let scrollTarget = null;
	if (hash.includes(":~:")) {
		let split = hash.split(":~:");
		hash = split[0];
		scrollTarget = split[1];
	}
	// Next check if this is the home page.
	if (hash === "") {
		createHomePage();
		document.getElementById('page-header').scrollIntoView();
		addSortTables();
		return;
	}

	hash = hash.replace("%E2%80%94", "--");

	// Next check if this hash contains a member jump target.
	let jumpID = null;
	if (hash.includes("---")) {
		let split = hash.split("---");
		hash = split[0];
		jumpID = split[1];
	}

	// Next check if this is a class ID (int)
	let id = parseInt(hash);
	if (!isNaN(id)) {
		loadClassIDWithJump(hash, jumpID);
		return;
	}

	// Check if this is the old way of searching
	if (hash.includes("--")) {
		let split = hash.split("--");
		switch (split.length) {
			case 2:
				hash = `?${split[0]}=${split[1]}`;
				break;
			default:
				console.error("Invalid search hash: " + hash);
				break;
		}
		return;
	}

	// Check if this is the new way of searching
	if (hash.startsWith("?")) {
		let parms = new URLSearchParams(hash)
		searchFromParameters(parms);
		return;
	}

	// Let's assume it's a class ID.
	loadClassIDWithJump(hash, jumpID);
}

addEventListener('popstate', (event) => {
	onHashChange();
});


window.onload = () => {
	onHashChange();
}