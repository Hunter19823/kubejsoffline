function createTable(id) {
	breakLine();
	let table = document.createElement('table');
	document.body.appendChild(table);
	if (id && typeof id === 'string')
		table.id = id;
	return table;
}

function createTableWithHeaders(table, ...headers) {
	let tbody = document.createElement('tbody');
	let tr = document.createElement("tr");
	let th = null;
	table.appendChild(tbody);
	tbody.appendChild(tr);
	for (let i = 0; i < headers.length; i++) {
		th = document.createElement('th');
		tr.appendChild(th);
		th.append(headers[i]);
	}
	return tbody;
}

function addRow(table, ...data) {
	let tr = document.createElement('tr');
	let td = null;
	table.appendChild(tr);
	for (let i = 0; i < data.length; i++) {
		td = document.createElement('td');
		tr.appendChild(td);
		if (typeof data[i] === 'number') {
			td.appendChild(createFullSignature(data[i]));
		} else {
			td.appendChild(data[i]);
		}
	}
	return tr;
}

function span(text) {
	let span = document.createElement('span');
	span.innerText = text;
	return span;
}

function breakLine() {
	document.body.appendChild(br());
}

function br() {
	return document.createElement('br');
}

function option(text, action, group) {
	return {
		text: text,
		action: action,
		group: group
	};
}

function createOptions(...args) {
	let output = document.createElement('select');
	let option = null;
	let groupMap = new Map();
	let actionMap = new Map();
	for (let opt of args) {
		option = document.createElement('option');
		option.value = opt.text;
		option.innerText = opt.text;
		actionMap.set(opt.text, opt.action);
		if (opt.group && opt.group !== 'Misc') {
			if (!groupMap.has(opt.group)) {
				groupMap.set(opt.group, document.createElement('optgroup'));
				groupMap.get(opt.group).label = opt.group;
				output.appendChild(groupMap.get(opt.group));
			}
			groupMap.get(opt.group).appendChild(option);
		} else {
			if (!groupMap.has('Misc')) {
				groupMap.set('Misc', document.createElement('optgroup'));
				groupMap.get('Misc').label = 'Misc';
			}
			groupMap.get('Misc').appendChild(option);
		}
	}
	if (groupMap.has('Misc')) {
		output.appendChild(groupMap.get('Misc'));
	}
	output.onchange = () => {
		if (actionMap.has(output.value)) {
			actionMap.get(output.value)();
		} else {
			console.error('No action for ' + output.value);
		}
	}
	return output;
}

function appendAnnotationToolTip(tag, annotations) {
	if (!annotations || annotations.size === 0)
		return;

	tag.classList.add('tooltip');
	let tooltip = document.createElement('div');
	tooltip.classList.add('tooltiptext');
	for (let annotation of annotations) {
		tooltip.appendChild(createAnnotationSignature(annotation));
	}
	tag.appendChild(tooltip);
}

function createShortLink(id, parents) {
	if (!parents) {
		parents = new Set();
	}
	if (parents.has(id)) {
		return span(getClass(id).simplename());
	}
	parents.add(id);
	let out = document.createElement('span');
	let data = getClass(id);
	let type = span(data.simplename());
	let args = data.paramargs();
	let depth = null;
	type.style.textDecoration = 'underline';
	type.style.color = '#8cb4ff';
	type.style.cursor = 'pointer';
	type.onclick = () => {
		location.hash = "#" + id;
	}
	out.append(type);
	if (args) {
		out.append('<');
		for (let i = 0; i < args.length; i++) {
			out.appendChild(createShortLink(args[i], parents));
			if (i < args.length - 1) {
				out.append(', ');
			}
		}
		out.append('>');
	}
	depth = data.arrayDepth();
	if (depth) {
		for (let i = 0; i < depth; i++) {
			type.append('[]');
		}
	}
	return out;
}

function createFullSignature(id, parents) {
	if (!parents) {
		parents = new Set();
	} else {
		if (parents.has(id)) {
			return span(getClass(id).simplename());
		}
	}
	parents.add(id);
	let data = getClass(id);
	let out = document.createElement('span');
	let parts = data.type().split('.');
	let part = null;
	let sp = null;
	let args = data.paramargs();
	for (let i = 0; i < parts.length; i++) {
		part = parts[i];
		sp = span(part);
		out.appendChild(sp);
		if (i < parts.length - 1) {
			out.append('.');
		} else {
			sp.style.textDecoration = 'underline';
			sp.style.color = '#8cb4ff';
			sp.style.cursor = 'pointer';
			sp.onclick = () => {
				location.hash = "#" + id;
			}
			appendAnnotationToolTip(sp, data.annotations());
		}
	}
	if (args) {
		out.append('<');
		for (let i = 0; i < args.length; i++) {
			out.appendChild(createFullSignature(args[i], parents));
			if (i < args.length - 1) {
				out.append(', ');
			}
		}
		out.append('>');
	}

	return out;
}

