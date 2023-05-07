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


function li(content) {
	let tag = document.createElement('li');
	tag.innerText = content;
	return tag;
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
