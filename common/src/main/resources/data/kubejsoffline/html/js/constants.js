/**
 * Used by classdatatools to map fully qualified names to ids.
 * @file constants.js
 * @type {Map<JavaTypeName, TypeIdentifier>}
 * @public
 */
const LOOK_UP_CACHE = new Map();

/**
 * Used to dump gradle variables into static javascript variables for use in the website.
 *
 * @file constants.js
 * @enum {string}
 * @public
 */
const PROJECT_INFO = {
    "mod_id": "${mod_id}",
    "mod_name": "${mod_name}",
    "mod_version": "${mod_version}",
    "mod_description": "${mod_description}",
    "mod_homepage": "${mod_homepage}",
    "mod_source": "${mod_source}",
    "mod_author": "${mod_author}",
    "curseforge_id": "${curseforge_id}",
    "modrinth_id": "${modrinth_id}",
    "minecraft_version": "${minecraft_version}",
    "fabric_loader_version": "${fabric_loader_version}",
    "fabric_api_version": "${fabric_api_version}",
    "forge_version": "${forge_version}",
    "kubejs_version": "${kubejs_version}",
    "architectury_version": "${architectury_version}",
    "git_commit": "${github_commit_hash}",
}

/**
 * Used by createsignaturetools.js to map link identifiers to onClick functions.
 *
 * @file constants.js
 * @type {{}}
 * @public
 */
const LINK_MAP = {};


/**
 * Global settings for the website.
 *
 * @file constants.js
 * @enum {boolean | number}
 * @public
 */
const GLOBAL_SETTINGS = {
    "showMethods": true,
    "showMethodsInherited": true,
    "showFields": true,
    "showFieldsInherited": true,
    "showConstructors": true,
    "showConstructorsInherited": true,
    "showPrivate": false,
    "showProtected": true,
    "showPackage": true,
    "showRelationships": true,
    "defaultSearchPageSize": 25,
    "debug": false,
};

/**
 * This is a JS equivalent of the Java Modifier class.
 * It is used to decode and encode access and modifier flags.
 *
 * @file constants.js
 * @type {{VOLATILE: number, METHOD_MODIFIERS: number, VARARGS: number, NATIVE: number, INTERFACE_MODIFIERS: number, isStatic(*): boolean, isVolatile(*): boolean, isSynthetic(*): boolean, PARAMETER_MODIFIERS: number, isPrivate(*): boolean, BRIDGE: number, ABSTRACT: number, STATIC: number, isNative(*): boolean, CONSTRUCTOR_MODIFIERS: number, isStrict(*): boolean, interfaceModifiers(): number, isProtected(*): boolean, classModifiers(): number, INTERFACE: number, ANNOTATION: number, isPublic(*): boolean, ACCESS_MODIFIERS: number, isFinal(*): boolean, PROTECTED: number, isInterface(*): boolean, isMandated(*): boolean, SYNCHRONIZED: number, STRICT: number, ENUM: number, fieldModifiers(): number, FIELD_MODIFIERS: number, CLASS_MODIFIERS: number, methodModifiers(): number, SYNTHETIC: number, FINAL: number, isSynchronized(*): boolean, constructorModifiers(): number, parameterModifiers(): number, isAbstract(*): boolean, PRIVATE: number, PUBLIC: number, MANDATED: number, toString(*): string, isTransient(*): boolean, TRANSIENT: number}}
 * @public
 */