function createMethodSignature(method_data) {
	let out = document.createElement('span');
	let method = getMethod(method_data);
	let parameters = method.parameters();
	let param = null;
	let name = span(method.name());
	appendAnnotationToolTip(name, method.annotations());
	out.append(span(MODIFIER.toString(method.modifiers()) + " "));
	out.append(createShortLink(method.returnType()));
	out.append(' ');
	out.append(name);
	out.append('(');
	for (let i = 0; i < parameters.length; i++) {
		param = getParameter(parameters[i]);
		out.appendChild(createShortLink(param.type()));
		name = span(param.name());
		appendAnnotationToolTip(name, param.annotations());
		out.append(' ');
		out.append(name);
		if (i < parameters.length - 1) {
			out.append(', ');
		}
	}
	out.append(')');
	return out;
}

function createFieldSignature(field_data) {
	let field = getField(field_data);
	let out = document.createElement('span');
	let name = span(field.name());
	appendAnnotationToolTip(name, field.annotations());
	out.append(span(MODIFIER.toString(field.modifiers()) + " "));
	out.append(createShortLink(field.type()));
	out.append(' ');
	out.append(name);
	return out;
}

function createConstructorSignature(constructor_data, classID) {
	let class_type = getClass(classID);
	let constructor = getConstructor(constructor_data);
	let out = document.createElement('span');
	let parameters = constructor.parameters();
	let param = null;
	let name = null;
	out.append(span(MODIFIER.toString(constructor.modifiers()) + " "));
	out.append(createShortLink(class_type.id()));
	out.append('(');
	for (let i = 0; i < parameters.length; i++) {
		param = getParameter(parameters[i]);
		out.appendChild(createShortLink(param.type()));
		name = span(param.name());
		appendAnnotationToolTip(name, param.annotations());
		out.append(' ');
		out.append(name);
		if (i < parameters.length - 1) {
			out.append(', ');
		}
	}
	out.append(')');
	return out;
}

function createAnnotationSignature(annotation_data) {
	let annotation = getAnnotation(annotation_data);
	let out = document.createElement('span');
	out.append(span(getClass(annotation.type()).simplename()));
	out.append(br());
	out.append(annotation.string());
	return out;
}

function createHomePage() {
	wipePage();
	let keys = Object.keys(EVENTS);
	let span = null;
	let table = null;
	for (let i = 0; i < keys.length; i++) {
		span = document.createElement('span');
		span.innerHTML = keys[i];
		let period = keys[i]?.lastIndexOf('.');
		table = createTableWithHeaders(createTable(period === -1 ? keys[i] : keys[i].substring(period + 1)), span);
		for (let j = 0; j < EVENTS[keys[i]].length; j++) {
			addRow(table, EVENTS[keys[i]][j]);
		}
	}
}

function createMethodTable(id) {
	let methods = getClass(id).methods();
	let table = null;
	let method = null;
	let meth = null;
	let row = null;
	if (methods) {
		table = createTableWithHeaders(createTable('methods'), 'Methods', 'Return Type');
		for (method of methods) {
			meth = getMethod(method);
			row = addRow(table, createMethodSignature(method), createFullSignature(getMethod(method).returnType()));
			row.setAttribute('name', meth.name());
			row.setAttribute('returnType', meth.returnType());
			row.setAttribute('mod', meth.modifiers());
			row.setAttribute('params', meth.parameters());
		}
	}
}

function createFieldTable(id) {
	let fields = getClass(id).fields();
	let table = null;
	let field = null;
	let row = null;
	let data = null;
	if (fields) {
		table = createTableWithHeaders(createTable('fields'), 'Fields', 'Type');
		for (field of fields) {
			data = getField(field);
			row = addRow(table, createFieldSignature(field), createFullSignature(getField(field).type()));
			row.setAttribute('name', data.name());
			row.setAttribute('type', data.type());
			row.setAttribute('mod', data.modifiers());
		}
	}
}

function createRelationshipTable(id) {
	let data = getClass(id);
	let table = createTableWithHeaders(createTable('relations'), 'Relationships', 'RelatedClass');
	let seen = new Set();
	let relation = null;
	for (let i = 0; i < RELATIONS.length; i++) {
		relation = data.relation(i);
		if (relation) {
			for (let j = 0; j < relation.length; j++) {
				if (!seen.has(relation[j])) {
					addRow(table, span(RELATIONS[i]), createFullSignature(relation[j]));
					seen.add(relation[j]);
				}
			}
		}
	}
	if (seen.size === 0) {
		table.parentNode.removeChild(table);
	}
}

