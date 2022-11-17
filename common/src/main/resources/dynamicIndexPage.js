let MODIFIER = {
	PUBLIC: 1,
	PRIVATE: 2,
	PROTECTED: 4,
	STATIC: 8,
	FINAL: 16,
	SYNCHRONIZED: 32,
	VOLATILE: 64,
	TRANSIENT: 128,
	NATIVE: 256,
	INTERFACE: 512,
	ABSTRACT: 1024,
	STRICT: 2048,
	BRIDGE: 64,
	VARARGS: 128,
	SYNTHETIC: 4096,
	ANNOTATION: 8192,
	ENUM: 16384,
	MANDATED: 32768,
	CLASS_MODIFIERS: 3103,
	INTERFACE_MODIFIERS: 3087,
	CONSTRUCTOR_MODIFIERS: 7,
	METHOD_MODIFIERS: 3391,
	FIELD_MODIFIERS: 223,
	PARAMETER_MODIFIERS: 16,
	ACCESS_MODIFIERS: 7,

	isPublic(mod) {
		return (mod & 1) !== 0;
	},

	isPrivate(mod) {
		return (mod & 2) !== 0;
	},

	isProtected(mod) {
		return (mod & 4) !== 0;
	},

	isStatic(mod) {
		return (mod & 8) !== 0;
	},

	isFinal(mod) {
		return (mod & 16) !== 0;
	},

	isSynchronized(mod) {
		return (mod & 32) !== 0;
	},

	isVolatile(mod) {
		return (mod & 64) !== 0;
	},

	isTransient(mod) {
		return (mod & 128) !== 0;
	},

	isNative(mod) {
		return (mod & 256) !== 0;
	},

	isInterface(mod) {
		return (mod & 512) !== 0;
	},

	isAbstract(mod) {
		return (mod & 1024) !== 0;
	},

	isStrict(mod) {
		return (mod & 2048) !== 0;
	},

	toString(mod){
		let sj = [];
		if (this.isPublic(mod)) {
			sj.push("public");
		}

		if (this.isProtected(mod)) {
			sj.push("protected");
		}

		if (this.isPrivate(mod)) {
			sj.push("private");
		}

		if (this.isAbstract(mod)) {
			sj.push("abstract");
		}

		if (this.isStatic(mod)) {
			sj.push("static");
		}

		if (this.isFinal(mod)) {
			sj.push("final");
		}

		if (this.isTransient(mod)) {
			sj.push("transient");
		}

		if (this.isVolatile(mod)) {
			sj.push("volatile");
		}

		if (this.isSynchronized(mod)) {
			sj.push("synchronized");
		}

		if (this.isNative(mod)) {
			sj.push("native");
		}

		if (this.isStrict(mod)) {
			sj.push("strictfp");
		}

		if (this.isInterface(mod)) {
			sj.push("interface");
		}

		return sj.join(' ');
	},

	isSynthetic(mod){
		return (mod & 4096) !== 0;
	},

	isMandated(mod) {
		return (mod & '耀') !== 0;
	},

	classModifiers() {
		return 3103;
	},

	interfaceModifiers() {
		return 3087;
	},

	constructorModifiers() {
		return 7;
	},

	methodModifiers(){
		return 3391;
	},

	fieldModifiers() {
		return 223;
	},

	parameterModifiers() {
		return 16;
	}
}

function getClassData(id) {
	return DATA[id];
}

function getClassName(id) {
	return getClassData(id).name;
}

let HISTORY = [];
let FUTURE = [];
let CURRENT;

function back() {
	console.log(HISTORY);
	if(HISTORY.length>=1){
		let temp = HISTORY.pop();
		FUTURE.push(temp);
		temp();
	}
}

function forward() {
	console.log(FUTURE);
	if(FUTURE.length>0){
		FUTURE.pop()();
	}
}


function createBackButton() {
	let button = document.createElement('button');
	button.innerText = 'Back';
	button.onclick = () => {
		back();
		console.log("Back");
	};
	return button;
}

function createForwardButton() {
	let button = document.createElement('button');
	button.innerText = 'Forward';
	button.onclick = () => {
		forward();
		console.log("Forward");
	};
	return button;
}

function getParameterizedArgs(id) {
	let data = getClassData(id);
	if(!data['args']) {
		return [];
	}
	[data].push()
	return data['args'].map(getClassData);
}

function logAllMeetingCondition(condition) {
	if(typeof condition === 'function') {
		let out = [];
		for(let i=0; i<DATA.length; i++) {
			try {
				if (condition(DATA[i])) {
					console.log(DATA[i]);
				}
			} catch (e) {
				console.error(e);
			}
		}
	}
}

function logNamesContaining(str) {
	logAllMeetingCondition((data) => data.name.includes(str));
}

function logAllArrays() {
	logAllMeetingCondition((data) => data['isArray']);
}

function logAllParameterized() {
	logAllMeetingCondition((data) => data['args']);
}

function logAllParameterizedArrays() {
	logAllMeetingCondition((data) => data['args'] && data['isArray']);
}

function createTable(){
	let table = document.createElement('table');
	document.body.appendChild(table);

	return table;
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
		if(typeof data[i] === 'number') {
			td.appendChild(createFullSignature(data[i]));
		}else {
			td.appendChild(data[i]);
		}
	}
}

function span(text) {
	let span = document.createElement('span');
	span.innerText = text;
	return span;
}

