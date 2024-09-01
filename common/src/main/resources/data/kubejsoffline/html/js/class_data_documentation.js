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
 * @public
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
 * @public
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
 * @public
 */
/**
 * A combination of every type name.
 * @see FullTypeName
 * @see TypeName
 * @see SimplifiedTypeName
 * @typedef {FullTypeName | TypeName | SimplifiedTypeName} JavaTypeName
 * @public
 */
/**
 * The name of a java package.
 * @typedef {string} PackageName
 * @example java.lang
 * @example net.minecraft
 * @example com.example
 * @public
 */
/**
 * Something that uniquely identifies something else.
 * @typedef {*} Identifier
 * @public
 */
/**
 * The index of a type within the {@link DATA.types} array
 * @extends {Identifier}
 * @typedef {(int)} TypeIdentifier
 * @public
 */
/**
 * The index of a parameter within the {@link DATA.parameters} array
 * @extends {Identifier}
 * @typedef {(int)} ParameterIdentifier
 * @public
 */
/**
 * The index of a package within the {@link DATA.packages} array
 * @extends {Identifier}
 * @typedef {(int)} PackageIdentifier
 * @public
 */
/**
 * The index of a name within the {@link DATA.names} array
 * @extends {Identifier}
 * @typedef {(int)} NameIdentifier
 * @public
 */
/**
 * The index of an annotation within the {@link DATA.annotations} array
 * @extends {Identifier}
 * @typedef {(int)} AnnotationIdentifier
 * @public
 */
/**
 * The fully qualified definition of a constructor.
 * @extends {Identifier}
 * @typedef {string} ConstructorDefinition
 * @public
 */
/**
 * The fully qualified definition of a method.
 * @extends {Identifier}
 * @typedef {string} MethodDefinition
 * @public
 */
/**
 * The fully qualified definition of a field.
 * @extends {Identifier}
 * @typedef {string} FieldDefinition
 * @public
 */
/**
 * The fully qualified definition of a parameter.
 * @extends {Identifier}
 * @typedef {string} ParameterDefintion
 * @public
 */
/**
 * The fully qualified definition of a class.
 * @extends {Identifier}
 * @typedef {string} ClassDefinition
 * @public
 */

/**
 * Used to map type variables to their actual types.
 * @typedef {{[key: TypeIdentifier]: TypeIdentifier}} TypeVariableMap
 * @public
 */
/**
 * Indexed class data.
 * @typedef {Object} IndexedClassData
 * @property {TypeIdentifier} _id - The id of the class.
 * @public
 */

/**
 * Holds compressed data
 * @typedef CompressedDataHolder
 * @property {Object} data - The compressed data.
 * @property {TypeVariableMap} _type_variable_map - The type variable map.
 * @public
 */
/**
 * @typedef TypeHolder
 * @method type - Retrieves the type of the object.
 * @method getType - Retrieves the type of the object. (Alias for type)
 * @property {function(): TypeIdentifier} type - Retrieves the type of the object.
 * @property {function(): TypeIdentifier} getType - Retrieves the type of the object. (Alias for type)
 * @public
 */
/**
 * @typedef ModifiersHolder
 * @method modifiers - Retrieves the modifiers of the object.
 * @method getModifiers - Retrieves the modifiers of the object. (Alias for modifiers)
 * @property {function(): int} modifiers - Retrieves the modifiers of the object.
 * @property {function(): int} getModifiers - Retrieves the modifiers of the object. (Alias for modifiers)
 * @public
 */
/**
 * @typedef ParametersHolder
 * @method parameters - Retrieves the parameters of the object.
 * @method getParameters - Retrieves the parameters of the object. (Alias for parameters)
 * @property {function(): Array<Parameter>} parameters - Retrieves the parameters of the object.
 * @property {function(): Array<Parameter>} getParameters - Retrieves the parameters of the object. (Alias for parameters)
 * @public
 */
/**
 * @typedef AnnotationsHolder
 * @method annotations - Retrieves the annotations of the object.
 * @method getAnnotations - Retrieves the annotations of the object. (Alias for annotations)
 * @property {function(): Array<Annotation>} annotations - Retrieves the annotations of the object.
 * @property {function(): Array<Annotation>} getAnnotations - Retrieves the annotations of the object. (Alias for annotations)
 * @public
 */
/**
 * @typedef TypeVariableMapHolder
 * @property _type_variable_map - The type variable map.
 * @method getTypeVariableMap - Retrieves the type variable map of the object.
 * @property {function(): TypeVariableMap} getTypeVariableMap - Retrieves the type variable map of the object.
 * @public
 */
