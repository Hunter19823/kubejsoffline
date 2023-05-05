function createBaseContextMenu() {
	let menu = document.createElement('div');
	menu.classList.add('context-menu');
	menu.id = 'context-menu';

	function addMenuItem(name, action) {
		let item = document.createElement('div');
		item.classList.add('context-menu-item');
		item.innerHTML = name;
		item.onclick = action;
		menu.appendChild(item);
	}

	let title = document.createElement('div');
	title.classList.add('context-menu-title');
	title.innerHTML = 'Context Menu';
	menu.appendChild(title);

	addMenuItem('Back', () => {
		history.back();
	});
	addMenuItem('Forward', () => {
		history.forward();
	});
	addMenuItem('Reload', () => {
		onHashChange();
	});
	addMenuItem('Go To Home Page', () => {
		changeURL('');
	});

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addMenuItem('Toggle Private Members', () => {
		GLOBAL_SETTINGS.showPrivate = !GLOBAL_SETTINGS.showPrivate;
		onHashChange();
	});
	addMenuItem('Toggle Protected Members', () => {
		GLOBAL_SETTINGS.showProtected = !GLOBAL_SETTINGS.showProtected;
		onHashChange();
	});
	addMenuItem('Toggle Package Members', () => {
		GLOBAL_SETTINGS.showPackage = !GLOBAL_SETTINGS.showPackage;
		onHashChange();
	});

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addMenuItem('Toggle Methods', () => {
		GLOBAL_SETTINGS.showMethods = !GLOBAL_SETTINGS.showMethods;
		onHashChange();
	});
	addMenuItem('Toggle Inherited Methods', () => {
		GLOBAL_SETTINGS.showMethodsInherited = !GLOBAL_SETTINGS.showMethodsInherited;
		onHashChange();
	});

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addMenuItem('Toggle Fields', () => {
		GLOBAL_SETTINGS.showFields = !GLOBAL_SETTINGS.showFields;
		onHashChange();
	});
	addMenuItem('Toggle Inherited Fields', () => {
		GLOBAL_SETTINGS.showFieldsInherited = !GLOBAL_SETTINGS.showFieldsInherited;
		onHashChange();
	});

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addMenuItem('Toggle Constructors', () => {
		GLOBAL_SETTINGS.showConstructors = !GLOBAL_SETTINGS.showConstructors;
		onHashChange();
	});
	addMenuItem('Toggle Inherited Constructors', () => {
		GLOBAL_SETTINGS.showConstructorsInherited = !GLOBAL_SETTINGS.showConstructorsInherited;
		onHashChange();
	});

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addMenuItem('Toggle Relationships', () => {
		GLOBAL_SETTINGS.showRelationships = !GLOBAL_SETTINGS.showRelationships;
		onHashChange();
	});

	// Add a separator
	menu.appendChild(document.createElement('hr'));

	document.body.appendChild(menu);
	return menu;
}

let count = 0;
addEventListener('contextmenu', (event) => {
	if (count++ % 2 === 0) {
		event.preventDefault();
		let menu = document.getElementById('context-menu');
		if (!menu) {
			menu = createBaseContextMenu();
		}
		// Set the position of the menu
		// then show it
		menu.style.top = event.clientY + 'px';
		menu.style.left = event.clientX + 'px';
		menu.style.display = 'block';
	} else {
		let menu = document.getElementById('context-menu');
		if (!menu) {
			menu = createBaseContextMenu();
		}
		menu.style.display = 'none';
	}
});
addEventListener('click', (event) => {
	let menu = document.getElementById('context-menu');
	if (!menu) {
		menu = createBaseContextMenu();
	}
	menu.style.display = 'none';
	count = 0;
});