function createShortLink(id){
	let out = document.createElement('span');
	let data = getClassData(id);
	let type = span(data['name'].substring(data['name'].lastIndexOf('.')+1));
	type.style.textDecoration = 'underline';
	type.style.color = '#8cb4ff';
	type.style.cursor = 'pointer';
	type.onclick = () => {
		if(CURRENT) {
			HISTORY.push(CURRENT);
		}
		loadClass(id);
	}
	out.append(type);
	if(data['args']){
		let args = data['args'];
		out.append('<');
		for(let i=0; i<args.length; i++){
			out.appendChild(createShortLink(args[i]));
			if(i<args.length-1) {
				out.append(', ');
			}
		}
		out.append('>');
	}

	if(data['isArray']){
		for (let i = 0; i < data['arrayDepth']; i++) {
			out.append('[]');
		}
	}
	return out;
}

function createFullSignature(id) {
	let data = getClassData(id);
	let out = document.createElement('span');
	let parts = data['type'].split('.');
	for(let i=0; i<parts.length; i++){
		let part = parts[i];
		let sp = span(part);
		out.appendChild(sp);
		if(i<parts.length-1){
			out.append('.');
		}else{
			sp.style.textDecoration = 'underline';
			sp.style.color = '#8cb4ff';
			sp.style.cursor = 'pointer';
			sp.onclick = () => {
				if(CURRENT) {
					HISTORY.push(CURRENT);
				}
				loadClass(id);
			}
		}
	}
	// let type = span(data['type']);
	// // Underline the type
	// type.style.cursor = 'pointer';
	// type.onclick = () => {
	// 	if(CURRENT) {
	// 		HISTORY.push(CURRENT);
	// 	}
	// 	loadClass(id);
	// }
	// out.appendChild(type);

	if(data['args']){
		let args = data['args'];
		out.append('<');
		for(let i=0; i<args.length; i++){
			out.appendChild(createFullSignature(args[i]));
			if(i<args.length-1) {
				out.append(', ');
			}
		}
		out.append('>');
	}
	// if(data['isArray']){
	// 	for (let i = 0; i < data['arrayDepth']; i++) {
	// 		out.append('[]');
	// 	}
	// }

	return out;
}


function createMethodSignature(method_data) {
	let out = document.createElement('span');
	out.append(span(MODIFIER.toString(method_data['mod'])+" "));
	out.append(createShortLink(method_data['type']));
	out.append(' ');
	out.append(method_data['name']);
	out.append('(');
	let parameters = method_data['param'];
	for(let i=0; i<parameters.length; i++){
		out.appendChild(createShortLink(parameters[i]['type']));
		out.append(' '+parameters[i]['name']);
		if(i<parameters.length-1){
			out.append(', ');
		}
	}
	out.append(')');
	return out;
}

function createFieldSignature(field_data) {
	let out = document.createElement('span');
	out.append(span(MODIFIER.toString(field_data['mod'])+" "));
	out.append(createShortLink(field_data['type']));
	out.append(' ');
	out.append(field_data['name']);
	return out;
}



function createEventTable() {
	wipePage();
	if(CURRENT) {
		HISTORY.push(CURRENT);
	}

	CURRENT = () => {
		createEventTable();
	}
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

function createMethodTable(id) {
	let data = getClassData(id);
	if(data['meth']) {
		let table = createTableWithHeaders(createTable(), 'MethodSignature', 'Return Type');
		let methods = data['meth'];
		for (let i = 0; i < methods.length; i++) {
			let method = methods[i];
			addRow(table, createMethodSignature(method), createFullSignature(method['type']));
		}
		let parents = new Set();
		parents.add(id);
		while(data['superclass'] && !parents.has(data['superclass'])) {
			data = getClassData(data['superclass']);
			parents.add(data['id']);
			if(data['meth']) {
				for (let i = 0; i < data['meth'].length; i++) {
					let method = data['meth'][i];
					addRow(table, createMethodSignature(method), createFullSignature(method['type']));
				}
			}
		}
	}
}

function createFieldTable(id) {
	let data = getClassData(id);
	if(data['fields']) {
		let table = createTableWithHeaders(createTable(), 'Field Signature', 'Type');
		let fields = data['fields'];
		for (let i = 0; i < fields.length; i++) {
			let field = fields[i];
			addRow(table, createFieldSignature(field), createFullSignature(field['type']));
		}
	}
}

function createRelationshipTable(id) {
	let data = getClassData(id);
	let table = createTableWithHeaders(createTable(), 'RelatedClass', 'Relationship');
	let seen = new Set();
	for(let i=0; i<RELATIONS.length; i++){
		if(data[""+i]) {
			for(let j=0; j<data[""+i].length; j++){
				if(!seen.has(data[""+i][j])) {
					addRow(table, createFullSignature(data["" + i][j]), span(RELATIONS[i]));
					seen.add(data["" + i][j]);
				}
			}
		}
	}
}



function loadClass(id) {
	wipePage();

	CURRENT = () => {
		loadClass(id);
	}
	let data = getClassData(id);
	let h1 = document.createElement('h3');
	document.body.append(h1);
	h1.append(createFullSignature(data.id));
	if (data['superclass']) {
		h1.append(span(" extends "));
		h1.append(createFullSignature(data['superclass']));
	}
	if(data['interfaces']) {
		h1.append(span(" implements "));
		let interfaces = data['interfaces'];
		for (let i = 0; i < interfaces.length; i++) {
			h1.append(createFullSignature(interfaces[i]));
			if (i < interfaces.length - 1) {
				h1.append(', ');
			}
		}
	}
	createFieldTable(id);
	createMethodTable(id);
	createRelationshipTable(id);
}


// Clear the page of all content
function wipePage() {
	document.body.innerHTML = '';
	document.body.append(createBackButton());
	document.body.append(createForwardButton());
}
//createEventTable();






window.onload = () => {
	createEventTable();
}

createEventTable();