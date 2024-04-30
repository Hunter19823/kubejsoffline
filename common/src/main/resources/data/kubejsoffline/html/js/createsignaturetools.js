function changeURL(url) {
    console.log("Changing URL to: " + url);
    history.pushState("", document.title, window.location.pathname + url);
    onHashChange();
}

function changeURLFromElement(element) {
    changeURL(element.getAttribute('href'));
}

function createLink(element, id, rawId = null, focus = null) {
    element.classList.add('link');
    let redirect = id;
    if (rawId) {
        redirect = rawId;
    }
    redirect = getClass(redirect).fullyQualifiedName();

    if (!redirect.match(/([a-z][a-z_0-9]*\.)+[A-Z_]($[A-Z_]|[\w_])*/)) {
        if (rawId) {
            redirect = rawId;
        } else {
            redirect = id;
        }
    }
    if (focus) {
        redirect += `?focus=${focus}`;
    }
    element.setAttribute('href', `#${redirect}`);
    element.setAttribute('onclick', 'changeURLFromElement(this);');

    return element;
}

function createShortLink(id, typeVariableMap = {}) {
    const target = getClass(id);
    const shortSignature = createLinkableSignature(id, typeVariableMap, false, false);
    if (target.isRawClass()) {
        const typeVariables = target.getTypeVariables();
        if (typeVariables.length === 0) {
            return shortSignature;
        }
        shortSignature.append(
                tagJoiner(
                        typeVariables,
                        ", ",
                        (actualType) => createLinkableSignature(
                                actualType,
                                typeVariableMap,
                                false,
                                false
                        ),
                        span("<"),
                        span(">")
                )
        )
    }
    return shortSignature;
}

function createFullSignature(id, typeVariableMap = {}) {
    const target = getClass(id);
    const fullSignature = createLinkableSignature(id, typeVariableMap, false, true);
    if (target.isRawClass()) {
        const typeVariables = target.getTypeVariables();
        if (typeVariables.length === 0) {
            return fullSignature;
        }
        fullSignature.append(
                tagJoiner(
                        typeVariables,
                        ", ",
                        (actualType) => createLinkableSignature(
                                actualType,
                                typeVariableMap,
                                false,
                                true
                        ),
                        span("<"),
                        span(">")
                )
        )
    }
    return fullSignature;
}

function createMethodSignature(method, typeVariableMap = {}) {
    let out = document.createElement('span');
    let parameters = method.parameters();
    let param = null;
    let name = span(method.name());
    appendAnnotationToolTip(name, method.annotations(), typeVariableMap);
    out.append(span(MODIFIER.toString(method.modifiers()) + " "));
    out.append(createShortLink(method.type(), typeVariableMap));
    out.append(' ');
    out.append(name);
    out.append('(');
    for (let i = 0; i < parameters.length; i++) {
        param = parameters[i];
        out.appendChild(createShortLink(param.type(), typeVariableMap));
        name = span(param.name());
        appendAnnotationToolTip(name, param.annotations(), typeVariableMap);
        out.append(' ');
        out.append(name);
        if (i < parameters.length - 1) {
            out.append(', ');
        }
    }
    out.append(')');
    return out;
}

/**
 * This function creates a html element representing a field.
 * @param {Field} field Created from the Field class
 * @param {TypeVariableMap} typeVariableMap
 * @returns {HTMLSpanElement}
 */
function createFieldSignature(field, typeVariableMap = {}) {
    let out = document.createElement('span');
    let name = span(field.name());
    appendAnnotationToolTip(name, field.annotations(), typeVariableMap);
    out.append(span(MODIFIER.toString(field.modifiers()) + " "));
    out.append(createShortLink(field.type(), typeVariableMap));
    out.append(' ');
    out.append(name);
    return out;
}

function createConstructorSignature(constructor, classID, typeVariableMap = {}) {
    let class_type = getClass(classID);
    let out = document.createElement('span');
    let parameters = constructor.parameters();
    let param = null;
    let name = null;
    out.append(span(MODIFIER.toString(constructor.modifiers()) + " "));
    out.append(createShortLink(class_type.id(), typeVariableMap));
    out.append('(');
    for (let i = 0; i < parameters.length; i++) {
        param = parameters[i];
        out.appendChild(createShortLink(param.type(), typeVariableMap));
        name = span(param.name());
        appendAnnotationToolTip(name, param.annotations(), typeVariableMap);
        out.append(' ');
        out.append(name);
        if (i < parameters.length - 1) {
            out.append(', ');
        }
    }
    out.append(')');
    return out;
}

/**
 * This function creates a html element representing an annotation.
 *
 * @param {Annotation} annotation
 * @param {TypeVariableMap} typeVariableMap
 * @returns {HTMLSpanElement}
 */