function createConstructorTable(id) {
	let constructors = getClass(id).constructors();
	let table = null;
	let constructor = null;
	let row = null;
	let cons = null;
	if (constructors) {
		table = createTableWithHeaders(createTable('constructors'), 'Constructors');
		for (constructor of constructors) {
			cons = getConstructor(constructor);
			row = addRow(table, createConstructorSignature(constructor, id));
			row.setAttribute('mod', cons.modifiers());
			row.setAttribute('params', cons.parameters());
		}
	}
}

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

function createPageHeader() {
	let header = document.createElement('div');
	let title = document.createElement('h1');
	let img = document.createElement('img');
	header.id = 'page-header';
	header.style.textDecoration = 'underline';
	header.style.color = '#8cb4ff';
	header.style.cursor = 'pointer';
	title.onclick = () => {
		location.hash = "";
	};
	img.onclick = () => {
		location.hash = "";
	};
	title.innerHTML = 'KubeJS Offline';
	img.src = 'https://raw.githubusercontent.com/Hunter19823/kubejsoffline/master/kubejs_offline_logo.png';
	img.style.height = '7em';
	img.onerror = () => {
		img.style.display = 'none';
	};
	header.appendChild(img);
	header.appendChild(title);
	document.body.append(header);
}

// Clear the page of all content
function wipePage() {
	document.body.innerHTML = '';
	createPageHeader();
}

function loadClassIDWithQueryString(classID, queryString) {
	if (!classID) {
		console.error("No classID provided (1)");
		return;
	}
	console.log("Loading page from hash: '" + classID + "' and Query String '" + queryString + "'.");
	let id = "0";
	if (classID?.includes('?')) {
		idString = classID.split('?')[0];
		if (idString?.length > 0) {
			id = idString;
		} else {
			console.error("No classID provided (2)");
			return;
		}

		queryString = classID.split('?')[1];
	} else {
		id = classID;
	}
	if (id?.length > 0) {
		loadClass(id);
		document.getElementById('page-header').scrollIntoView();
	} else {
		console.error("No classID provided (3)");

	}
}

function swapTags(a, b) {
	let parent = a.parentNode;
	let t = document.createElement('div');
	parent.append(t);
	parent.insertBefore(t, a);
	parent.replaceChild(a, b);
	parent.replaceChild(b, t);
}

function checkTableSorted(trs, comparator) {
	// Get the rows as an array
	for (let i = 2; i < trs.length; i++) {
		if (comparator(trs[i - 1], trs[i]) > 0) {
			return false;
		}
	}
	return true;
}

// Sort the table using insertion sort, ignore the first row
function sortTable(table, comparator) {
	// Get the rows as an array
	let trs = table.getElementsByTagName('tr');
	while (!checkTableSorted(trs, comparator))
			// Loop through the rows
			// Starting at the second row, index 2
		for (let i = 2; i < trs.length; i++) {
			// Get the current row
			let row = trs[i];
			// Store the last element needed to be swapped.
			let toSwap = null;
			// Loop through the rows before the current row
			for (let j = i - 1; j >= 1; j--) {
				// Get the row before the current row
				let prev = trs[j];
				// If the current row is less than the previous row
				if (comparator(row, prev) < 0) {
					// Swap the rows
					toSwap = prev;
				} else {
					// The row is in the correct position
					break;
				}
			}
			if (toSwap) {
				swapTags(row, toSwap);
			}
		}
}

function sortByAttribute(attribute) {
	return (a, b) => {
		let aAttr = a.getAttribute(attribute);
		let bAttr = b.getAttribute(attribute);
		if (aAttr < bAttr) {
			return -1;
		}
		if (aAttr > bAttr) {
			return 1;
		}
		return 0;
	}
}

function sortByModifiedAttribute(attribute, mutator) {
	return (a, b) => {
		let aAttr = mutator(a.getAttribute(attribute));
		let bAttr = mutator(b.getAttribute(attribute));
		if (aAttr < bAttr) {
			return -1;
		}
		if (aAttr > bAttr) {
			return 1;
		}
		return 0;
	}
}

