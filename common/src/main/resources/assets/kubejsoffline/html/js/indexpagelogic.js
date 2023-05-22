function loadClass(id) {
	wipePage();
	console.log("Loading class " + id);
	let data = getClass(id);
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

function loadClassIDWithQueryString(classID, queryString) {
	if (!classID) {
		console.error("No classID provided (1)");
		return;
	}
	console.log("Loading page from hash: '" + classID + "' and Query String '" + queryString + "'.");
	let id = classID;

	if (id?.length > 0) {
		loadClass(id);
		// Clear search string.
		document.getElementById('page-header').scrollIntoView();
	} else {
		console.error("No classID provided (3)");
	}
}

function onHashChange() {
	let class_id = null;
	let queryString = null;
	if (window.location.hash?.length > 0) {
		class_id = window.location.hash.substring(1);
	}
	if (window.location.search?.length > 0) {
		queryString = window.location.search.substring(1);
	}
	// TODO: Improve searching
	if (class_id) {
		loadClassIDWithQueryString(class_id, queryString);
	} else if(queryString) {
		let searchParams = new URLSearchParams(queryString);
		if(searchParams.has('search')){
			wipePage();
			if(!searchParams.has('query')){
				document.body.append(span("No query provided! :("));
				document.body.append(br());
				searchHelp();
				return;
			}
			searchHelp();
			let query = searchParams.get('query');
			switch (searchParams.get('search')) {
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
	} else {
		createHomePage();
		document.getElementById('page-header').scrollIntoView();
	}
	addSortTables();
}

addEventListener('popstate', (event) => {
	onHashChange();
});


window.onload = () => {
	onHashChange();
}