function changeURL(url) {
	console.log("Changing URL to: " + url);
	history.pushState("", document.title, window.location.pathname + url);
	onHashChange();
}

function changeURLFromElement(element) {
	changeURL(element.getAttribute('href'));
}

function createLink(element, id, rawId = null, focus = null) {
	element.classList.add('link');
	let redirect = id;
	if (rawId) {
		redirect = rawId;
	}
	redirect = getClass(redirect).type();

	if (!redirect.match(/([a-z][a-z_0-9]*\.)+[A-Z_]($[A-Z_]|[\w_])*/)) {
		if (rawId) {
			redirect = rawId;
		} else {
			redirect = id;
		}
	}
	if (focus) {
		redirect += `?focus=${focus}`;
	}
	element.setAttribute('href', `#${redirect}`);
	element.setAttribute('onclick', 'changeURLFromElement(this);');

	return element;
}

function createShortLink(id, parents) {
	if (!parents) {
		parents = new Set();
	}
	if (parents.has(id)) {
		let rout = span(getClass(id).name());
		createLink(rout, id, getClass(id).rawtype());
		return rout;
	}
	parents.add(id);
	let out = document.createElement('span');
	let data = getClass(id);
	let type = span(data.name());
	let args = data.paramargs();
	let depth = null;
	if (data.isInnerClass()) {
		let firstHalf = createShortLink(data.outerclass(), parents);
		let middle = span('$');
		out.append(firstHalf);
		out.append(middle);
	}
	createLink(type, id, data.rawtype());
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
	// if (!parents) {
	// 	parents = new Set();
	// } else {
	// 	if (parents.has(id)) {
	// 		let rout = span(getClass(id).name());
	// 		createLink(rout, id, getClass(id).rawtype());
	// 		return rout;
	// 	}
	// }
	// parents.add(id);
	let data = getClass(id);
	let out = document.createElement('span');
	let name = span(data.name());
	if (data.isInnerClass()) {
		let firstHalf = createFullSignature(data.outerclass(), parents);
		let middle = span('$');
		out.append(firstHalf);
		out.append(middle);
	} else {
		let package = data.package();
		if (!exists(package)) {
			package = "";
			console.error("Package does not exist for: " + data.name(), data.data);
		}
		let parts = package.split('.');
		let part = null;
		for (let i = 0; i < parts.length; i++) {
			part = parts[i];
			out.appendChild(span(part));
			out.append('.');
		}
	}
	let args = data.paramargs();
	createLink(name, id, data.rawtype());
	appendAnnotationToolTip(name, data.annotations());
	out.appendChild(name);
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
	let type = getClass(annotation.type());
	let simple_name = span(type.simplename());
	let annotation_string = `@${type.type()}(${annotation.string()})`;
	out.append(br());
	out.append(annotation_string);
	return out;
}

function appendAttributesToClassTableRow(row, class_id) {
	let clazz = getClass(class_id);
	row.setAttribute('mod', clazz.modifiers());
	row.setAttribute('name', clazz.name());
	row.setAttribute('type', class_id);
	row.setAttribute('row-type', 'class');
	row.id = clazz.type();
	// row.setAttribute('declared-in', clazz);
}

function appendAttributesToMethodTableRow(row, class_id, method, current_class_id = null) {
	row.setAttribute('mod', method.modifiers());
	row.setAttribute('name', method.name());
	row.setAttribute('type', method.returnType());
	row.setAttribute('declared-in', class_id);
	row.setAttribute('parameters', method.parameters().length);
	row.setAttribute('row-type', 'method');
	row.setAttribute('dataIndex', method.dataIndex());
	if (current_class_id) {
		row.setAttribute('current-class', current_class_id);
	}

	row.id = method.id();
}

function appendAttributesToFieldTableRow(row, class_id, field, current_class_id = null) {
	row.setAttribute('mod', field.modifiers());
	row.setAttribute('name', field.name());
	row.setAttribute('type', field.type());
	row.setAttribute('declared-in', class_id);
	row.setAttribute('row-type', 'field');
	row.setAttribute('dataIndex', field.dataIndex());
	if (current_class_id) {
		row.setAttribute('current-class', current_class_id);
	}
	row.id = field.id();
}

function appendAttributesToConstructorTableRow(row, class_id, constructor, current_class_id = null) {
	row.setAttribute('mod', constructor.modifiers());
	row.setAttribute('parameters', constructor.parameters().length);
	row.setAttribute('declared-in', class_id);
	row.setAttribute('row-type', 'constructor');
	row.setAttribute('dataIndex', constructor.dataIndex());
	if (current_class_id) {
		row.setAttribute('current-class', current_class_id);
	}
	row.id = constructor.id();
}

function appendAttributesToRelationshipToTableRow(row, relationship, relationshipName, current_class_id = null) {
	row.setAttribute('type', relationship);
	row.setAttribute('row-type', 'relationship');

	if (current_class_id) {
		row.setAttribute('current-class', current_class_id);
	}
}

const LINK_MAP = {};

function handleClickLink(element) {
	LINK_MAP[element.id]();
}

function createLinkSpan(action) {
	let clipboard = span('');
	clipboard.innerHTML = '&#128279;'
	clipboard.setAttribute('class', 'clickable');
	clipboard.setAttribute('title', 'Copy Link to clipboard');
	clipboard.setAttribute('onclick', 'handleClickLink(this)');
	// Assign a random ID to the span
	clipboard.id = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);

	// Add the action to the map
	LINK_MAP[clipboard.id] = () => {
		clipboard = document.getElementById(clipboard.id);
		action();
		// Change the innerHTML to a checkmark
		clipboard.innerHTML = '&#10003;';
		// Wait 2 seconds
		setTimeout(() => {
			// Change the innerHTML back to a clipboard
			clipboard.innerHTML = '&#128279;';
		}, 2000);
	};
	return clipboard;
}

function copyLinkToClipboard(link, currentElementID = null) {
	return createLinkSpan(() => {
		navigator.clipboard.writeText(link).then(r => console.log("Successfully Copied link to clipboard"));
		if (currentElementID) {
			console.log("Focusing link element: " + currentElementID);
			focusElement(currentElementID);
		}
	});
}


function addClassToTable(table, class_id) {
	let clazz = getClass(class_id);
	let row = addRow(table, span(class_id), createShortLink(class_id), span(clazz.package()), createFullSignature(class_id));
	appendAttributesToClassTableRow(row, class_id);
}

function addMethodToTable(table, classID, method, current_class_id = null) {
	let row = addRow(table, href(span(classID), `#${getClass(classID).type()}`), createMethodSignature(method.data), createFullSignature(classID));
	appendAttributesToMethodTableRow(row, classID, method, current_class_id);
}

function addFieldToTable(table, class_id, field, current_class_id = null) {
	let row = addRow(table, href(span(class_id), `#${getClass(class_id).type()}`), createFieldSignature(field.data), createFullSignature(class_id));
	appendAttributesToFieldTableRow(row, class_id, field, current_class_id);
}