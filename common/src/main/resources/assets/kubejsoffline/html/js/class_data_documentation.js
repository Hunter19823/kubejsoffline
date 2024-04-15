/**
 * A fully qualified Java Type name
 * @typedef {string} FullTypeName
 *
 * @example Parameterized Type
 * java.util.List<java.lang.String>
 * @example Nested Parameterized Type
 * java.util.Map<java.lang.String, java.lang.String>
 * @example Nested Parameterized Type with Type Variable
 * java.util.Map<java.lang.String, T>
 * @example Nested Parameterized Type with Wildcard Type
 * java.util.Map<java.lang.String, ?>
 * @example Parameterized Type with Owner Type
 * java.util.Map<java.lang.String, java.lang.String>.Entry
 * @example Parameterized Type with Owner Type and Type Variable
 * java.util.Map<java.lang.String, java.lang.String>.Entry<K, V>
 * @example Parameterized Type with Type Variable definition
 * java.util.List<T extends java.lang.Number>
 * @example Raw Type
 * java.util.String
 * @example Primitive Type
 * int
 * @example Void Type
 * void
 * @example Type Variable without bounds
 * T
 * @example Type Variable with extends bounds
 * T extends java.lang.Number
 * @example Type Variable with multiple bounds
 * T extends java.lang.Number & java.lang.Comparable<T>
 * @example Type Variable with super bounds
 * T super java.lang.String
 * @example Array Type
 * java.lang.String[]
 * @example Wildcard Type without bounds
 * ?
 * @example Wildcard Type with extends bounds
 * ? extends java.lang.Number
 * @example Wildcard Type with super bounds
 * ? super java.lang.String
 */
/**
 * A Java Type name without package information
 * @typedef {string} TypeName
 * @example Parameterized Type
 * List<String>
 * @example Nested Parameterized Type
 * Map<String, String>
 * @example Nested Parameterized Type with Type Variable
 * Map<String, T>
 * @example Nested Parameterized Type with Wildcard Type
 * Map<String, ?>
 * @example Nested Parameterized Type with Owner Type
 * Map<String, String>.Entry
 * @example Nested Parameterized Type with Owner Type and Type Variable
 * Map<String, String>.Entry<K, V>
 * @example Parameterized Type with Type Variable definition
 * List<T extends Number>
 * @example Raw Type
 * String
 * @example Primitive Type
 * int
 * @example Void Type
 * void
 * @example Type Variable without bounds
 * T
 * @example Type Variable with extends bounds
 * T extends Number
 * @example Type Variable with multiple bounds
 * T extends Number & Comparable<T>
 * @example Type Variable with super bounds
 * T super String
 * @example Array Type
 * String[]
 * @example Wildcard Type without bounds
 * ?
 * @example Wildcard Type with extends bounds
 * ? extends Number
 * @example Wildcard Type with super bounds
 * ? super String
 */
/**
 * A Java Type name without package information and type variables
 * @typedef {string} SimplifiedTypeName
 * @example Parameterized Type
 * List
 * @example Nested Parameterized Type
 * Map
 * @example Nested Parameterized Type with Type Variable
 * Map
 * @example Nested Parameterized Type with Wildcard Type
 * Map
 * @example Nested Parameterized Type with Owner Type
 * Map
 * @example Nested Parameterized Type with Owner Type and Type Variable
 * Map
 * @example Parameterized Type with Type Variable definition
 * List
 * @example Raw Type
 * String
 * @example Primitive Type
 * int
 * @example Void Type
 * void
 * @example Type Variable without bounds
 * T
 * @example Type Variable with extends bounds
 * T
 * @example Type Variable with multiple bounds
 * T
 * @example Type Variable with super bounds
 * T
 * @example Array Type
 * String
 * @example Wildcard Type without bounds
 * ?
 * @example Wildcard Type with extends bounds
 * ?
 * @example Wildcard Type with super bounds
 * ?
 */
/**
 * A type that identifies something.
 * @typedef {*} Identifier
 */
/**
 * The index of a type within the {@link DATA.types} array
 * @extends {Identifier}
 * @typedef {(int)} TypeIdentifier
 */
/**
 * The index of a parameter within the {@link DATA.parameters} array
 * @extends {Identifier}
 * @typedef {(int)} ParameterIdentifier
 */
/**
 * The index of a package within the {@link DATA.packages} array
 * @extends {Identifier}
 * @typedef {(int)} PackageIdentifier
 */
/**
 * The index of a name within the {@link DATA.names} array
 * @extends {Identifier}
 * @typedef {(int)} NameIdentifier
 */
/**
 * The index of an annotation within the {@link DATA.annotations} array
 * @extends {Identifier}
 * @typedef {(int)} AnnotationIdentifier
 */
/**
 * The data structure for all compressed data.
 * @typedef {Object} DataDump
 * @property {Array<string>} names
 * //TODO: Finish this.
 * @property {Array<?>} packages
 * @property {Array<?>} types
 * @property {Array<?>} parameters
 * @property {Array<?>} annotations
 */

