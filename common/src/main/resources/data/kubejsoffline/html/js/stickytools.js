function handleStickyElements() {
    let stickyElementPositions = Array.from(document.querySelectorAll('.stick-able')).map(element => ({
        element,
        top: element.getBoundingClientRect().top + window.scrollY,
        bottom: element.getBoundingClientRect().bottom + window.scrollY
    })).sort((a, b) => a.top - b.top);

    function updateStickyElementPositions() {
        stickyElementPositions = Array.from(document.querySelectorAll('.stick-able')).map(element => ({
            element,
            top: element.getBoundingClientRect().top + window.scrollY,
            bottom: element.getBoundingClientRect().bottom + window.scrollY
        })).sort((a, b) => a.top - b.top);
    }


    function handleScroll() {
        const scrollPosition = window.scrollY;

        let currentStickyElement = null;
        for (let i = stickyElementPositions.length - 1; i >= 0; i--) {
            const stickyElement = stickyElementPositions[i];
            if (scrollPosition + stickyElement.element.offsetHeight >= stickyElement.top) {
                if (exists(currentStickyElement)) {
                    // If the bottom of the previous sticky element is greater than the top of the current sticky element
                    // then the previous sticky element should be moved up by the difference
                    const difference = currentStickyElement.top - (currentStickyElement.element.offsetHeight + window.scrollY);
                    stickyElement.element.style.top = `${difference}px`;
                } else {
                    stickyElement.element.style.top = `0`;
                }
                currentStickyElement = stickyElement;
                currentStickyElement.element.classList.add('sticky');
            } else {
                stickyElement.element.classList.remove('sticky');
                stickyElement.element.style.top = '';
            }
        }

        if (currentStickyElement) {
            currentStickyElement.element.classList.add('sticky');
        }
    }

    GLOBAL_DATA['handleScroll'] = handleScroll;
    GLOBAL_DATA['handleResize'] = updateStickyElementPositions;

    updateStickyElementPositions();
    handleScroll();
}

function addLinkToTableRow(tr, id) {
    const url = CURRENT_URL.clone();
    url.params.set("focus", id);
    const linkIcon = copyLinkToClipboard(url.href(), tr.id);
    const td = document.createElement('td');
    td.classList.add('link-container');
    td.appendChild(linkIcon);
    tr.insertBefore(td, tr.firstChild);
}

function addLinkToElement(element, id) {
    const url = CURRENT_URL.clone();
    url.params.set("focus", id);
    const linkIcon = copyLinkToClipboard(url.href(), element.id);
    element.appendChild(linkIcon);
}