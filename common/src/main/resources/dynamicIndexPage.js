function getClassData(id) {
	return DATA[id];
}

function getClassName(id) {
	return getClassData(id).name;
}

function createTable(){
	let table = document.createElement('table');
	document.body.appendChild(table);

	return table;
}

function createShortLink(id){
	let span = document.createElement('span');
	span.innerText = getClassData(id).name;

	return span;
}

function createTableWithHeaders(table, ...headers) {
	let tbody = document.createElement('tbody');
	let tr = document.createElement("tr");
	table.appendChild(tbody);
	tbody.appendChild(tr);
	for(let i=0; i<headers.length; i++){
		let th = document.createElement('th');
		tr.appendChild(th);
		th.append(headers[i]);
	}
	return tbody;
}

function addRow(table, ...data){
	let tr = document.createElement('tr');
	table.appendChild(tr);
	for(let i=0; i<data.length; i++){
		let td = document.createElement('td');
		tr.appendChild(td);
		console.log(data[i]);
		td.appendChild(createShortLink(data[i]));
	}
}



function createEventTable() {
	let keys = Object.keys(EVENTS)
	for(let i=0; i<keys.length; i++){
		let span = document.createElement('span');
		span.innerHTML = keys[i];
		let table = createTableWithHeaders(createTable(), span);
		for(let j=0; j<EVENTS[keys[i]].length; j++){
			addRow(table,EVENTS[keys[i]][j]);
		}
	}
}

createEventTable();