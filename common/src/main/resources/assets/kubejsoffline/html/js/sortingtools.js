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
		// If neither have the attribute, no change
		if (!a.hasAttribute(attribute) && !b.hasAttribute(attribute)) {
			return 0;
		}
		// If only a has the attribute, a is first
		if (a.hasAttribute(attribute) && !b.hasAttribute(attribute)) {
			return -1;
		}
		// If only b has the attribute, b is first
		if (!a.hasAttribute(attribute) && b.hasAttribute(attribute)) {
			return 1;
		}
		// If both have the attribute, compare the values
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
	// First check if a or b do not have the mod attribute
	if (!a.hasAttribute('mod') && !b.hasAttribute('mod')) {
		// If neither have the mod attribute, no change
		return 0;
	}
	if (a.hasAttribute('mod') && !b.hasAttribute('mod')) {
		// If only a has the mod attribute, a is first
		return -1;
	}
	if (!a.hasAttribute('mod') && b.hasAttribute('mod')) {
		// If only b has the mod attribute, b is first
		return 1;
	}

	// Repeat for the name attribute
	if (!a.hasAttribute('name') && !b.hasAttribute('name')) {
		// If neither have the name attribute, no change
		return 0;
	}
	if (a.hasAttribute('name') && !b.hasAttribute('name')) {
		// If only a has the name attribute, a is first
		return -1;
	}
	if (!a.hasAttribute('name') && b.hasAttribute('name')) {
		// If only b has the name attribute, b is first
		return 1;
	}

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
	'super_class': sortByModifiedAttribute('type', (type) => {
		return getClass(type).superclass();
	}),
	'simple_name': sortByModifiedAttribute('type', (type) => {
		return getClass(type).simplename();
	}),
	'type': sortByModifiedAttribute('type', (type) => {
		return getClass(type).type();
	}),
	'declared': sortByModifiedAttribute('declared-in', (type) => {
		return getClass(type).name();
	}),
	'parameter_count': sortByModifiedAttribute('parameters', (count) => {
		return parseInt(count);
	}),
}

function addSortTables() {
	let tables = [document.getElementById('fields'), document.getElementById('methods'), document.getElementById('matches')];
	for (let i = 0; i < tables.length; i++) {
		let table = tables[i];
		if (table) {
			let options = createOptions(
					option('default', SORT_FUNCTIONS.default, 'Default'),
					option('Name', () => {
						sortTable(table, SORT_FUNCTIONS.name);
					}, 'Signature'),
					option('Super Class', () => {
						sortTable(table, SORT_FUNCTIONS.super_class);
					}, 'Signature'),
					option('Return Type (Simple Name)', () => {
						sortTable(table, SORT_FUNCTIONS.simple_name);
					}, 'Signature'),
					option('Return Type (Full Name)', () => {
						sortTable(table, SORT_FUNCTIONS.type);
					}, 'Signature'),
					option('Declared In', () => {
						sortTable(table, SORT_FUNCTIONS.declared);
					}, 'Signature'),
					option('Parameter Count', () => {
						sortTable(table, SORT_FUNCTIONS.parameter_count);
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