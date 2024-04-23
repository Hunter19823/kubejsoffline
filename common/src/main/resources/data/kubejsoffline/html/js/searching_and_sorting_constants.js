/**
 * Class Attributes
 *
 * @file searching_and_sorting.js
 * @type {{OWNER_TYPE: string, TYPE_VARIABLE_BOUNDS: string, LOWER_BOUND: string, ARRAY_DEPTH: string, MODIFIERS: string, PACKAGE: string, PARAMETERIZED_ARGUMENTS: string, TYPED_NAME: string, UPPER_BOUND: string, SIMPLE_NAME: string, REFERENCE_NAME: string, ID: string, FULL_NAME_NON_GENERIC: string, RAW_TYPE: string, SUPER_CLASS: string}}
 */
const CLASS_ATTRIBUTES = {
    ID: 'id',
    REFERENCE_NAME: 'referenceName',
    FULL_NAME_NON_GENERIC: 'fullyQualifiedName',
    TYPED_NAME: 'name',
    SIMPLE_NAME: 'simplename',
    PACKAGE: 'getPackageName',
    ARRAY_DEPTH: 'getArrayDepth',
    OWNER_TYPE: 'getOwnerType',
    TYPE_VARIABLE_BOUNDS: 'getTypeVariableBounds',
    LOWER_BOUND: 'getLowerBound',
    UPPER_BOUND: 'getUpperBound',
    RAW_TYPE: 'getRawType',
    MODIFIERS: 'getModifiers',
    PARAMETERIZED_ARGUMENTS: 'getParameterizedArgs',
    SUPER_CLASS: 'getSuperClass',
}
/**
 * Parameter Attributes
 * @file searching_and_sorting.js
 * @type {{MODIFIERS: string, TYPE: string, NAME: string}}
 */
const PARAMETER_ATTRIBUTES = {
    NAME: 'name',
    TYPE: 'type',
    MODIFIERS: 'modifiers'
}
/**
 * Method Attributes
 * @file searching_and_sorting.js
 * @type {{MODIFIERS: string, DECLARED_IN: string, REFERENCE_NAME: string, PARAMETERS: string, TYPE: string, NAME: string}}
 */
const METHOD_ATTRIBUTES = {
    NAME: 'name',
    TYPE: 'returnType',
    MODIFIERS: 'modifiers',
    PARAMETERS: 'parameters',
    DECLARED_IN: 'declaredIn',
    REFERENCE_NAME: 'id'
}
/**
 * Field Attributes
 * @file searching_and_sorting.js
 * @type {{MODIFIERS: string, DECLARED_IN: string, REFERENCE_NAME: string, TYPE: string, NAME: string}}
 */
const FIELD_ATTRIBUTES = {
    NAME: 'name',
    TYPE: 'type',
    MODIFIERS: 'modifiers',
    DECLARED_IN: 'declaredIn',
    REFERENCE_NAME: 'id'
}
/**
 * Constructor Attributes
 * @file searching_and_sorting.js
 * @type {{MODIFIERS: string, DECLARED_IN: string, REFERENCE_NAME: string, PARAMETERS: string}}
 */
const CONSTRUCTOR_ATTRIBUTES = {
    MODIFIERS: 'modifiers',
    PARAMETERS: 'parameters',
    DECLARED_IN: 'declaredIn',
    REFERENCE_NAME: 'id'
}
/**
 * Annotation Attributes
 * @file searching_and_sorting.js
 * @type {{TEXT: string, NAME: string}}
 */
const ANNOTATION_ATTRIBUTES = {
    NAME: 'type',
    TEXT: 'string'
}

/**
 * All query-able attributes
 *
 * @file searching_and_sorting.js
 * @type {{"parameter-raw-type": string, "raw-class-only": string, "method-id": string, simplename: string, "method-type-package": string, "method-name": string, "class-package": string, "class-any": string, "ignore-parameters": string, "field-any": string, "method-type-name": string, "type-name": string, "field-id": string, "parameter-id": string, "non-raw-class-only": string, "class-id": string, "class-raw-type": string, "parameter-type-name": string, "method-any": string, "class-type-simplename": string, "parameter-any": string, "field-raw-type": string, "field-type-name": string, "ignore-methods": string, "class-type-name": string, "field-name": string, "field-type-package": string, "parameter-type-simple-name": string, package: string, "parameter-name": string, "non-wildcard-only": string, any: string, "wildcard-only": string, "method-type-simplename": string, "class-name": string, "method-raw-type": string, "parameter-count": string, "type-variable-only": string, "ignore-classes": string, "field-type-simplename": string, "non-type-variable-only": string, "parameter-type-package": string, "ignore-fields": string, name: string, "raw-type": string}}
 */