const MODIFIER = {
    PUBLIC: 1,
    PRIVATE: 2,
    PROTECTED: 4,
    STATIC: 8,
    FINAL: 16,
    SYNCHRONIZED: 32,
    VOLATILE: 64,
    TRANSIENT: 128,
    NATIVE: 256,
    INTERFACE: 512,
    ABSTRACT: 1024,
    STRICT: 2048,
    BRIDGE: 64,
    VARARGS: 128,
    SYNTHETIC: 4096,
    ANNOTATION: 8192,
    ENUM: 16384,
    MANDATED: 32768,
    CLASS_MODIFIERS: 3103,
    INTERFACE_MODIFIERS: 3087,
    CONSTRUCTOR_MODIFIERS: 7,
    METHOD_MODIFIERS: 3391,
    FIELD_MODIFIERS: 223,
    PARAMETER_MODIFIERS: 16,
    ACCESS_MODIFIERS: 7,

    isPublic(mod) {
        return (mod & 1) !== 0;
    },

    isPrivate(mod) {
        return (mod & 2) !== 0;
    },

    isProtected(mod) {
        return (mod & 4) !== 0;
    },

    isStatic(mod) {
        return (mod & 8) !== 0;
    },

    isFinal(mod) {
        return (mod & 16) !== 0;
    },

    isSynchronized(mod) {
        return (mod & 32) !== 0;
    },

    isVolatile(mod) {
        return (mod & 64) !== 0;
    },

    isTransient(mod) {
        return (mod & 128) !== 0;
    },

    isNative(mod) {
        return (mod & 256) !== 0;
    },

    isInterface(mod) {
        return (mod & 512) !== 0;
    },

    isAbstract(mod) {
        return (mod & 1024) !== 0;
    },

    isStrict(mod) {
        return (mod & 2048) !== 0;
    },

    toString(mod) {
        let sj = [];
        if (this.isPublic(mod)) {
            sj.push("public");
        }

        if (this.isProtected(mod)) {
            sj.push("protected");
        }

        if (this.isPrivate(mod)) {
            sj.push("private");
        }

        if (this.isAbstract(mod)) {
            sj.push("abstract");
        }

        if (this.isStatic(mod)) {
            sj.push("static");
        }

        if (this.isFinal(mod)) {
            sj.push("final");
        }

        if (this.isTransient(mod)) {
            sj.push("transient");
        }

        if (this.isVolatile(mod)) {
            sj.push("volatile");
        }

        if (this.isSynchronized(mod)) {
            sj.push("synchronized");
        }

        if (this.isNative(mod)) {
            sj.push("native");
        }

        if (this.isStrict(mod)) {
            sj.push("strictfp");
        }

        if (this.isInterface(mod)) {
            sj.push("interface");
        }

        return sj.join(' ');
    },

    isSynthetic(mod) {
        return (mod & 4096) !== 0;
    },

    isMandated(mod) {
        return (mod & '耀') !== 0;
    },

    classModifiers() {
        return 3103;
    },

    interfaceModifiers() {
        return 3087;
    },

    constructorModifiers() {
        return 7;
    },

    methodModifiers() {
        return 3391;
    },

    fieldModifiers() {
        return 223;
    },

    parameterModifiers() {
        return 16;
    }
}

/**
 * Represents the different types of relationships between classes.
 * @typedef {Map<RELATIONSHIP,Map<TypeIdentifier, Set<TypeIdentifier>>>} RelationshipGraph
 * @public
 */
/**
 * This constant is used to map relationships between classes to optimize the search algorithm.
 * This is used by relationship_graph.js
 * Each key in the object is a relationship type, and the value is a map of ids to a set of other ids.
 *
 * @type {RelationshipGraph}
 * @public
 */
const RELATIONSHIP_GRAPH = new Map();

/**
 * This constant is used to map relationships between classes to optimize the search algorithm.
 * @enum {string} RELATIONSHIP
 * @public
 */
const RELATIONSHIP = {
    "INHERITS": "INHERITS",
    "SUPER_CLASS": "SUPER_CLASS",
    "INHERITED_BY": "INHERITED_BY",
    "REFERENCES": "REFERENCES",
    "REFERENCED_BY": "REFERENCED_BY",
    "FIELD_TYPE": "FIELD_TYPE",
    "METHOD_RETURN_TYPE": "METHOD_RETURN_TYPE",
    "PARAMETER_TYPE": "PARAMETER_TYPE",
    "CONSTRUCTOR_PARAMETER_TYPE": "CONSTRUCTOR_PARAMETER_TYPE",
    "METHOD_PARAMETER_TYPE": "METHOD_PARAMETER_TYPE",
    "TYPE_VARIABLE_OF": "TYPE_VARIABLE_OF",
    "COMPONENT_OF": "COMPONENT_OF",
    "DECLARING_CLASS": "DECLARING_CLASS",
    "DECLARES_CLASS": "DECLARES_CLASS",
    "ENCLOSING_CLASS": "ENCLOSING_CLASS",
    "ENCLOSES_CLASS": "ENCLOSES_CLASS",
    "PARAMETERIZED_VARIANT": "PARAMETERIZED_VARIANT",
    "RAW_TYPE": "RAW_TYPE",
    "OWNER_TYPE": "OWNER_TYPE",
    "TYPE_VARIABLE_BOUNDS": "TYPE_VARIABLE_BOUNDS",
    "LOWER_BOUND": "LOWER_BOUND",
    "UPPER_BOUND": "UPPER_BOUND",
    "BOUNDED_WITHIN": "BOUNDED_WITHIN",
}

const TASKS = {
    "OPTIMIZE": "optimize",
    "SEARCH": "search"
};

let _last_filter = null;
let _last_search_parameters = null;

const GLOBAL_DATA = {};

let PageableSortableTable = null;

/**
 * This is a global DecodedURL object for the latest hash.
 * @type {URLData}
 */
let CURRENT_URL;

let name_parameters;
let signature_parameters;
let cachedGenericDefinition;