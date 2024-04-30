// I Stole this code from https://stackoverflow.com/questions/1482832/how-to-get-all-elements-that-are-highlighted
// Thank you for your help, StackOverflow!
function rangeIntersectsNode(range, node) {
    var nodeRange;
    if (range.intersectsNode) {
        return range.intersectsNode(node);
    } else {
        nodeRange = node.ownerDocument.createRange();
        try {
            nodeRange.selectNode(node);
        } catch (e) {
            nodeRange.selectNodeContents(node);
        }

        return range.compareBoundaryPoints(Range.END_TO_START, nodeRange) == -1 &&
            range.compareBoundaryPoints(Range.START_TO_END, nodeRange) == 1;
    }
}

function getSelectedElementTags(win) {
    var range, sel, elmlist, treeWalker, containerElement;
    sel = win.getSelection();
    if (sel.rangeCount > 0) {
        range = sel.getRangeAt(0);
    }

    if (range) {
        containerElement = range.commonAncestorContainer;
        if (containerElement.nodeType != 1) {
            containerElement = containerElement.parentNode;
        }

        treeWalker = win.document.createTreeWalker(
            containerElement,
            NodeFilter.SHOW_ELEMENT,
            function (node) {
                return rangeIntersectsNode(range, node) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_REJECT;
            },
            false
        );

        elmlist = [treeWalker.currentNode];
        while (treeWalker.nextNode()) {
            elmlist.push(treeWalker.currentNode);
            let currentNode = treeWalker.currentNode;
            while (currentNode && currentNode.tagName === 'SPAN') {
                currentNode = currentNode.parentNode;
            }
            if (!currentNode)
                continue;
            elmlist.push(currentNode);
            while (currentNode && currentNode.tagName === 'TD') {
                currentNode = currentNode.parentNode;
            }
            if (!currentNode)
                continue;
            elmlist.push(currentNode);
        }

        return [...new Set(elmlist)];
    }

    return [];
}

function createBaseContextMenu() {
    let menu = document.createElement('div');
    persistElement(menu);
    menu.classList.add('context-menu');
    menu.id = 'context-menu';

    function addMenuItem(name, action) {
        let item = document.createElement('div');
        item.classList.add('context-menu-item');
        item.innerHTML = name;
        item.onclick = action;
        menu.appendChild(item);
        return item;
    }

    function addToggleMenuItem(name, action, initialValue = false) {
        let item = document.createElement('div');
        let toggle = createRoundedToggleSwitch(name, initialValue, action);
        item.classList.add('context-menu-item');
        item.appendChild(toggle);
        menu.appendChild(item);
        return item;
    }

    function addSettingItem(name, setting) {
        return addToggleMenuItem(name, async () => {
            GLOBAL_SETTINGS[setting] = !GLOBAL_SETTINGS[setting];
            console.log("Setting item triggered: " + GLOBAL_SETTINGS[setting]);
            onHashChange();
        }, GLOBAL_SETTINGS[setting]);
    }

    let title = document.createElement('div');
    title.classList.add('context-menu-title');
    title.innerHTML = 'Context Menu';
    menu.appendChild(title);

    menu.appendChild(document.createElement('hr'));
    if (!window.getSelection().isCollapsed) {
        addMenuItem('Copy', () => {
            navigator.clipboard.writeText(window.getSelection().toString()).then(r => {
                console.log('Finished Copying to clipboard');
            });
        });
        // Find all selected entries with a type attribute
        let selectedElementTags = getSelectedElementTags(window);
        let tableEntries = selectedElementTags.filter((node) => {
            return node.hasAttribute('row-type');
        });
        if (tableEntries.length !== 0) {
            // First collect all the different types of entries
            let classes = tableEntries.filter((node) => {
                return node.getAttribute('row-type') === 'class';
            }).map((node) => {
                return getClass(node.getAttribute('type'));
            });
            let methods = tableEntries.filter((node) => {
                return node.getAttribute('row-type') === 'method' && MODIFIER.isStatic(node.getAttribute('mod')) && node.hasAttribute('current-class');
            }).map((node) => {
                return getClass(node.getAttribute('current-class')).methods()[node.getAttribute('dataIndex')];
            });
            let fields = tableEntries.filter((node) => {
                return node.getAttribute('row-type') === 'field' && MODIFIER.isStatic(node.getAttribute('mod')) && node.hasAttribute('current-class');
            }).map((node) => {
                return getClass(node.getAttribute('current-class')).fields()[node.getAttribute('dataIndex')];
            });
            let constructors = tableEntries.filter((node) => {
                return node.getAttribute('row-type') === 'constructor';
            }).map((node) => {
                return getConstructor(getClass(node.getAttribute('current-class')).constructors()[node.getAttribute('dataIndex')]);
            });
            let code = "";
            let constants = new Set();
            classes.forEach((node) => {
                constants.add(node.toKubeJSLoad());
            });
            methods.forEach((node) => {
                constants.add(getClass(node.getDeclaringClass()).toKubeJSLoad())
            });
            fields.forEach((node) => {
                constants.add(getClass(node.getDeclaringClass()).toKubeJSLoad())
            });

            constants.forEach((node) => {
                code += '\n' + node;
            });
            if (constructors.length !== 0) {
                code += '\n\n// Constructors';
                constructors.forEach((node) => {
                    code += '\n' + node.toKubeJSStaticCall();
                });
            }
            if (methods.length !== 0) {
                code += '\n\n// Methods';
                methods.forEach((node) => {
                    code += '\n' + node.toKubeJSStaticCall();
                });
            }

            if (fields.length !== 0) {
                code += '\n\n// Fields';
                fields.forEach((node) => {
                    code += '\n' + node.toKubeJSStaticReference();
                });
            }

            if (code.length !== 0) {
                addMenuItem('Copy Selected Code' + (classes.length > 1 ? 'es' : ''), () => {
                    navigator.clipboard.writeText(code).then(r => {
                        console.log('Finished Copying Code to clipboard...');
                    });

                    menu.remove();
                });
                menu.appendChild(document.createElement('hr'));
            }
        }
    }


    addMenuItem('Back', () => {
        history.back();
    });
    addMenuItem('Forward', () => {
        history.forward();
    });
    addMenuItem('Reload', async () => {
        console.log("Reload Triggered...");
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
        if (menu) {
            menu.remove();
        }

        menu = createBaseContextMenu();
        // Set the position of the menu
        // then show it
        menu.style.top = event.clientY + 'px';
        menu.style.left = event.clientX + 'px';
        menu.style.display = 'block';
    } else {
        let menu = document.getElementById('context-menu');
        if (menu) {
            menu.remove();
        }
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