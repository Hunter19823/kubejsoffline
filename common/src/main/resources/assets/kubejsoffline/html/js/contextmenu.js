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

	function addToggleMenuItem(name, action, initialValue = false) {
		let item = document.createElement('div');
		let toggle = createRoundedToggleSwitch(name, initialValue, action);
		item.classList.add('context-menu-item');
		item.appendChild(toggle);
		menu.appendChild(item);
	}

	function addSettingItem(name, setting) {
		addToggleMenuItem(name, () => {
			GLOBAL_SETTINGS[setting] = !GLOBAL_SETTINGS[setting];
			onHashChange();
		}, GLOBAL_SETTINGS[setting]);
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
	addSettingItem('Private Members', 'showPrivate');
	addSettingItem('Protected Members', 'showProtected');
	addSettingItem('Package Members', 'showPackage');

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addSettingItem('Methods', 'showMethods');
	addSettingItem('Inherited Methods', 'showMethodsInherited');

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addSettingItem('Fields', 'showFields');
	addSettingItem('Inherited Fields', 'showFieldsInherited');

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addSettingItem('Constructors', 'showConstructors');
	addSettingItem('Inherited Constructors', 'showConstructorsInherited');

	// Add a separator
	menu.appendChild(document.createElement('hr'));
	addSettingItem('Relationships', 'showRelationships');

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
	if (menu.style.display !== 'none') {
		// Check if the click was inside the menu
		let rect = menu.getBoundingClientRect();
		if (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom) {
			menu.style.display = 'none';
			count = 0;
		}
	}
});