/**
 * @typedef NameHolder
 * @method name - Retrieves the name of the object.
 * @method getName - Retrieves the name of the object. (Alias for name)
 * @property {function(): string} name - Retrieves the name of the object.
 * @property {function(): string} getName - Retrieves the name of the object. (Alias for name)
 * @public
 */
/**
 * @typedef {NameHolder} TypeVariablesHolder
 * @method getTypeVariables - Retrieves the type variables of the object.
 * @property {function(): Array<TypeIdentifier>} getTypeVariables - Retrieves the type variables of the object.
 *
 * @public
 */
/**
 * @typedef DataIndexHolder
 * @method dataIndex - Retrieves the index of the object in the data array.
 * @method getDataIndex - Retrieves the index of the object in the data array. (Alias for dataIndex)
 * @property {function(): int} dataIndex - Retrieves the index of the object in the data array.
 * @property {function(): int} getDataIndex - Retrieves the index of the object in the data array. (Alias for dataIndex)
 * @public
 */
/**
 * @typedef DeclaringClassHolder
 * @method declaringClass - Retrieves the index of the object that declares this object.
 * @method getDeclaringClass - Retrieves the index of the object that declares this object. (Alias for declaredIn)
 * @property {function(): int} declaringClass - Retrieves the index of the object that declares this object.
 * @property {function(): int} getDeclaringClass - Retrieves the index of the object that declares this object. (Alias for declaredIn)
 * @public
 */
/**
 * @typedef IdHolder
 * @template {Identifier} ID
 * @method id - Retrieves the {@link ID} of the object.
 * @method getId - Retrieves the {@link ID} of the object. (Alias for id)
 * @property {function(): ID} id - Retrieves the id of the object.
 * @property {function(): ID} getId - Retrieves the id of the object. (Alias for id)
 * @public
 */
/**
 * @typedef HyperLinkable
 * @method hrefLink - Retrieves the hyperlink for the object.
 * @method getHrefLink - Retrieves the hyperlink for the object. (Alias for hrefLink)
 * @property {function(): string} hrefLink - Retrieves the hyperlink for the object.
 * @property {function(): string} getHrefLink - Retrieves the hyperlink for the object. (Alias for hrefLink)
 * @public
 */
/**
 * @typedef KubeJSCodeFormattable
 * @method toKubeJSStaticCall - Formats the object as a KubeJS static call.
 * @method toKubeJSCode - Formats the object as a KubeJS static reference.
 * @property {function(): string} toKubeJSStaticCall - Formats the object as a KubeJS static call.
 * @property {function(): string} toKubeJSCode - Formats the object as a KubeJS static reference.
 * @public
 */
/**
 * @typedef ExceptionsHolder
 * @method getExceptions - Retrieves the exceptions thrown by the object.
 * @property {function(): Array} getExceptions - Retrieves the exceptions thrown by the object.
 * @public
 */


/**
 * @typedef MethodHolder
 * @method methods - Retrieves an array of {@link Method} objects.
 * @method getMethods - Retrieves an array of {@link Method} objects. (Alias for methods)
 * @property {function(): Array<Method>} methods - Retrieves an array of {@link Method} objects.
 * @property {function(): Array<Method>} getMethods - Retrieves an array of {@link Method} objects. (Alias for methods)
 * @public
 */
/**
 * @typedef FieldHolder
 * @method fields - Retrieves an array of {@link Field} objects.
 * @method getFields - Retrieves an array of {@link Field} objects. (Alias for fields)
 * @property {function(): Array<Field>} fields - Retrieves an array of {@link Field} objects.
 * @property {function(): Array<Field>} getFields - Retrieves an array of {@link Field} objects. (Alias for fields)
 * @public
 */
/**
 * @typedef ConstructorHolder
 * @method constructors - Retrieves an array of {@link Constructor} objects.
 * @method getConstructors - Retrieves an array of {@link Constructor} objects. (Alias for constructors)
 * @property {function(): Array<Constructor>} constructors - Retrieves an array of {@link Constructor} objects.
 * @property {function(): Array<Constructor>} getConstructors - Retrieves an array of {@link Constructor} objects. (Alias for constructors)
 * @public
 */
/**
 * @typedef InterfaceHolder
 * @method interfaces - Retrieves an array of {@link TypeIdentifier} objects.
 * @method getInterfaces - Retrieves an array of {@link TypeIdentifier} objects. (Alias for interfaces)
 * @property {function(): Array<TypeIdentifier>} interfaces - Retrieves an array of {@link TypeIdentifier} objects.
 * @property {function(): Array<TypeIdentifier>} getInterfaces - Retrieves an array of {@link TypeIdentifier} objects. (Alias for interfaces)
 */
