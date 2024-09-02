function createPageHeader() {
    let header = document.createElement('div');
    let title = document.createElement('h1');
    let img = document.createElement('img');
    header.id = 'page-header';
    header.classList.add('header-div');
    header.classList.add('link');
    title.setAttribute('href', '#');
    img.setAttribute('href', '#');
    title.setAttribute('onclick', 'changeURLFromElement(this);');
    img.setAttribute('onclick', 'changeURLFromElement(this);');
    title.innerHTML = `KubeJS Offline v${PROJECT_INFO.mod_version} [${PROJECT_INFO.minecraft_version}]`;
    img.src = 'https://raw.githubusercontent.com/Hunter19823/kubejsoffline/master/kubejs_offline_logo.png';
    img.style.height = '7em';
    img.onerror = () => {
        img.style.display = 'none';
    };
    header.appendChild(img);
    header.appendChild(title);
    document.body.append(header);
    createSearchBar();
}

// Clear the page of all content
function wipePage() {
    let persist = document.body.getElementsByClassName('refresh-persistent');
    persist = Array.from(persist);
    document.body.innerHTML = '';
    createPageHeader();
    for (let child of persist) {
        document.body.append(child);
    }
}

function createHomePage() {
    wipePage();
    const keys = Object.keys(DATA._events);

    for (let i = 0; i < keys.length; i++) {
        const key = keys[i];
        const period = key.lastIndexOf('.');
        const title = period === -1 ? key : `${key.substring(period + 1)} (${key.substring(0, period)})`;
        if (DATA._events[key].length === 0)
            continue;
        createClassTable(title, key, DATA._events[key].filter(exists).map(getClass))
    }
}
