function handleStickyElements() {
	const stickyElements = Array.from(document.querySelectorAll('.stick-able'));
	const stickyElementPositions = stickyElements.map(element => ({
		element,
		top: 0
	}));

	function updateStickyElementPositions() {
		stickyElementPositions.forEach(stickyElement => {
			const rect = stickyElement.element.getBoundingClientRect();
			stickyElement.top = rect.top + window.pageYOffset;
		});
	}

	function handleScroll() {
		const scrollPosition = window.pageYOffset;

		let currentStickyElement = null;
		for (let i = stickyElementPositions.length - 1; i >= 0; i--) {
			const stickyElement = stickyElementPositions[i];
			if (scrollPosition + stickyElement.element.offsetHeight >= stickyElement.top) {
				currentStickyElement = stickyElement.element;
				break;
			}
		}

		stickyElements.forEach(element => element.classList.remove('sticky'));
		if (currentStickyElement) {
			currentStickyElement.classList.add('sticky');
		}
	}

	window.addEventListener('scroll', handleScroll);
	window.addEventListener('resize', updateStickyElementPositions);

	updateStickyElementPositions();
	handleScroll();
}

function addLinkIcons() {
	// Get all elements with an id, excluding tables and divs
	const elementsWithIds = document.querySelectorAll('[id]:not(table):not(div)');

	// Iterate over each element
	elementsWithIds.forEach(element => {
		// Create linkIcon
		let url = DecodeURL();
		url.params.set("focus", element.id);
		const linkIcon = copyLinkToClipboard(url.href(), element.id);
		linkIcon.style.marginRight = '5px'; // Adjust the spacing between the icon and the element

		// Check if the element is a table row
		if (element.tagName.toLowerCase() === 'tr') {
			// Create a new table cell
			const td = document.createElement('td');
			td.style.display = 'flex';
			td.style.alignItems = 'center';

			// Clone the existing content of the table row
			const rowContent = Array.from(element.children);

			// Clear the existing content of the table row
			while (element.lastChild) {
				element.removeChild(element.lastChild);
			}

			// Append the link icon and the cloned content to the table cell
			td.appendChild(linkIcon);

			// Append the table cell to the table row
			element.appendChild(td);
			rowContent.forEach(child => element.appendChild(child.cloneNode(true)));
		} else {
			// For other elements, create a new div
			const div = document.createElement('div');
			div.style.display = 'flex';
			div.style.alignItems = 'center';

			// Create the unicode link icon (you can replace the icon with your desired unicode character)
			linkIcon.style.marginRight = '5px'; // Adjust the spacing between the icon and the element

			// Clone the element and append it to the new div
			const elementClone = element.cloneNode(true);
			div.appendChild(linkIcon);
			div.appendChild(elementClone);

			// Replace the original element with the new div
			element.parentNode.replaceChild(div, element);
		}
	});
}