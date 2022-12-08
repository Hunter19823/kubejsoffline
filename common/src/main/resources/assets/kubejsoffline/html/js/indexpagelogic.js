function createTable() {
	let table = document.createElement('table');
	document.body.appendChild(table);

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

function sortTable(table, comparison) {
	let rows = Array.from(table.rows);
	rows.sort(comparison);
	for (let i = 0; i < rows.length; i++) {
		table.appendChild(rows[i]);
	}
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
}

function span(text) {
	let span = document.createElement('span');
	span.innerText = text;
	return span;
}

function breakLine(){
	let br = document.createElement('br');
	document.body.appendChild(br);
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
	for(let opt of args){
		option = document.createElement('option');
		option.value = opt.text;
		option.innerText = opt.text;
		actionMap.set(opt.text, opt.action);
		if(opt.group && opt.group !== 'Misc') {
			if(!groupMap.has(opt.group)){
				groupMap.set(opt.group, document.createElement('optgroup'));
				groupMap.get(opt.group).label = opt.group;
				output.appendChild(groupMap.get(opt.group));
			}
			groupMap.get(opt.group).appendChild(option);
		}else {
			if(!groupMap.has('Misc')){
				groupMap.set('Misc', document.createElement('optgroup'));
				groupMap.get('Misc').label = 'Misc';
			}
			groupMap.get('Misc').appendChild(option);
		}
	}
	if(groupMap.has('Misc')){
		output.appendChild(groupMap.get('Misc'));
	}
	output.onchange = () => {
		if(actionMap.has(output.value)) {
			actionMap.get(output.value)();
		}else{
			console.error('No action for ' + output.value);
		}
	}
	return output;
}

function createShortLink(id) {
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
		loadClass(id);
	}
	out.append(type);
	if (args) {
		out.append('<');
		for (let i = 0; i < args.length; i++) {
			out.appendChild(createShortLink(args[i]));
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

function createFullSignature(id) {
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
		}
	}
	if (args) {
		out.append('<');
		for (let i = 0; i < args.length; i++) {
			out.appendChild(createFullSignature(args[i]));
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
	out.append(span(MODIFIER.toString(method.modifiers()) + " "));
	out.append(createShortLink(method.returnType()));
	out.append(' ');
	out.append(method.name());
	out.append('(');
	for (let i = 0; i < parameters.length; i++) {
		param = getParameter(parameters[i]);
		out.appendChild(createShortLink(param.type()));
		out.append(' ' + param.name());
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
	out.append(span(MODIFIER.toString(field.modifiers()) + " "));
	out.append(createShortLink(field.type()));
	out.append(' ');
	out.append(field.name());
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
		table = createTableWithHeaders(createTable(), span);
		for (let j = 0; j < EVENTS[keys[i]].length; j++) {
			addRow(table, EVENTS[keys[i]][j]);
		}
	}
}

function createMethodTable(id) {
	let methods = getClass(id).methods();
	let table = null;
	let method = null;
	if (methods) {
		table = createTableWithHeaders(createTable(), 'MethodSignature', 'Return Type');
		for (method of methods) {
			addRow(table, createMethodSignature(method), createFullSignature(getMethod(method).returnType()));
		}
	}
}

function createFieldTable(id) {
	let fields = getClass(id).fields();
	let table = null;
	let field = null;
	if (fields) {
		table = createTableWithHeaders(createTable(), 'Field Signature', 'Type');
		for (field of fields) {
			addRow(table, createFieldSignature(field), createFullSignature(getField(field).type()));
		}
	}
}

function createRelationshipTable(id) {
	let data = getClass(id);
	let table = createTableWithHeaders(createTable(), 'Relationship', 'RelatedClass');
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
	if(seen.size === 0) {
		table.parentNode.removeChild(table);
	}
}

function loadClass(id) {
	wipePage();
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
		let i=0;
		for (_interface of interfaces) {
			h1.append(createFullSignature(_interface));
			if (i < interfaces.size - 1) {
				h1.append(', ');
			}
			i++;
		}
	}
	breakLine();
	createFieldTable(id);
	breakLine();
	createMethodTable(id);
	breakLine();
	createRelationshipTable(id);
}

function createPageHeader() {
	let header = document.createElement('div');
	header.id = 'page-header';
	header.style.textDecoration = 'underline';
	header.style.color = '#8cb4ff';
	header.style.cursor = 'pointer';
	header.onclick = () => {
		location.hash = "";
	};
	let title = document.createElement('h1');
	title.innerHTML = 'KubeJS Offline';
	header.appendChild(title);
	document.body.append(header);
}

// Clear the page of all content
function wipePage() {
	document.body.innerHTML = '';
	createPageHeader();
}

function loadPageFromHash(hash) {
	if (hash?.includes('#')) {
		let id = hash.split('#')[1];
		if(id?.length > 0) {
			loadClass(id);
			return;
		}
	}
	createHomePage();
}

window.onload = () => {
	loadPageFromHash(location.hash);
}

addEventListener('hashchange', (event) => {
	loadPageFromHash(event.newURL);
});