/**
 * @typedef JavaTypeNameHolder
 *
 * @method referenceName - Retrieves the {@link FullTypeName} with generics enabled.
 * @method getReferenceName - Retrieves the {@link FullTypeName} with generics enabled. (Alias for referenceName)
 *
 * @method fullyQualifiedName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default.
 * @method fullName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default. (Alias for fullyQualifiedName)
 * @method getFullyQualifiedName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default. (Alias for fullyQualifiedName)'
 * @method getFullName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default. (Alias for fullyQualifiedName)
 *
 * @method name - Retrieves the {@link TypeName} with an option for generics which is enabled by default.
 * @method getName - Retrieves the {@link TypeName} with an option for generics which is enabled by default. (Alias for name)
 *
 * @method simpleName - Retrieves the {@link SimplifiedTypeName} which does not include generics or type-bounds.
 * @method getSimpleName - Retrieves the {@link SimplifiedTypeName} which does not include generics or type-bounds. (Alias for simpleName)
 *
 * @method package - Retrieves the {@link PackageName} of the object.
 * @method getPackageName - Retrieves the {@link PackageName} of the object. (Alias for package)
 * @method getPacakge - Retrieves the {@link PackageName} of the object. (Alias for package)
 *
 * @property {function(TypeVariableMap=, boolean): FullTypeName} referenceName - Retrieves the {@link FullTypeName} with generics enabled.
 * @property {function(TypeVariableMap=, boolean): FullTypeName} getReferenceName - Retrieves the {@link FullTypeName} with generics enabled. (Alias for referenceName)
 * @property {function(TypeVariableMap=, boolean): FullTypeName} fullyQualifiedName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default.
 * @property {function(TypeVariableMap=, boolean): FullTypeName} fullName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default. (Alias for fullyQualifiedName)
 * @property {function(TypeVariableMap=, boolean): FullTypeName} getFullyQualifiedName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default. (Alias for fullyQualifiedName)
 * @property {function(TypeVariableMap=, boolean): FullTypeName} getFullName - Retrieves the {@link FullTypeName} with an option for generics which is enabled by default. (Alias for fullyQualifiedName)
 * @property {function(TypeVariableMap=, boolean): TypeName} name - Retrieves the {@link TypeName} with an option for generics which is enabled by default.
 * @property {function(TypeVariableMap=, boolean): TypeName} getName - Retrieves the {@link TypeName} with an option for generics which is enabled by default. (Alias for name)
 * @property {function(TypeVariableMap=): SimplifiedTypeName} simpleName - Retrieves the {@link SimplifiedTypeName} which does not include generics or type-bounds.
 * @property {function(TypeVariableMap=): SimplifiedTypeName} getSimpleName - Retrieves the {@link SimplifiedTypeName} which does not include generics or type-bounds. (Alias for simpleName)
 * @property {function(): PackageName} package - Retrieves the {@link PackageName} of the object.
 * @property {function(): PackageName} getPackageName - Retrieves the {@link PackageName} of the object. (Alias for package)
 * @property {function(): PackageName} getPacakge - Retrieves the {@link PackageName} of the object. (Alias for package)
 * @public
 */

/**
 * @typedef ParameterizedTypeHolder
 * @method ownerType - Retrieves the owner type of the object.
 * @method getOwnerType - Retrieves the owner type of the object. (Alias for ownerType
 * @method getRawType - Retrieves the raw type of the object.
 * @method getParameterizedArgs - Retrieves the parameterized arguments of the object.
 * @property {function(): TypeIdentifier?} ownerType - Retrieves the owner type of the object.
 * @property {function(): TypeIdentifier?} getOwnerType - Retrieves the owner type of the object. (Alias for ownerType)
 * @property {function(): TypeIdentifier} getRawType - Retrieves the raw type of the object.
 * @property {function(): Array<TypeIdentifier>} getParameterizedArgs - Retrieves the parameterized arguments of the object.
 * @public
 */

/**
 * @typedef WildcardTypeHolder
 * @method getLowerBounds - Retrieves the lower bounds of the object.
 * @method getUpperBounds - Retrieves the upper bounds of the object.
 * @property {function(): Array<TypeIdentifier>} getLowerBounds - Retrieves the lower bounds of the object.
 * @property {function(): Array<TypeIdentifier>} getUpperBounds - Retrieves the upper bounds of the object.
 * @public
 */
/**
 * @typedef TypeVariableHolder
 * @method getTypeVariableBounds - Retrieves the bounds of the object.
 * @property {function(): Array<TypeIdentifier>} getTypeVariableBounds - Retrieves the bounds of the object.
 * @public
 */