const NEW_QUERY_TERMS = {
    'any': 'withAny',
    'class-any': 'withClassAny',
    'field-any': 'withFieldAny',
    'method-any': 'withMethodAny',
    'parameter-any': 'withMethodParameterAny',


    'class-id': 'withClassId',
    'field-id': 'withFieldTypeId',
    'method-id': 'withMethodReturnTypeId',
    'parameter-id': 'withMethodParameterTypeId',


    'name': 'withName',
    'class-name': 'withClassName',
    'field-name': 'withFieldName',
    'method-name': 'withMethodName',
    'parameter-name': 'withMethodParameterName',

    'field-type-name': 'withFieldTypeName',
    'method-type-name': 'withMethodReturnTypeName',
    'parameter-type-name': 'withMethodParameterTypeName',


    'simplename': 'withSimpleName',
    'class-type-simplename': 'withClassSimpleName',
    'field-type-simplename': 'withFieldTypeSimpleName',
    'method-type-simplename': 'withMethodReturnTypeSimpleName',
    'parameter-type-simple-name': 'withMethodParameterTypeSimpleName',


    'raw-type': 'withRawType',
    'class-raw-type': 'withClassRawType',
    'field-raw-type': 'withFieldRawType',
    'method-raw-type': 'withMethodReturnRawType',
    'parameter-raw-type': 'withMethodParameterRawType',


    'type-name': 'withType',
    'class-type-name': 'withClassType',
    'field-type-name': 'withFieldTypeTypeName',
    'method-type-name': 'withMethodReturnTypeTypeName',
    'parameter-type-name': 'withMethodParameterTypeTypeName',


    'package': 'withPackage',
    'class-package': 'withClassPackage',
    'field-type-package': 'withFieldTypePackage',
    'method-type-package': 'withMethodReturnTypePackage',
    'parameter-type-package': 'withMethodParameterTypePackage',


    'parameter-count': 'withMethodParameterCount',


    'ignore-classes': 'withIgnoreClasses',
    'ignore-fields': 'withIgnoreFields',
    'ignore-methods': 'withIgnoreMethods',
    'ignore-parameters': 'withIgnoreParameters',


    'raw-class-only': 'withRawClassOnly',
    'non-raw-class-only': 'withNonRawClassOnly',
    'type-variable-only': 'withTypeVariableOnly',
    'non-type-variable-only': 'withNonTypeVariableOnly',
    'wildcard-only': 'withWildcardOnly',
    'non-wildcard-only': 'withNonWildcardOnly',


}

/**
 * Sort Functions
 * @file searching_and_sorting.js
 * @type {{declared: ((function(*, *): (number))|*), default: ((function(*, *): (number|undefined))|*), private: ((function(*, *): (number))|*), static: ((function(*, *): (number))|*), simple_name: ((function(*, *): (number))|*), public: ((function(*, *): (number))|*), protected: ((function(*, *): (number))|*), parameter_count: ((function(*, *): (number))|*), name: ((function(*, *): (number))|*), final: ((function(*, *): (number))|*), super_class: ((function(*, *): (number))|*), type: ((function(*, *): (number))|*)}}
 */
const SORT_FUNCTIONS = {
    'default': defaultSort,
    'name': sortByAttribute('name'),
    'public': sortByModifiedAttribute('mod', (mod) => {
        return MODIFIER.isPublic(mod);
    }),
    'protected': sortByModifiedAttribute('mod', (mod) => {
        return MODIFIER.isProtected(mod);
    }),
    'private': sortByModifiedAttribute('mod', (mod) => {
        return MODIFIER.isPrivate(mod);
    }),
    'final': sortByModifiedAttribute('mod', (mod) => {
        return MODIFIER.isFinal(mod);
    }),
    'static': sortByModifiedAttribute('mod', (mod) => {
        return MODIFIER.isStatic(mod);
    }),
    'super_class': sortByModifiedAttribute('type', (type) => {
        return getClass(type).superclass();
    }),
    'simple_name': sortByModifiedAttribute('type', (type) => {
        return getClass(type).simplename();
    }),
    'type': sortByModifiedAttribute('type', (type) => {
        return getClass(type).fullyQualifiedName();
    }),
    'declared': sortByModifiedAttribute('declared-in', (type) => {
        return getClass(type).getDeclaringClass();
    }),
    'parameter_count': sortByModifiedAttribute('parameters', (count) => {
        return parseInt(count);
    }),
}

