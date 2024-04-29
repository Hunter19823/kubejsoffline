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

function addLinkToTableRow(tr, id) {
    const url = DecodeURL();
    url.params.set("focus", id);
    const linkIcon = copyLinkToClipboard(url.href(), tr.id);
    const td = document.createElement('td');
    td.classList.add('link-container');
    td.appendChild(linkIcon);
    tr.insertBefore(td, tr.firstChild);
}