/**
 * Used to map type variables to their actual types.
 * @typedef {{[key: TypeIdentifier]: TypeIdentifier}} TypeVariableMap
 */

/**
 * Holds compressed data
 * @typedef CompressedDataHolder
 * @property {Object} data - The compressed data.
 * @property {TypeVariableMap} _type_variable_map - The type variable map.
 */

/**
 * Sets the type function on the provided object.
 * The type function retrieves the type of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the type for.
 * @param {string} propertyName - The name of the property that contains the type.
 * @returns {T & TypeHolder} The same object with the type function added.
 */
function setRemapType(target, propertyName) {
    /**
     * @typedef TypeHolder
     * @method type - Retrieves the type of the object.
     * @method getType - Retrieves the type of the object. (Alias for type)
     * @property {function(): TypeIdentifier} type - Retrieves the type of the object.
     * @property {function(): TypeIdentifier} getType - Retrieves the type of the object. (Alias for type)
     */
    /**
     * Retrieves the type of the object.
     *
     * @returns {TypeIdentifier} The type of the object.
     */
    target.type = function () {
        const type = target.data[propertyName];
        if (!exists(target._type_variable_map)) {
            return type;
        }
        if (exists(target._type_variable_map[type])) {
            return target._type_variable_map[type];
        }
        return type;
    }

    target.getType = target.type;

    return target;
}

/**
 * Sets the modifiers function on the provided object.
 * The modifiers function retrieves the modifiers of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the modifiers for.
 * @returns {T & ModifiersHolder} The same object with the modifiers function added.
 */
function setModifiers(target) {
    /**
     * @typedef ModifiersHolder
     * @method modifiers - Retrieves the modifiers of the object.
     * @method getModifiers - Retrieves the modifiers of the object. (Alias for modifiers)
     * @property {function(): int} modifiers - Retrieves the modifiers of the object.
     * @property {function(): int} getModifiers - Retrieves the modifiers of the object. (Alias for modifiers)
     */
    /**
     * Retrieves the modifiers of the object.
     *
     * @returns {int} The modifiers of the object.
     */
    target.modifiers = function () {
        return target.data[PROPERTY.MODIFIERS];
    }

    target.getModifiers = target.modifiers;

    return target;
}

/**
 * Sets the parameters function on the provided object.
 * The parameters function retrieves the parameters of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the parameters for.
 * @returns {T & ParametersHolder} The same object with the parameters function added.
 */
function setParameters(target) {
    /**
     * @typedef ParametersHolder
     * @method parameters - Retrieves the parameters of the object.
     * @method getParameters - Retrieves the parameters of the object. (Alias for parameters)
     * @property {function(): Array} parameters - Retrieves the parameters of the object.
     * @property {function(): Array} getParameters - Retrieves the parameters of the object. (Alias for parameters)
     */
    /**
     * Retrieves the parameters of the object.
     *
     * @returns {Array} The parameters of the object.
     */
    target.parameters = function () {
        return getAsArray(target.data[PROPERTY.PARAMETERS]);
    }

    target.getParameters = target.parameters;

    return target;
}

/**
 * Sets the annotations function on the provided object.
 * The annotations function retrieves the annotations of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the annotations for.
 * @returns {T & AnnotationsHolder} The same object with the annotations function added.
 */
function setAnnotations(target) {
    /**
     * @typedef AnnotationsHolder
     * @method annotations - Retrieves the annotations of the object.
     * @method getAnnotations - Retrieves the annotations of the object. (Alias for annotations)
     * @property {function(): Array} annotations - Retrieves the annotations of the object.
     * @property {function(): Array} getAnnotations - Retrieves the annotations of the object. (Alias for annotations)
     */
    /**
     * Retrieves the annotations of the object.
     *
     * @returns {Array} The annotations of the object.
     */
    target.annotations = function () {
        return getAsArray(target.data[PROPERTY.ANNOTATIONS]);
    }

    target.getAnnotations = target.annotations;

    return target;
}

/**
 * Sets the getTypeVariableMap function on the provided object.
 * The getTypeVariableMap function retrieves the type variable map of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the type variable map for.
 * @returns {T & TypeVariableMapHolder} The same object with the getTypeVariableMap function added.
 */
function setTypeVariableMap(target) {
    /**
     * @typedef TypeVariableMapHolder
     * @method getTypeVariableMap - Retrieves the type variable map of the object.
     * @property {function(): Object} getTypeVariableMap - Retrieves the type variable map of the object.
     */
    /**
     * Retrieves the type variable map of the object.
     *
     * @returns {Object} The type variable map of the object.
     */
    target.getTypeVariableMap = function () {
        if (!exists(target._type_variable_map)) {
            this.data._type_variable_map = {}
        }
        return _type_variable_map;
    }

    return target;
}

