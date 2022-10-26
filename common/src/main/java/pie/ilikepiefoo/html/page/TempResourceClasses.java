package pie.ilikepiefoo.html.page;

public class TempResourceClasses {

	public static final String STYLE = "#top-bar {\n" +
			"    background-color: yellow;\n" +
			"    position: absolute;\n" +
			"    float: left;\n" +
			"    z-index: 4;\n" +
			"    width: 100%;\n" +
			"    height: 5em;\n" +
			"    top: 0;\n" +
			"    left: 0;\n" +
			"}\n" +
			"\n" +
			"#top-navigation-bar {\n" +
			"    z-index: 2;\n" +
			"    background-color: darkorange;\n" +
			"    position: absolute;\n" +
			"    width: 100%;\n" +
			"    height: 5em;\n" +
			"    top: 5em;\n" +
			"    font-size: 1em;\n" +
			"}\n" +
			"\n" +
			"#project-navigation {\n" +
			"    z-index: 1;\n" +
			"    background-color: cyan;\n" +
			"    position: absolute;\n" +
			"    overflow-x: scroll;\n" +
			"    overflow-y: scroll;\n" +
			"    width: 30em;\n" +
			"    height: calc(100% - 10em);\n" +
			"    top: 10em;\n" +
			"    resize: horizontal;\n" +
			"}\n" +
			"\n" +
			"#content-window {\n" +
			"    background-color: firebrick;\n" +
			"    position: absolute;\n" +
			"    width: calc(100% - 30em);\n" +
			"    height: calc(100% - 10em);\n" +
			"    top: 10em;\n" +
			"    left: 30em;\n" +
			"    resize: horizontal;\n" +
			"}\n" +
			"\n" +
			"#content-page {\n" +
			"    width: 100%;\n" +
			"    height: 100%;\n" +
			"}\n" +
			"\n" +
			"#logo-img {\n" +
			"    position: absolute;\n" +
			"    top: 0px;\n" +
			"    bottom: 0px;\n" +
			"}\n" +
			"\n" +
			"#page-name {\n" +
			"    position: absolute;\n" +
			"    left: 50vw;\n" +
			"    right: 50vw;\n" +
			"    line-height: 1.5;\n" +
			"    text-align: center;\n" +
			"    vertical-align: middle;\n" +
			"}\n" +
			"\n" +
			"img {\n" +
			"    max-height: 100%;\n" +
			"    max-width: 100%;\n" +
			"}\n" +
			"\n" +
			"div {\n" +
			"    display: block;\n" +
			"}\n" +
			"\n" +
			"body {\n" +
			"    margin: 0;\n" +
			"    overflow: hidden;\n" +
			"}\n" +
			"\n" +
			"html {\n" +
			"    overflow-y: overlay;\n" +
			"}\n" +
			"\n" +
			".sidenav {\n" +
			"    height: 100%;\n" +
			"    width: 200px;\n" +
			"}\n" +
			"\n" +
			".sidenav {\n" +
			"    padding: 6px 8px 6px 16px;\n" +
			"    text-decoration: none;\n" +
			"    display: block;\n" +
			"    border: none;\n" +
			"    text-align: left;\n" +
			"    cursor: pointer;\n" +
			"    outline: none;\n" +
			"    font-size: 20px;\n" +
			"}\n" +
			"\n" +
			".sidenav .sidenav-option:hover, .dropdown-btn:hover {\n" +
			"    color: #f1f1f1;\n" +
			"    backdrop-filter: invert();\n" +
			"}\n" +
			"\n" +
			"ul[role=\"tree\"] {\n" +
			"    margin: 0;\n" +
			"    padding: 0;\n" +
			"    list-style: none;\n" +
			"    font-size: 120%;\n" +
			"}\n" +
			"\n" +
			"ul[role=\"tree\"] li {\n" +
			"    margin: 0;\n" +
			"    padding: 0;\n" +
			"    list-style: none;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-expanded=\"false\"] + [role=\"group\"] {\n" +
			"    display: none;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-expanded=\"true\"] + [role=\"group\"] {\n" +
			"    display: block;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"].doc::before {\n" +
			"    content: \"©\";\n" +
			"    display: inline-block;\n" +
			"    padding-right: 2px;\n" +
			"    padding-left: 5px;\n" +
			"    vertical-align: middle;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-expanded=\"false\"] > ul {\n" +
			"    display: none;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-expanded=\"true\"] > ul {\n" +
			"    display: block;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-expanded=\"false\"] > span::before {\n" +
			"    content: \"\uD83D\uDCC1\";\n" +
			"    display: inline-block;\n" +
			"    padding-right: 3px;\n" +
			"    vertical-align: middle;\n" +
			"    font-weight: 900;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-expanded=\"true\"] > span::before {\n" +
			"    content: \"\uD83D\uDCC2\";\n" +
			"    display: inline-block;\n" +
			"    padding-right: 3px;\n" +
			"    vertical-align: middle;\n" +
			"    font-weight: 900;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"],\n" +
			"[role=\"ti\"] span {\n" +
			"    width: 9em;\n" +
			"    margin: 0;\n" +
			"    padding: 0.125em;\n" +
			"    display: block;\n" +
			"}\n" +
			"\n" +
			"/* disable default keyboard focus styling for tis\n" +
			"   Keyboard focus is styled with the following CSS */\n" +
			"[role=\"ti\"]:focus {\n" +
			"    outline: 0;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"][aria-selected=\"true\"] {\n" +
			"    padding-left: 4px;\n" +
			"    border-left: 5px solid #005a9c;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"].focus,\n" +
			"[role=\"ti\"] span.focus {\n" +
			"    border-color: black;\n" +
			"    background-color: #eee;\n" +
			"}\n" +
			"\n" +
			"[role=\"ti\"].hover,\n" +
			"[role=\"ti\"] span:hover {\n" +
			"    padding-left: 4px;\n" +
			"    background-color: #ddd;\n" +
			"    border-left: 5px solid #333;\n" +
			"}\n" +
			"\n" +
			"\n" +
			"\n" +
			".hide {\n" +
			"    display: none;\n" +
			"}";
	public static final String SCRIPT = "/*\n" +
			" *   This content is licensed according to the W3C Software License at\n" +
			" *   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document\n" +
			" *\n" +
			" *   File:   Tree.js\n" +
			" *\n" +
			" *   Desc:   Tree widget that implements ARIA Authoring Practices\n" +
			" *           for a tree being used as a file viewer\n" +
			" */\n" +
			"\n" +
			"/* global Treeitem */\n" +
			"\n" +
			"'use strict';\n" +
			"\n" +
			"/**\n" +
			" * ARIA Treeview example\n" +
			" *\n" +
			" * @function onload\n" +
			" * @description  after page has loaded initialize all treeitems based on the role=treeitem\n" +
			" */\n" +
			"\n" +
			"window.addEventListener('load', function () {\n" +
			"\tvar trees = document.querySelectorAll('[role=\"tree\"]');\n" +
			"\n" +
			"\tfor (var i = 0; i < trees.length; i++) {\n" +
			"\t\tvar t = new Tree(trees[i]);\n" +
			"\t\tt.init();\n" +
			"\t}\n" +
			"});\n" +
			"\n" +
			"/*\n" +
			" *   @constructor\n" +
			" *\n" +
			" *   @desc\n" +
			" *       Tree item object for representing the state and user interactions for a\n" +
			" *       tree widget\n" +
			" *\n" +
			" *   @param node\n" +
			" *       An element with the role=tree attribute\n" +
			" */\n" +
			"\n" +
			"var Tree = function (node) {\n" +
			"\t// Check whether node is a DOM element\n" +
			"\tif (typeof node !== 'object') {\n" +
			"\t\treturn;\n" +
			"\t}\n" +
			"\n" +
			"\tthis.domNode = node;\n" +
			"\n" +
			"\tthis.treeitems = [];\n" +
			"\tthis.firstChars = [];\n" +
			"\n" +
			"\tthis.firstTreeitem = null;\n" +
			"\tthis.lastTreeitem = null;\n" +
			"\tthis.selectedItem = null;\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.init = function () {\n" +
			"\tfunction findTreeitems(node, tree, group) {\n" +
			"\t\tvar elem = node.firstElementChild;\n" +
			"\t\tvar ti = group;\n" +
			"\n" +
			"\t\twhile (elem) {\n" +
			"\t\t\tif (elem.tagName.toLowerCase() === 'li') {\n" +
			"\t\t\t\tti = new Treeitem(elem, tree, group);\n" +
			"\t\t\t\tti.init();\n" +
			"\t\t\t\ttree.treeitems.push(ti);\n" +
			"\t\t\t\ttree.firstChars.push(ti.label.substring(0, 1).toLowerCase());\n" +
			"\t\t\t}\n" +
			"\n" +
			"\t\t\tif (elem.firstElementChild) {\n" +
			"\t\t\t\tfindTreeitems(elem, tree, ti);\n" +
			"\t\t\t}\n" +
			"\n" +
			"\t\t\telem = elem.nextElementSibling;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\n" +
			"\t// initialize pop up menus\n" +
			"\tif (!this.domNode.getAttribute('role')) {\n" +
			"\t\tthis.domNode.setAttribute('role', 'tree');\n" +
			"\t}\n" +
			"\n" +
			"\tfindTreeitems(this.domNode, this, false);\n" +
			"\n" +
			"\tthis.updateVisibleTreeitems();\n" +
			"\n" +
			"\tthis.firstTreeitem.domNode.tabIndex = 0;\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setSelectedToItem = function (treeitem) {\n" +
			"\tif (this.selectedItem) {\n" +
			"\t\tthis.selectedItem.domNode.setAttribute('aria-selected', 'false');\n" +
			"\t}\n" +
			"\ttreeitem.domNode.setAttribute('aria-selected', 'true');\n" +
			"\tthis.selectedItem = treeitem;\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusToItem = function (treeitem) {\n" +
			"\tfor (var i = 0; i < this.treeitems.length; i++) {\n" +
			"\t\tvar ti = this.treeitems[i];\n" +
			"\n" +
			"\t\tif (ti === treeitem) {\n" +
			"\t\t\tti.domNode.tabIndex = 0;\n" +
			"\t\t\tti.domNode.focus();\n" +
			"\t\t} else {\n" +
			"\t\t\tti.domNode.tabIndex = -1;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusToNextItem = function (currentItem) {\n" +
			"\tvar nextItem = false;\n" +
			"\n" +
			"\tfor (var i = this.treeitems.length - 1; i >= 0; i--) {\n" +
			"\t\tvar ti = this.treeitems[i];\n" +
			"\t\tif (ti === currentItem) {\n" +
			"\t\t\tbreak;\n" +
			"\t\t}\n" +
			"\t\tif (ti.isVisible) {\n" +
			"\t\t\tnextItem = ti;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\n" +
			"\tif (nextItem) {\n" +
			"\t\tthis.setFocusToItem(nextItem);\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusToPreviousItem = function (currentItem) {\n" +
			"\tvar prevItem = false;\n" +
			"\n" +
			"\tfor (var i = 0; i < this.treeitems.length; i++) {\n" +
			"\t\tvar ti = this.treeitems[i];\n" +
			"\t\tif (ti === currentItem) {\n" +
			"\t\t\tbreak;\n" +
			"\t\t}\n" +
			"\t\tif (ti.isVisible) {\n" +
			"\t\t\tprevItem = ti;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\n" +
			"\tif (prevItem) {\n" +
			"\t\tthis.setFocusToItem(prevItem);\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusToParentItem = function (currentItem) {\n" +
			"\tif (currentItem.groupTreeitem) {\n" +
			"\t\tthis.setFocusToItem(currentItem.groupTreeitem);\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusToFirstItem = function () {\n" +
			"\tthis.setFocusToItem(this.firstTreeitem);\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusToLastItem = function () {\n" +
			"\tthis.setFocusToItem(this.lastTreeitem);\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.expandTreeitem = function (currentItem) {\n" +
			"\tif (currentItem.isExpandable) {\n" +
			"\t\tcurrentItem.domNode.setAttribute('aria-expanded', true);\n" +
			"\t\tthis.updateVisibleTreeitems();\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.expandAllSiblingItems = function (currentItem) {\n" +
			"\tfor (var i = 0; i < this.treeitems.length; i++) {\n" +
			"\t\tvar ti = this.treeitems[i];\n" +
			"\n" +
			"\t\tif (ti.groupTreeitem === currentItem.groupTreeitem && ti.isExpandable) {\n" +
			"\t\t\tthis.expandTreeitem(ti);\n" +
			"\t\t}\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.collapseTreeitem = function (currentItem) {\n" +
			"\tvar groupTreeitem = false;\n" +
			"\n" +
			"\tif (currentItem.isExpanded()) {\n" +
			"\t\tgroupTreeitem = currentItem;\n" +
			"\t} else {\n" +
			"\t\tgroupTreeitem = currentItem.groupTreeitem;\n" +
			"\t}\n" +
			"\n" +
			"\tif (groupTreeitem) {\n" +
			"\t\tgroupTreeitem.domNode.setAttribute('aria-expanded', false);\n" +
			"\t\tthis.updateVisibleTreeitems();\n" +
			"\t\tthis.setFocusToItem(groupTreeitem);\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.updateVisibleTreeitems = function () {\n" +
			"\tthis.firstTreeitem = this.treeitems[0];\n" +
			"\n" +
			"\tfor (var i = 0; i < this.treeitems.length; i++) {\n" +
			"\t\tvar ti = this.treeitems[i];\n" +
			"\n" +
			"\t\tvar parent = ti.domNode.parentNode;\n" +
			"\n" +
			"\t\tti.isVisible = true;\n" +
			"\n" +
			"\t\twhile (parent && parent !== this.domNode) {\n" +
			"\t\t\tif (parent.getAttribute('aria-expanded') == 'false') {\n" +
			"\t\t\t\tti.isVisible = false;\n" +
			"\t\t\t}\n" +
			"\t\t\tparent = parent.parentNode;\n" +
			"\t\t}\n" +
			"\n" +
			"\t\tif (ti.isVisible) {\n" +
			"\t\t\tthis.lastTreeitem = ti;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.setFocusByFirstCharacter = function (currentItem, char) {\n" +
			"\tvar start, index;\n" +
			"\n" +
			"\tchar = char.toLowerCase();\n" +
			"\n" +
			"\t// Get start index for search based on position of currentItem\n" +
			"\tstart = this.treeitems.indexOf(currentItem) + 1;\n" +
			"\tif (start === this.treeitems.length) {\n" +
			"\t\tstart = 0;\n" +
			"\t}\n" +
			"\n" +
			"\t// Check remaining slots in the menu\n" +
			"\tindex = this.getIndexFirstChars(start, char);\n" +
			"\n" +
			"\t// If not found in remaining slots, check from beginning\n" +
			"\tif (index === -1) {\n" +
			"\t\tindex = this.getIndexFirstChars(0, char);\n" +
			"\t}\n" +
			"\n" +
			"\t// If match was found...\n" +
			"\tif (index > -1) {\n" +
			"\t\tthis.setFocusToItem(this.treeitems[index]);\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Tree.prototype.getIndexFirstChars = function (startIndex, char) {\n" +
			"\tfor (var i = startIndex; i < this.firstChars.length; i++) {\n" +
			"\t\tif (this.treeitems[i].isVisible) {\n" +
			"\t\t\tif (char === this.firstChars[i]) {\n" +
			"\t\t\t\treturn i;\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\treturn -1;\n" +
			"};\n" +
			"\n" +
			"\n" +
			"\n" +
			"/*\n" +
			"\t *   This content is licensed according to the W3C Software License at\n" +
			"\t *   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document\n" +
			"\t *\n" +
			"\t *   File:   treeitem.js\n" +
			"\t *\n" +
			"\t *   Desc:   Treeitem widget that implements ARIA Authoring Practices\n" +
			"\t *           for a tree being used as a file viewer\n" +
			"\t */\n" +
			"\n" +
			"'use strict';\n" +
			"\n" +
			"/*\n" +
			" *   @constructor\n" +
			" *\n" +
			" *   @desc\n" +
			" *       Treeitem object for representing the state and user interactions for a\n" +
			" *       treeItem widget\n" +
			" *\n" +
			" *   @param node\n" +
			" *       An element with the role=tree attribute\n" +
			" */\n" +
			"\n" +
			"var Treeitem = function (node, treeObj, group) {\n" +
			"\t// Check whether node is a DOM element\n" +
			"\tif (typeof node !== 'object') {\n" +
			"\t\treturn;\n" +
			"\t}\n" +
			"\n" +
			"\tnode.tabIndex = -1;\n" +
			"\tthis.tree = treeObj;\n" +
			"\tthis.groupTreeitem = group;\n" +
			"\tthis.domNode = node;\n" +
			"\tthis.label = node.textContent.trim();\n" +
			"\n" +
			"\tif (node.getAttribute('aria-label')) {\n" +
			"\t\tthis.label = node.getAttribute('aria-label').trim();\n" +
			"\t}\n" +
			"\n" +
			"\tthis.isExpandable = false;\n" +
			"\tthis.isVisible = false;\n" +
			"\tthis.inGroup = false;\n" +
			"\n" +
			"\tif (group) {\n" +
			"\t\tthis.inGroup = true;\n" +
			"\t}\n" +
			"\n" +
			"\tvar elem = node.firstElementChild;\n" +
			"\n" +
			"\twhile (elem) {\n" +
			"\t\tif (elem.tagName.toLowerCase() == 'ul') {\n" +
			"\t\t\telem.setAttribute('role', 'group');\n" +
			"\t\t\tthis.isExpandable = true;\n" +
			"\t\t\tbreak;\n" +
			"\t\t}\n" +
			"\n" +
			"\t\telem = elem.nextElementSibling;\n" +
			"\t}\n" +
			"\n" +
			"\tthis.keyCode = Object.freeze({\n" +
			"\t\tRETURN: 13,\n" +
			"\t\tSPACE: 32,\n" +
			"\t\tPAGEUP: 33,\n" +
			"\t\tPAGEDOWN: 34,\n" +
			"\t\tEND: 35,\n" +
			"\t\tHOME: 36,\n" +
			"\t\tLEFT: 37,\n" +
			"\t\tUP: 38,\n" +
			"\t\tRIGHT: 39,\n" +
			"\t\tDOWN: 40,\n" +
			"\t});\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.init = function () {\n" +
			"\tthis.domNode.tabIndex = -1;\n" +
			"\n" +
			"\tif (!this.domNode.getAttribute('role')) {\n" +
			"\t\tthis.domNode.setAttribute('role', 'ti');\n" +
			"\t}\n" +
			"\n" +
			"\tthis.domNode.addEventListener('keydown', this.handleKeydown.bind(this));\n" +
			"\tthis.domNode.addEventListener('click', this.handleClick.bind(this));\n" +
			"\tthis.domNode.addEventListener('focus', this.handleFocus.bind(this));\n" +
			"\tthis.domNode.addEventListener('blur', this.handleBlur.bind(this));\n" +
			"\n" +
			"\tif (!this.isExpandable) {\n" +
			"\t\tthis.domNode.addEventListener('mouseover', this.handleMouseOver.bind(this));\n" +
			"\t\tthis.domNode.addEventListener('mouseout', this.handleMouseOut.bind(this));\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.isExpanded = function () {\n" +
			"\tif (this.isExpandable) {\n" +
			"\t\treturn this.domNode.getAttribute('aria-expanded') === 'true';\n" +
			"\t}\n" +
			"\n" +
			"\treturn false;\n" +
			"};\n" +
			"\n" +
			"/* EVENT HANDLERS */\n" +
			"\n" +
			"Treeitem.prototype.handleKeydown = function (event) {\n" +
			"\tvar flag = false,\n" +
			"\t\t\tchar = event.key;\n" +
			"\n" +
			"\tfunction isPrintableCharacter(str) {\n" +
			"\t\treturn str.length === 1 && str.match(/\\S/);\n" +
			"\t}\n" +
			"\n" +
			"\tfunction printableCharacter(item) {\n" +
			"\t\tif (char == '*') {\n" +
			"\t\t\titem.tree.expandAllSiblingItems(item);\n" +
			"\t\t\tflag = true;\n" +
			"\t\t} else {\n" +
			"\t\t\tif (isPrintableCharacter(char)) {\n" +
			"\t\t\t\titem.tree.setFocusByFirstCharacter(item, char);\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\n" +
			"\tif (event.altKey || event.ctrlKey || event.metaKey) {\n" +
			"\t\treturn;\n" +
			"\t}\n" +
			"\n" +
			"\tif (event.shift) {\n" +
			"\t\tif (isPrintableCharacter(char)) {\n" +
			"\t\t\tprintableCharacter(this);\n" +
			"\t\t}\n" +
			"\t} else {\n" +
			"\t\tswitch (event.keyCode) {\n" +
			"\t\t\tcase this.keyCode.RETURN:\n" +
			"\t\t\tcase this.keyCode.SPACE:\n" +
			"\t\t\t\tvar treeitem = event.currentTarget;\n" +
			"\t\t\t\tvar label = treeitem.getAttribute('aria-label');\n" +
			"\t\t\t\tif (!label) {\n" +
			"\t\t\t\t\tvar child = treeitem.firstElementChild;\n" +
			"\t\t\t\t\tlabel = child ? child.innerText : treeitem.innerText;\n" +
			"\t\t\t\t}\n" +
			"\t\t\t\tdocument.getElementById('last_action').value = label.trim();\n" +
			"\n" +
			"\t\t\t\tif (!this.isExpandable) this.tree.setFocusToItem(this);\n" +
			"\t\t\t\tthis.tree.setSelectedToItem(this);\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tcase this.keyCode.UP:\n" +
			"\t\t\t\tthis.tree.setFocusToPreviousItem(this);\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tcase this.keyCode.DOWN:\n" +
			"\t\t\t\tthis.tree.setFocusToNextItem(this);\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tcase this.keyCode.RIGHT:\n" +
			"\t\t\t\tif (this.isExpandable) {\n" +
			"\t\t\t\t\tif (this.isExpanded()) {\n" +
			"\t\t\t\t\t\tthis.tree.setFocusToNextItem(this);\n" +
			"\t\t\t\t\t} else {\n" +
			"\t\t\t\t\t\tthis.tree.expandTreeitem(this);\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t}\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tcase this.keyCode.LEFT:\n" +
			"\t\t\t\tif (this.isExpandable && this.isExpanded()) {\n" +
			"\t\t\t\t\tthis.tree.collapseTreeitem(this);\n" +
			"\t\t\t\t\tflag = true;\n" +
			"\t\t\t\t} else {\n" +
			"\t\t\t\t\tif (this.inGroup) {\n" +
			"\t\t\t\t\t\tthis.tree.setFocusToParentItem(this);\n" +
			"\t\t\t\t\t\tflag = true;\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t}\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tcase this.keyCode.HOME:\n" +
			"\t\t\t\tthis.tree.setFocusToFirstItem();\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tcase this.keyCode.END:\n" +
			"\t\t\t\tthis.tree.setFocusToLastItem();\n" +
			"\t\t\t\tflag = true;\n" +
			"\t\t\t\tbreak;\n" +
			"\n" +
			"\t\t\tdefault:\n" +
			"\t\t\t\tif (isPrintableCharacter(char)) {\n" +
			"\t\t\t\t\tprintableCharacter(this);\n" +
			"\t\t\t\t}\n" +
			"\t\t\t\tbreak;\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\n" +
			"\tif (flag) {\n" +
			"\t\tevent.stopPropagation();\n" +
			"\t\tevent.preventDefault();\n" +
			"\t}\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.handleClick = function (event) {\n" +
			"\tif (this.isExpandable) {\n" +
			"\t\tif (this.isExpanded()) {\n" +
			"\t\t\tthis.tree.collapseTreeitem(this);\n" +
			"\t\t} else {\n" +
			"\t\t\tthis.tree.expandTreeitem(this);\n" +
			"\t\t}\n" +
			"\t\tevent.stopPropagation();\n" +
			"\t} else {\n" +
			"\t\tthis.tree.setFocusToItem(this);\n" +
			"\t}\n" +
			"\tthis.tree.setSelectedToItem(this);\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.handleFocus = function () {\n" +
			"\tvar node = this.domNode;\n" +
			"\tif (this.isExpandable) {\n" +
			"\t\tnode = node.firstElementChild;\n" +
			"\t}\n" +
			"\tnode.classList.add('focus');\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.handleBlur = function () {\n" +
			"\tvar node = this.domNode;\n" +
			"\tif (this.isExpandable) {\n" +
			"\t\tnode = node.firstElementChild;\n" +
			"\t}\n" +
			"\tnode.classList.remove('focus');\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.handleMouseOver = function (event) {\n" +
			"\tevent.currentTarget.classList.add('hover');\n" +
			"};\n" +
			"\n" +
			"Treeitem.prototype.handleMouseOut = function (event) {\n" +
			"\tevent.currentTarget.classList.remove('hover');\n" +
			"};\n" +
			"\n" +
			"\n" +
			"\n" +
			"/*\n" +
			"\t *   This content is licensed according to the W3C Software License at\n" +
			"\t *   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document\n" +
			"\t *\n" +
			"\t *   File:   treeitem.js\n" +
			"\t *\n" +
			"\t *   Desc:   Setup click events for Tree widget examples\n" +
			"\t */\n" +
			"\n" +
			"'use strict';\n" +
			"\n" +
			"/**\n" +
			" * ARIA Treeview example\n" +
			" *\n" +
			" * @function onload\n" +
			" * @description  after page has loaded initialize all treeitems based on the role=treeitem\n" +
			" */\n" +
			"\n" +
			"window.addEventListener('load', function () {\n" +
			"\tconst treeitems = document.querySelectorAll('[role=\"ti\"]');\n" +
			"\n" +
			"\tfor (let i = 0; i < treeitems.length; i++) {\n" +
			"\t\ttreeitems[i].addEventListener('click', function (event) {\n" +
			"\t\t\tlet treeitem = event.currentTarget;\n" +
			"\t\t\tlet label = treeitem.getAttribute('aria-label');\n" +
			"\t\t\tif (!label) {\n" +
			"\t\t\t\tlet child = treeitem.firstElementChild;\n" +
			"\t\t\t\tlabel = child ? child.innerText : treeitem.innerText;\n" +
			"\t\t\t}\n" +
			"\n" +
			"\n" +
			"\t\t\t//document.getElementById('last_action').value = label.trim();\n" +
			"\t\t\tdocument.getElementById('content-page').src=treeitem.dataset.src;\n" +
			"\t\t\tevent.stopPropagation();\n" +
			"\t\t\tevent.preventDefault();\n" +
			"\t\t});\n" +
			"\t}\n" +
			"});\n" +
			"\n" +
			"window.addEventListener('load', function () {\n" +
			"\tconst np = document.getElementById('project-navigation');\n" +
			"\tconst ct = document.getElementById('content-window');\n" +
			"\n" +
			"\tlet observer = new ResizeObserver((event) => {\n" +
			"\t\tfor (const entry of event.values()) {\n" +
			"\t\t\tct.style.width=`calc(100% - ${np.style.width})`;\n" +
			"\t\t\tct.style.left=`calc(${np.style.width})`;\n" +
			"\t\t}\n" +
			"\t});\n" +
			"\n" +
			"\tobserver.observe(np);\n" +
			"});\n" +
			"\n" +
			"function collapseAll() {\n" +
			"\tlet np = document.getElementById('project-navigation');\n" +
			"\tlet items = np.getElementsByTagName(\"li\");\n" +
			"\tfor (let i = 0; i < items.length; i++) {\n" +
			"\t\tif(items[i].hasAttribute(\"role\") && items[i].getAttribute(\"role\") === \"ti\") {\n" +
			"\t\t\titems[i].setAttribute(\"aria-expanded\", \"false\");\n" +
			"\t\t\titems[i].removeAttribute(\"aria-selected\");\n" +
			"\t\t\titems[i].classList.remove(\"hide\");\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}\n" +
			"\n" +
			"function liveSearch() {\n" +
			"\tcollapseAll();\n" +
			"\n" +
			"\tlet np = document.getElementById('project-navigation');\n" +
			"\tlet query = document.getElementById('searchbar').value.toLowerCase();\n" +
			"\tlet groups = np.getElementsByTagName('ul');\n" +
			"\tlet children = null;\n" +
			"\tlet matchingGroup = false;\n" +
			"\tlet parent = null;\n" +
			"\n" +
			"\tif(query.length !== 0) {\n" +
			"\t\tfor (let i = 0; i < groups.length; i++) {\n" +
			"\t\t\tchildren = groups[i].children;\n" +
			"\t\t\tmatchingGroup = false;\n" +
			"\t\t\tfor (let j = 0; j < children.length; j++) {\n" +
			"\t\t\t\tif(children[j].classList.contains(\"doc\")) {\n" +
			"\t\t\t\t\tif(children[j].innerText.toLowerCase().includes(query)) {\n" +
			"\t\t\t\t\t\t// Show Child, Show Group.\n" +
			"\t\t\t\t\t\tmatchingGroup = true;\n" +
			"\t\t\t\t\t\tchildren[j].classList.remove(\"hide\");\n" +
			"\t\t\t\t\t}else {\n" +
			"\t\t\t\t\t\tchildren[j].classList.add(\"hide\");\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t}else {\n" +
			"\t\t\t\t\tif(children[j].hasAttribute(\"aria-expanded\")) {\n" +
			"\t\t\t\t\t\tchildren[j].setAttribute(\"aria-expanded\", \"false\");\n" +
			"\t\t\t\t\t\tchildren[j].classList.add(\"hide\");\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t}\n" +
			"\t\t\t}\n" +
			"\t\t\tif(matchingGroup) {\n" +
			"\t\t\t\tgroups[i].setAttribute(\"aria-expanded\", \"true\");\n" +
			"\t\t\t\tgroups[i].classList.remove(\"hide\");\n" +
			"\t\t\t\tparent = groups[i].parentElement;\n" +
			"\t\t\t\twhile (parent) {\n" +
			"\t\t\t\t\tif(parent.getAttribute(\"aria-expanded\") === \"true\"){\n" +
			"\t\t\t\t\t\tparent = null;\n" +
			"\t\t\t\t\t} else if (parent.tagName === \"UL\") {\n" +
			"\t\t\t\t\t\tparent = parent.parentElement;\n" +
			"\t\t\t\t\t\tparent.classList.remove(\"hide\");\n" +
			"\t\t\t\t\t} else if (parent.hasAttribute(\"role\") && parent.getAttribute(\"role\") === \"ti\") {\n" +
			"\t\t\t\t\t\tparent.setAttribute(\"aria-expanded\", \"true\");\n" +
			"\t\t\t\t\t\tparent.classList.remove(\"hide\");\n" +
			"\t\t\t\t\t\tparent = parent.parentElement;\n" +
			"\t\t\t\t\t} else {\n" +
			"\t\t\t\t\t\tparent = null;\n" +
			"\t\t\t\t\t}\n" +
			"\t\t\t\t}\n" +
			"\t\t\t}else {\n" +
			"\t\t\t\tgroups[i].setAttribute(\"aria-expanded\", \"false\");\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"\n" +
			"\t// const np = document.getElementById('project-navigation');\n" +
			"\t// const items = np.getElementsByClassName('doc');\n" +
			"\t// const query = document.getElementById('searchbar').value.toLowerCase();\n" +
			"\t// if(query.length !== 0) {\n" +
			"\t// \tfor (let i = 0; i < items.length; i++) {\n" +
			"\t// \t\tif (items[i].innerText.toLowerCase()\n" +
			"\t// \t\t\t\t.includes(query)\n" +
			"\t// \t\t) {\n" +
			"\t// \t\t\titems[i].setAttribute(\"aria-selected\", \"true\");\n" +
			"\t// \t\t\tlet parent = items[i].parentElement;\n" +
			"\t// \t\t\tlet count = 0;\n" +
			"\t// \t\t\twhile (parent && count < 10) {\n" +
			"\t// \t\t\t\tif (parent.tagName === \"UL\") {\n" +
			"\t// \t\t\t\t\tparent = parent.parentElement;\n" +
			"\t// \t\t\t\t} else if (parent.hasAttribute(\"role\") && parent.getAttribute(\"role\") === \"ti\") {\n" +
			"\t// \t\t\t\t\tparent.setAttribute(\"aria-expanded\", \"true\");\n" +
			"\t// \t\t\t\t\tparent = parent.parentElement;\n" +
			"\t// \t\t\t\t} else {\n" +
			"\t// \t\t\t\t\tparent = null;\n" +
			"\t// \t\t\t\t}\n" +
			"\t// \t\t\t}\n" +
			"\t// \t\t} else {\n" +
			"\t// \t\t\titems[i].setAttribute(\"\n" +
			"\t// \t\t\t\", \"false\");\n" +
			"\t// \t\t}\n" +
			"\t// \t}\n" +
			"\t// }\n" +
			"}\n";


}