function defaultSort(a, b) {
	// Sorting order:
	// 1. public/protected/private
	// 2. static/non-static
	// 3. alphabetical
	// Get the mod attribute of the rows
	let aMod = a.getAttribute('mod');
	let bMod = b.getAttribute('mod');
	// Get the name attribute of the rows
	let aName = a.getAttribute('name');
	let bName = b.getAttribute('name');

	if (a === b) {
		return 0;
	}

	// If a is public and b is not
	if (MODIFIER.isPublic(aMod) && !MODIFIER.isPublic(bMod)) {
		// a goes before b
		return -1;
	}
	// If b is public and a is not
	if (MODIFIER.isPublic(bMod) && !MODIFIER.isPublic(aMod)) {
		// b goes before a
		return 1;
	}

	// If a is protected and b is not
	if (MODIFIER.isProtected(aMod) && !MODIFIER.isProtected(bMod)) {
		// a goes before b
		return -1;
	}
	// If b is protected and a is not
	if (MODIFIER.isProtected(bMod) && !MODIFIER.isProtected(aMod)) {
		// b goes before a
		return 1;
	}

	// If a is private and b is not
	if (MODIFIER.isPrivate(aMod) && !MODIFIER.isPrivate(bMod)) {
		// a goes before b
		return -1;
	}
	// If b is private and a is not
	if (MODIFIER.isPrivate(bMod) && !MODIFIER.isPrivate(aMod)) {
		// b goes before a
		return 1;
	}

	// if a is static and b is not
	if (MODIFIER.isStatic(aMod) && !MODIFIER.isStatic(bMod)) {
		// a goes before b
		return -1;
	}
	// if b is static and a is not
	if (MODIFIER.isStatic(bMod) && !MODIFIER.isStatic(aMod)) {
		// b goes before a
		return 1;
	}
	// if a and b are both static
	if (MODIFIER.isStatic(aMod) && MODIFIER.isStatic(bMod)) {
		// Compare the names
		if (aName < bName) {
			return -1;
		}
		if (aName > bName) {
			return 1;
		}
		return 0;
	}
}

const SORT_FUNCTIONS = {
	'default': defaultSort,
	'name': sortByAttribute('name'),
	'public': sortByModifiedAttribute('mod', (mod) => {
		return MODIFIER.isPublic(mod);
	}),
	'protected': sortByModifiedAttribute('mod', (mod) => {
		return MODIFIER.isProtected(mod);
	}),
	'private': sortByModifiedAttribute('mod', (mod) => {
		return MODIFIER.isPrivate(mod);
	}),
	'final': sortByModifiedAttribute('mod', (mod) => {
		return MODIFIER.isFinal(mod);
	}),
	'static': sortByModifiedAttribute('mod', (mod) => {
		return MODIFIER.isStatic(mod);
	}),
	'super class': sortByModifiedAttribute('type', (type) => {
		return getClass(type).superclass();
	}),
	'return type simplename': sortByModifiedAttribute('type', (type) => {
		return getClass(type).simplename();
	})
}

function addSortTables() {
	let tables = [document.getElementById('fields'), document.getElementById('methods')];
	for (let i = 0; i < tables.length; i++) {
		let table = tables[i];
		if (table) {
			let options = createOptions(
					option('default', SORT_FUNCTIONS.default, 'Default'),
					option('Name', () => {
						sortTable(table, SORT_FUNCTIONS.name);
					}, 'Signature'),
					option('Access', () => {
						sortTable(table, SORT_FUNCTIONS.public);
						sortTable(table, SORT_FUNCTIONS.protected);
						sortTable(table, SORT_FUNCTIONS.private);
						sortTable(table, SORT_FUNCTIONS.static);
					}, 'Access Modifier'),
					option('Public', () => {
						sortTable(table, SORT_FUNCTIONS.public);
					}, 'Access Modifier'),
					option('Protected', () => {
						sortTable(table, SORT_FUNCTIONS.protected);
					}, 'Access Modifier'),
					option('Private', () => {
						sortTable(table, SORT_FUNCTIONS.private);
					}, 'Access Modifier'),
			);
			let sortDiv = document.createElement('div');
			let sortLabel = document.createElement('label');
			sortLabel.setAttribute('for', 'sort');
			sortLabel.textContent = 'Sort by: ';
			sortDiv.appendChild(sortLabel);
			sortDiv.appendChild(options);
			table.parentNode.insertBefore(sortDiv, table);
			sortTable(table, SORT_FUNCTIONS.default);
		}
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
	// TODO: Add searching
	if (class_id) {
		loadClassIDWithQueryString(class_id, queryString);
	} else {
		createHomePage();
		document.getElementById('page-header').scrollIntoView();
	}
	addSortTables();
}

addEventListener('hashchange', (event) => {
	onHashChange();
});


window.onload = () => {
	onHashChange();
}