function createAnnotationSignature(annotation, typeVariableMap = {}) {
    let out = document.createElement('span');
    let type = getClass(annotation.type());
    let annotation_string = `@${type.fullyQualifiedName(typeVariableMap)}(${annotation.string()})`;
    out.append(br());
    out.append(annotation_string);
    return out;
}

function appendAttributesToClassTableRow(row, class_id) {
    let clazz = getClass(class_id);
    row.setAttribute('mod', clazz.modifiers());
    row.setAttribute('name', clazz.referenceName());
    row.setAttribute('type', class_id);
    row.setAttribute('row-type', 'class');
    row.id = clazz.id();
    // Add a link td to the row
    addLinkToTableRow(row, class_id);
    // row.setAttribute('declared-in', clazz);
}

function appendAttributesToMethodTableRow(row, class_id, method, current_class_id = null) {
    row.setAttribute('mod', method.modifiers());
    row.setAttribute('name', method.name());
    row.setAttribute('type', method.type());
    row.setAttribute('declared-in', class_id);
    row.setAttribute('parameters', method.parameters().length);
    row.setAttribute('row-type', 'method');
    row.setAttribute('dataIndex', method.dataIndex());
    if (current_class_id) {
        row.setAttribute('current-class', current_class_id);
    }

    row.id = method.id();
    addLinkToTableRow(row, method.id());
}

function appendAttributesToFieldTableRow(row, class_id, field, current_class_id = null) {
    row.setAttribute('mod', field.modifiers());
    row.setAttribute('name', field.name());
    row.setAttribute('type', field.type());
    row.setAttribute('declared-in', class_id);
    row.setAttribute('row-type', 'field');
    row.setAttribute('dataIndex', field.dataIndex());
    if (current_class_id) {
        row.setAttribute('current-class', current_class_id);
    }
    row.id = field.id();
    addLinkToTableRow(row, field.id());
}

function appendAttributesToConstructorTableRow(row, class_id, constructor, current_class_id = null) {
    row.setAttribute('mod', constructor.modifiers());
    row.setAttribute('parameters', constructor.parameters().length);
    row.setAttribute('declared-in', class_id);
    row.setAttribute('row-type', 'constructor');
    row.setAttribute('dataIndex', constructor.dataIndex());
    if (current_class_id) {
        row.setAttribute('current-class', current_class_id);
    }
    row.id = constructor.id();
    addLinkToTableRow(row, constructor.id());
}

function appendAttributesToRelationshipToTableRow(row, class_id, relationshipName, current_class_id = null) {
    const clazz = getClass(class_id);
    row.setAttribute('type', class_id);
    row.setAttribute('mod', clazz.modifiers());
    row.setAttribute('name', clazz.referenceName());
    row.setAttribute('row-type', 'relationship');

    if (current_class_id) {
        row.setAttribute('current-class', current_class_id);
    }
    row.id = clazz.id();
    addLinkToTableRow(row, class_id);
}

function handleClickLink(element) {
    LINK_MAP[element.id]();
}

function createLinkSpan(action) {
    let clipboard = span('\u{1F517}');
    clipboard.setAttribute('class', 'clickable');
    clipboard.setAttribute('title', 'Copy Link to clipboard');
    clipboard.setAttribute('onclick', 'handleClickLink(this)');
    // Assign a random ID to the span
    clipboard.id = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);

    // Add the action to the map
    LINK_MAP[clipboard.id] = () => {
        clipboard = document.getElementById(clipboard.id);
        action();
        // Change the innerHTML to a checkmark
        clipboard.innerText = '\u{2714}';
        // Wait 2 seconds
        setTimeout(() => {
            // Change the innerHTML back to a clipboard
            clipboard.innerText = '\u{1F517}';
        }, 2000);
    };
    return clipboard;
}

function copyLinkToClipboard(link, currentElementID = null) {
    return createLinkSpan(() => {
        navigator.clipboard.writeText(link).then(r => console.log("Successfully Copied link to clipboard"));
        if (currentElementID) {
            console.log("Focusing link element: " + currentElementID);
            focusElement(currentElementID);
        }
    });
}


function addClassToTable(table, class_id) {
    let clazz = getClass(class_id);
    let row = addRow(table, span(class_id), createShortLink(class_id), span(clazz.package()), createFullSignature(class_id));
    appendAttributesToClassTableRow(row, class_id);
}

function addMethodToTable(table, classID, method, current_class_id = null) {
    let row = addRow(table, href(span(classID), `#${getClass(classID).fullyQualifiedName()}`), createMethodSignature(method), createFullSignature(classID));
    appendAttributesToMethodTableRow(row, classID, method, current_class_id);
}

function addFieldToTable(table, class_id, field, current_class_id = null) {
    let row = addRow(table, href(span(class_id), `#${getClass(class_id).fullyQualifiedName()}`), createFieldSignature(field), createFullSignature(class_id));
    appendAttributesToFieldTableRow(row, class_id, field, current_class_id);
}

