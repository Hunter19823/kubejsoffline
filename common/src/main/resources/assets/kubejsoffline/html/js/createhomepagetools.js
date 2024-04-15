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
    const EVENTS = {
        "dev.latvian.mods.kubejs.event.EventJS": [],
        "net.fabricmc.fabric.api.event.Event": [],
        "dev.architectury.event.Event": [],
        "dev.latvian.mods.kubejs.recipe.RecipeJS": [],
        "net.minecraftforge.eventbus.api.Event": []
    }

    const keys = Object.keys(EVENTS);
    for (let i = 0; i < keys.length; i++) {
        let eventClass = getClass(keys[i]);
        if (eventClass === null) {
            continue;
        }
        EVENTS[keys[i]].push(...eventClass.relation(RELATIONS.indexOf("COMPONENT_OF")));
        EVENTS[keys[i]].push(...eventClass.relation(RELATIONS.indexOf("IMPLEMENTATION_OF")));
        // EVENTS[keys[i]].push(...eventClass.relation(RELATIONS.indexOf("TYPE_VARIABLE_OF")));
    }

    let span = null;
    let table = null;
    for (let i = 0; i < keys.length; i++) {
        const key = keys[i];
        span = document.createElement('span');
        span.innerHTML = key;
        let period = key?.lastIndexOf('.');
        table = createTableWithHeaders(createSortableTable(period === -1 ? key : key.substring(period + 1)), 'Link', span);
        for (let j = 0; j < EVENTS[key].length; j++) {
            let row = addRow(table, createFullSignature(EVENTS[key][j]));
            appendAttributesToClassTableRow(row, EVENTS[key][j]);
        }
    }
}