/**
 * @typedef {
 *     TypeHolder &
 *     ModifiersHolder &
 *     AnnotationsHolder
 * } RawClassTypeHolder
 * @public
 */
/**
 * @typedef TypeWrapper
 * @method isRaw - Checks if the object is a raw type.
 * @method isRawType - Checks if the object is a raw type (Alias for isRaw).
 * @method isRawClass - Checks if the object is a raw type (Alias for isRaw).
 * @method isParameterized - Checks if the object is a parameterized type.
 * @method isParameterizedType - Checks if the object is a parameterized type (Alias for isParameterized).
 * @method isWildcard - Checks if the object is a wildcard type.
 * @method isWildcardType - Checks if the object is a wildcard type (Alias for isWildcard).
 * @method isTypeVariable - Checks if the object is a type variable.
 * @property {function(): boolean} isRaw - Checks if the object is a raw type.
 * @property {function(): boolean} isRawType - Checks if the object is a raw type.
 * @property {function(): boolean} isRawClass - Checks if the object is a raw type.
 * @property {function(): boolean} isParameterized - Checks if the object is a parameterized type.
 * @property {function(): boolean} isParameterizedType - Checks if the object is a parameterized type.
 * @property {function(): boolean} isWildcard - Checks if the object is a wildcard type.
 * @property {function(): boolean} isWildcardType - Checks if the object is a wildcard type.
 * @property {function(): boolean} isTypeVariable - Checks if the object is a type variable.
 * @public
 */

/**
 * @typedef RelationshipHolder
 * @method getRelationships - Retrieves the relationships of the object.
 * @property {function(): Array<Relationship>} getRelationships - Retrieves the relationships of the object.
 * @public
 */


/**
 * @typedef {
 * NameHolder &
 * TypeHolder &
 * ModifiersHolder &
 * AnnotationsHolder &
 * DataIndexHolder &
 * TypeVariableMapHolder &
 * IdHolder<ParameterDefintion>
 * } Parameter
 *
 * @public
 */

/**
 * @typedef {
 *     TypeHolder &
 *     TypeVariableMap
 * } Annotation
 * @property {function(): string} string Returns the string representation of the annotation.
 * @property {function(): string} getString Returns the string representation of the annotation. (Alias of string)
 *
 * @public
 */

/**
 * This type is a wrapper for compressed Constructor data.
 * It contains all the necessary functions to retrieve the data, and the data itself.
 *
 * @typedef {
 * ModifiersHolder &
 * AnnotationsHolder &
 * ParametersHolder &
 * DataIndexHolder &
 * DeclaringClassHolder &
 * TypeVariableMapHolder &
 * IdHolder<Constructor> &
 * HyperLinkable
 * } Constructor
 *
 * @public
 */

/**
 * This type is a wrapper for compressed Field data.
 * It contains all the necessary functions to retrieve the data, and the data itself.
 *
 * @typedef {
 * NameHolder &
 * TypeHolder &
 * ModifiersHolder &
 * AnnotationsHolder &
 * DataIndexHolder &
 * DeclaringClassHolder &
 * TypeVariablesHolder &
 * IdHolder<FieldDefinition> &
 * HyperLinkable &
 * KubeJSCodeFormattable
 * } Field
 *
 * @public
 */

/**
 * This type is a wrapper for compressed Method data.
 * It contains all the necessary functions to retrieve the data, and the data itself.
 *
 * @typedef {
 * NameHolder &
 * TypeHolder &
 * ModifiersHolder &
 * AnnotationsHolder &
 * TypeVariableMapHolder &
 * ParametersHolder &
 * DataIndexHolder &
 * DeclaringClassHolder &
 * TypeVariablesHolder &
 * IdHolder<MethodDefinition> &
 * HyperLinkable &
 * KubeJSCodeFormattable
 * } Method
 *
 * @public
 */

/**
 * This type is a wrapper for compressed java type data.
 * It contains all the necessary functions to retrieve the data, and the data itself.
 *
 * @typedef {
 * NameHolder &
 * ModifiersHolder &
 * AnnotationsHolder &
 * TypeVariableMapHolder &
 * DataIndexHolder &
 * TypeVariablesHolder &
 * IdHolder<TypeIdentifier> &
 * HyperLinkable &
 * KubeJSCodeFormattable &
 * IndexedClassData &
 * ConstructorHolder &
 * MethodHolder &
 * FieldHolder &
 * InterfaceHolder &
 * JavaTypeNameHolder &
 * ParameterizedTypeHolder &
 * WildcardTypeHolder &
 * TypeVariableHolder &
 * RawClassTypeHolder &
 * TypeWrapper
 * } JavaType
 *
 * @public
 */