function tagJoiner(values, separator, transformer = (a) => span(a), prefix, suffix) {
    if (!exists(transformer)) {
        transformer = (a) => span(a);
    }
    const output = span();
    if (prefix) {
        output.append(prefix);
    }
    for (let i = 0; i < values.length; i++) {
        output.append(transformer(values[i]));
        // If not the last element, add the separator
        if (i < values.length - 1) {
            output.append(span(separator));
        }
    }
    if (suffix) {
        output.append(suffix);
    }
    return output;

}

function createLinkableSignature(type, typeVariableMap, isDefiningTypeVariable, appendPackageName, overrideID) {
    type = getClass(type);
    const outputSpan = document.createElement('span');
    if (type.isTypeVariable()) {
        if (isDefiningTypeVariable) {
            outputSpan.append(createLink(span(type.name()), type.id()));
            return outputSpan;
        }
        type = exists(typeVariableMap[type.id()]) ? getClass(typeVariableMap[type.id()]) : type;
    }
    if (type.isRawClass()) {
        const name = decompressString(type.data[PROPERTY.CLASS_NAME])
        if (exists(type.getDeclaringClass())) {
            outputSpan.append(createLinkableSignature(type.getDeclaringClass(), typeVariableMap, isDefiningTypeVariable, appendPackageName));
            outputSpan.append(span('$'));
            appendPackageName = false;
        }
        if (appendPackageName && type.package() && typeof type.package() === 'string' && type.package().length > 0) {
            outputSpan.append(span(type.package()));
            outputSpan.append(span('.'));
            if (exists(overrideID)) {
                outputSpan.append(createLink(span(name), overrideID));
            } else {
                outputSpan.append(createLink(span(name), type.id()));
            }
            return outputSpan;
        } else {
            if (exists(overrideID)) {
                outputSpan.append(createLink(span(name), overrideID));
            } else {
                outputSpan.append(createLink(span(name), type.id()));
            }
            return outputSpan;
        }
    }
    if (type.isTypeVariable()) {
        const typeVariableName = decompressString(type.data[PROPERTY.TYPE_VARIABLE_NAME]);
        if (isDefiningTypeVariable) {
            outputSpan.append(createLink(span(typeVariableName), type.id()));
            return outputSpan;
        }
        const bounds = type.getTypeVariableBounds();
        if (bounds.length === 0) {
            outputSpan.append(createLink(span(typeVariableName), type.id()));
            return outputSpan;
        }
        outputSpan.append(createLink(span(typeVariableName), type.id()));
        outputSpan.append(
                tagJoiner(
                        bounds,
                        " & ",
                        (bound) => createLinkableSignature(
                                bound,
                                typeVariableMap,
                                true,
                                appendPackageName
                        ),
                        span(" extends ")
                )
        );
        return outputSpan;
    }
    if (type.isWildcard()) {
        const name = "?";
        outputSpan.append(span(name));
        const lowerBounds = type.getLowerBound();
        if (lowerBounds.length !== 0) {
            outputSpan.append(
                    tagJoiner(
                            lowerBounds,
                            " & ",
                            (bound) => createLinkableSignature(
                                    bound,
                                    typeVariableMap,
                                    true,
                                    appendPackageName
                            ),
                            span(" super ")
                    )
            );
            return outputSpan;
        }
        const upperBounds = type.getUpperBound();
        if (upperBounds.length !== 0) {
            outputSpan.append(
                    tagJoiner(
                            upperBounds,
                            " & ",
                            (bound) => createLinkableSignature(
                                    bound,
                                    typeVariableMap,
                                    true,
                                    appendPackageName
                            ),
                            span(" extends ")
                    )
            );
            return outputSpan;
        }
        return outputSpan;
    }
    if (type.isParameterizedType()) {
        const rawTypeName = createLinkableSignature(type.getRawType(), typeVariableMap, isDefiningTypeVariable, appendPackageName && !(type.package().length > 0) && !exists(type.getOwnerType()), type.id());
        const ownerType = type.getOwnerType();
        if (exists(ownerType)) {
            const ownerPrefix = createLinkableSignature(ownerType, typeVariableMap, isDefiningTypeVariable, appendPackageName);
            outputSpan.append(ownerPrefix);
            outputSpan.append(span('$'));
            appendPackageName = false;
        }
        outputSpan.append(rawTypeName);
        const actualTypes = type.getTypeVariables();
        if (actualTypes.length === 0) {
            return outputSpan;
        }
        outputSpan.append(
                tagJoiner(
                        actualTypes,
                        ", ",
                        (actualType) => createLinkableSignature(
                                actualType,
                                typeVariableMap,
                                isDefiningTypeVariable,
                                appendPackageName
                        ),
                        span("<"),
                        span(">")
                )
        );
        return outputSpan;
    }

    console.error("Unknown Type! Cannot get generic definition for: ", type.id(), type.data);
    return span("Unknown Type");


}


