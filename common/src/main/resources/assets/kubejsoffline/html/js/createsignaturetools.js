function changeURL(url) {
	history.pushState("", document.title, window.location.pathname + url);
	onHashChange();
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
		redirect += `---${focus}`;
	}
	element.onclick = () => changeURL(`#${redirect}`);
}

function createShortLink(id, parents) {
	if (!parents) {
		parents = new Set();
	}
	if (parents.has(id)) {
		let rout = span(getClass(id).simplename());
		createLink(rout, id, getClass(id).rawtype());
		return rout;
	}
	parents.add(id);
	let out = document.createElement('span');
	let data = getClass(id);
	let type = span(data.simplename());
	let args = data.paramargs();
	let depth = null;
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
	if (!parents) {
		parents = new Set();
	} else {
		if (parents.has(id)) {
			let rout = span(getClass(id).simplename());
			createLink(rout, id, getClass(id).rawtype());
			return rout;
		}
	}
	parents.add(id);
	let data = getClass(id);
	let out = document.createElement('span');
	let parts = data.name().split('.');
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
			createLink(sp, id, data.rawtype());
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

function appendAttributesToClassTableRow(row, class_id) {
	let clazz = getClass(class_id);
	row.setAttribute('mod', clazz.modifiers());
	row.setAttribute('name', clazz.name());
	row.setAttribute('type', class_id);
	row.setAttribute('row-type', 'class');
	row.id = class_id;
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

function createLinkSpan(action) {
	let clipboard = span('');
	clipboard.innerHTML = '&#128279;'
	clipboard.setAttribute('class', 'link');
	clipboard.setAttribute('title', 'Copy Link to clipboard');
	clipboard.onclick = () => {
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

function copyLinkToClipboard(link) {
	return createLinkSpan(() => {
		navigator.clipboard.writeText(link).then(r => console.log("Successfully Copied link to clipboard"));
	});
}


function addClassToTable(table, class_id) {
	let clazz = getClass(class_id);
	let copyLink = copyLinkToClipboard(clazz.hrefLink());
	let row = addRow(table, div(copyLink, span(class_id)), createShortLink(class_id), span(clazz.package()), createFullSignature(class_id));
	appendAttributesToClassTableRow(row, class_id);
}

function addMethodToTable(table, classID, method, current_class_id = null) {
	let copyLink = copyLinkToClipboard(method.hrefLink());
	let row = addRow(table, div(copyLink, span(classID)), createMethodSignature(method.data), createFullSignature(classID));
	appendAttributesToMethodTableRow(row, classID, method, current_class_id);
}

function addFieldToTable(table, class_id, field, current_class_id = null) {
	let copyLink = copyLinkToClipboard(field.hrefLink());
	let row = addRow(table, div(copyLink, span(class_id)), createFieldSignature(field.data), createShortLink(field.type()), createFullSignature(class_id));
	appendAttributesToFieldTableRow(row, class_id, field, current_class_id);
}