/**
 * This type is a wrapper for the url data. This class helps determine
 * the current page, provides functions to retrieve the data, and the data itself.
 * @typedef {{hash: string, params: URLSearchParams, chromeHighlightText: string, hasFocus: function(): boolean, getFocus: function(): string, getFocusOrDefaultHeader: function(): string, isSearch: function(): boolean, isClass: function(): boolean, isHome: function(): boolean, href: function(): string, hrefHash: function(): string, getParamSize: function(): number, getParamSizeSafe: function(): number, clone: function(): URLData}} URLData
 * @public
 */


/**
 * This type is a wrapper for class's relationship to another class.
 * It acts like a {@link JavaType} but the type is the related class.
 * @typedef {
 * JavaType &
 * RelationshipHolder
 * } Relationship
 *
 * @public
 */

/**
 * Sets the type function on the provided object.
 * The type function retrieves the type of the object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the type for.
 * @returns {T & TypeHolder} The same object with the type function added.
 */
function setRemapType(target) {
    /**
     * Retrieves the type of the object.
     *
     * @returns {TypeIdentifier} The type of the object.
     */
    target.type = function () {
        const type = target.data[PROPERTY.TYPE];
        if (!exists(target.getTypeVariableMap)) {
            return type;
        }
        return target.getTypeVariableMap()[type] ?? type;
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
     * Retrieves the modifiers of the object.
     *
     * @returns {int} The modifiers of the object.
     */
    target.modifiers = function () {
        if (target.data[PROPERTY.MODIFIERS] === undefined) {
            if (target["getRawType"] !== undefined && exists(target.getRawType())) {
                return getClass(target.getRawType()).modifiers();
            }
        }
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
     * Converts a parameter to a Parameter object.
     * @param {ParameterIdentifier} parameter - The parameter to convert.
     * @returns {Parameter} The converted parameter.
     */
    function mapParameter(parameter) {
        return getParameter(parameter, target.getTypeVariableMap());
    }

    /**
     * Retrieves the parameters of the object.
     *
     * @returns {Parameter[]} The parameters of the object.
     */
    target.parameters = function () {
        return getAsArray(target.data[PROPERTY.PARAMETERS]).map(mapParameter);
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
     * Maps an annotation to an Annotation object.
     * @param {AnnotationIdentifier} annotation - The annotation to map.
     * @returns {Annotation} The mapped annotation.
     */
    function mapAnnotation(annotation) {
        return getAnnotation(annotation, target.getTypeVariableMap());
    }

    /**
     * Retrieves the annotations of the object.
     *
     * @returns {Annotation[]} The annotations of the object.
     */
    target.annotations = function () {
        return getAsArray(target.data[PROPERTY.ANNOTATIONS]).map(mapAnnotation);
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
     * Retrieves the type variable map of the object.
     *
     * @returns {TypeVariableMap} The type variable map of the object.
     */
    target.getTypeVariableMap = function () {
        if (!exists(target._type_variable_map)) {
            target._type_variable_map = {}
        }
        return target._type_variable_map;
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
     * Retrieves the name of the object.
     *
     * @returns {string} The name of the object.
     */
    target.name = function () {
        if (!exists(target.data._name_cache)) {
            target.data._name_cache = decompressString(target.data[PROPERTY.NAME]);
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
     * Retrieves the index of the object in the data array.
     *
     * @returns {int} The index of the object in the data array.
     */
    target.dataIndex = function () {
        return target.data._dataIndex;
    }

    target.getDataIndex = target.dataIndex;

    return target;
}

/**
 * Sets the declaringClass function on the provided object.
 * The declaredIn function retrieves the index of the object that declares this object.
 * @template {CompressedDataHolder} T The source object type.
 * @param {T} target - The object to set the declaredIn for.
 * @returns {T & DeclaringClassHolder} The same object with the declaredIn function added.
 */
function setDeclaringClass(target) {
    /**
     * Retrieves the index of the object that declares this object.
     *
     * @returns {int} The index of the object that declares this object.
     */
    target.declaringClass = function () {
        return target.data._declaringClass;
    }

    target.getDeclaringClass = target.declaringClass;

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
     * Retrieves the exceptions thrown by the object.
     *
     * @returns {Array} The exceptions thrown by the object.
     */
    target.getExceptions = function () {
        return getAsArray(target.data[PROPERTY.EXCEPTIONS]);
    }

    return target;
}