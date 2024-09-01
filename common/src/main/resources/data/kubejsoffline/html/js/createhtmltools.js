function addRow(table, ...data) {
    let tr = document.createElement('tr');
    let td = null;
    table.appendChild(tr);
    for (let i = 0; i < data.length; i++) {
        td = document.createElement('td');
        tr.appendChild(td);
        if (typeof data[i] === 'number') {
            td.appendChild(createFullSignature(data[i]));
        } else {
            td.appendChild(data[i]);
        }
    }
    return tr;
}

function div(...args) {
    let div = document.createElement('div');
    for (let arg of args) {
        if (typeof arg === 'string') {
            arg = span(arg);
        }
        div.appendChild(arg);
    }
    return div;
}

function span(text) {
    let span = document.createElement('span');
    if (!exists(text))
        return span;
    span.innerText = text;
    return span;
}

function header(text, level = 2) {
    let header = document.createElement('h' + level);
    header.innerText = text;
    return header;
}

function breakLine() {
    document.body.appendChild(br());
}

function br() {
    return document.createElement('br');
}

function option(text, action, group) {
    return {
        text: text,
        action: action,
        group: group
    };
}

function createOptions(...args) {
    let output = document.createElement('select');
    let option = null;
    let groupMap = new Map();
    let actionMap = new Map();
    for (let opt of args) {
        option = document.createElement('option');
        option.value = opt.text;
        option.innerText = opt.text;
        actionMap.set(opt.text, opt.action);
        if (opt.group && opt.group !== 'Misc') {
            if (!groupMap.has(opt.group)) {
                groupMap.set(opt.group, document.createElement('optgroup'));
                groupMap.get(opt.group).label = opt.group;
                output.appendChild(groupMap.get(opt.group));
            }
            groupMap.get(opt.group).appendChild(option);
        } else {
            if (!groupMap.has('Misc')) {
                groupMap.set('Misc', document.createElement('optgroup'));
                groupMap.get('Misc').label = 'Misc';
            }
            groupMap.get('Misc').appendChild(option);
        }
    }
    if (groupMap.has('Misc')) {
        output.appendChild(groupMap.get('Misc'));
    }
    output.onchange = () => {
        if (actionMap.has(output.value)) {
            actionMap.get(output.value)();
        } else {
            console.error('No action for ' + output.value);
        }
    }
    return output;
}


function li(content) {
    let tag = document.createElement('li');
    tag.innerText = content;
    return tag;
}

function href(element, url) {
    element.classList.add('link')
    element.setAttribute('href', url);
    element.setAttribute('onclick', 'handleClickLink(this)');
    element.onclick = () => {
        changeURL(url);
    };
    return element;
}


function appendAnnotationToolTip(tag, annotations, typeVariableMap = {}) {
    if (!annotations || annotations.length === 0)
        return tag;

    tag.classList.add('tooltip');
    let tooltip = document.createElement('div');
    tooltip.classList.add('tooltiptext');
    for (let annotation of annotations) {
        tooltip.appendChild(createAnnotationSignature(annotation, typeVariableMap));
    }
    tag.appendChild(tooltip);
    return tag;
}


function createRoundedToggleSwitch(name, initialValue, onChange) {
    let label = document.createElement('label');
    label.classList.add('switch');
    let input = document.createElement('input');
    input.type = 'checkbox';
    input.checked = initialValue;
    input.onchange = onChange;
    let span = document.createElement('span');
    span.classList.add('slider', 'round');
    label.appendChild(input);
    label.appendChild(span);
    let span2 = document.createElement('span');
    span2.innerText = name;
    label.appendChild(span2);
    return label;
}

function persistElement(element) {
    element.classList.add('refresh-persistent');
}