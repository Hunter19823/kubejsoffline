function createPageHeader() {
	let header = document.createElement('div');
	let title = document.createElement('h1');
	let img = document.createElement('img');
	header.id = 'page-header';
	header.style.textDecoration = 'underline';
	header.style.color = '#8cb4ff';
	header.style.cursor = 'pointer';
	title.onclick = () => {
		changeURL("");
	};
	img.onclick = () => {
		changeURL("");
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

function createHomePage() {
	wipePage();
	let keys = Object.keys(EVENTS);
	let span = null;
	let table = null;
	for (let i = 0; i < keys.length; i++) {
		span = document.createElement('span');
		span.innerHTML = keys[i];
		let period = keys[i]?.lastIndexOf('.');
		table = createTableWithHeaders(createSortableTable(period === -1 ? keys[i] : keys[i].substring(period + 1)), span);
		for (let j = 0; j < EVENTS[keys[i]].length; j++) {
			let row = addRow(table, EVENTS[keys[i]][j]);
			appendAttributesToClassTableRow(row, EVENTS[keys[i]][j]);
		}
	}
}