/**
 * Sets the getTypeVariables function on the provided object.
 * The getTypeVariables function retrieves the type variables of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the type variables for.
 * @returns {T & TypeVariablesHolder} The same object with the getTypeVariables function added.
 */
function setTypeVariables(target) {
    /**
     * @typedef TypeVariablesHolder
     * @method getTypeVariables - Retrieves the type variables of the object.
     * @property {function(): Array} getTypeVariables - Retrieves the type variables of the object.
     */
    /**
     * Retrieves the type variables of the object.
     *
     * @returns {Array} The type variables of the object.
     */
    target.getTypeVariables = function () {
        return getAsArray(target.data[PROPERTY.TYPE_VARIABLES]);
    }

    return target;
}

/**
 * Sets the name function on the provided object.
 * The name function retrieves the name of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the name for.
 * @returns {T & NameHolder} The same object with the name function added.
 */
function setBasicName(target) {
    /**
     * @typedef NameHolder
     * @method name - Retrieves the name of the object.
     * @method getName - Retrieves the name of the object. (Alias for name)
     * @property {function(): string} name - Retrieves the name of the object.
     * @property {function(): string} getName - Retrieves the name of the object. (Alias for name)
     */
    /**
     * Retrieves the name of the object.
     *
     * @returns {string} The name of the object.
     */
    target.name = function () {
        if (!exists(target.data._name_cache)) {
            target.data._name_cache = uncompressString(target.data[PROPERTY.NAME]);
        }
        return target.data._name_cache;
    }

    target.getName = target.name;

    return target;
}

/**
 * Sets the dataIndex function on the provided object.
 * The dataIndex function retrieves the index of the object in the data array.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the dataIndex for.
 * @returns {T & DataIndexHolder} The same object with the dataIndex function added.
 */
function setDataIndex(target) {
    /**
     * @typedef DataIndexHolder
     * @method dataIndex - Retrieves the index of the object in the data array.
     * @method getDataIndex - Retrieves the index of the object in the data array. (Alias for dataIndex)
     * @property {function(): int} dataIndex - Retrieves the index of the object in the data array.
     * @property {function(): int} getDataIndex - Retrieves the index of the object in the data array. (Alias for dataIndex)
     */
    /**
     * Retrieves the index of the object in the data array.
     *
     * @returns {int} The index of the object in the data array.
     */
    target.dataIndex = function () {
        return target.data[PROPERTY.DATA_INDEX];
    }

    target.getDataIndex = target.dataIndex;

    return target;
}

/**
 * Sets the declaredIn function on the provided object.
 * The declaredIn function retrieves the index of the object that declares this object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the declaredIn for.
 * @returns {T & DeclaredInHolder} The same object with the declaredIn function added.
 */
function setDeclaredIn(target) {
    /**
     * @typedef DeclaredInHolder
     * @method declaredIn - Retrieves the index of the object that declares this object.
     * @method getDeclaredIn - Retrieves the index of the object that declares this object. (Alias for declaredIn)
     * @property {function(): int} declaredIn - Retrieves the index of the object that declares this object.
     * @property {function(): int} getDeclaredIn - Retrieves the index of the object that declares this object. (Alias for declaredIn)
     */
    /**
     * Retrieves the index of the object that declares this object.
     *
     * @returns {int} The index of the object that declares this object.
     */
    target.declaredIn = function () {
        return target.data[PROPERTY.DECLARED_IN];
    }

    target.getDeclaredIn = target.declaredIn;

    return target;
}

/**
 * Sets the id function on the provided object based on it's type function.
 * The id function retrieves the id of the object.
 * @template {CompressedDataHolder & TypeHolder} T The source object type.
 * @param {T} target - The object to set the id for.
 * @returns {T & IdHolder} The same object with the id function added.
 */
function setTypeBasedID(target) {
    /**
     * @typedef IdHolder
     * @method id - Retrieves the id of the object.
     * @method getId - Retrieves the id of the object. (Alias for id)
     * @property {function(): int} id - Retrieves the id of the object.
     * @property {function(): int} getId - Retrieves the id of the object. (Alias for id)
     */
    /**
     * Retrieves the id of the object.
     *
     * @returns {int} The id of the object.
     */
    target.id = function () {
        return target.type();
    }

    target.getId = target.id;

    return target;
}

/**
 * Sets the getExceptions function on the provided object.
 * The getExceptions function retrieves the exceptions thrown by the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the getExceptions for.
 * @returns {T & ExceptionsHolder} The same object with the getExceptions function added.
 */
function setExceptions(target) {
    /**
     * @typedef ExceptionsHolder
     * @method getExceptions - Retrieves the exceptions thrown by the object.
     * @property {function(): Array} getExceptions - Retrieves the exceptions thrown by the object.
     */
    /**
     * Retrieves the exceptions thrown by the object.
     *
     * @returns {Array} The exceptions thrown by the object.
     */
    target.getExceptions = function () {
        return getAsArray(target.data[PROPERTY.EXCEPTIONS]);
    }

    return target;
}