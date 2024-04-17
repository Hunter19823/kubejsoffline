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

function findEventClasses() {
    if (DATA._eventsIndexed) {
        console.log("Events already indexed");
        return;
    }
    console.log("Indexing events");
    DATA._eventsIndexed = true;
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
        if (!exists(eventClass)) {
            console.debug("Failed to find class for ", keys[i])
            continue;
        }
        EVENTS[keys[i]].push(eventClass.id());
        EVENTS[keys[i]].push(...getRelation(RELATIONSHIP.INHERITS, eventClass.id()));
        EVENTS[keys[i]].push(...getRelation(RELATIONSHIP.TYPE_VARIABLE_OF, eventClass.id()));
        EVENTS[keys[i]].push(...getRelation(RELATIONSHIP.COMPONENT_OF, eventClass.id()));
    }
    DATA._events = EVENTS;
}

function createHomePage() {
    wipePage();
    const keys = Object.keys(DATA._events);

    let span = null;
    let table = null;
    for (let i = 0; i < keys.length; i++) {
        const key = keys[i];
        span = document.createElement('span');
        span.innerHTML = key;
        let period = key?.lastIndexOf('.');
        table = createTableWithHeaders(createSortableTable(period === -1 ? key : key.substring(period + 1)), 'Link', span);
        for (let j = 0; j < DATA._events[key].length; j++) {
            try {
                let row = addRow(table, createFullSignature(DATA._events[key][j]));
                appendAttributesToClassTableRow(row, DATA._events[key][j]);
            } catch (e) {
                console.error("Failed to create homepage entry for ", key, " Class: ", DATA._events[key][j], " Error: ", e);
            }
        }
    }
}
