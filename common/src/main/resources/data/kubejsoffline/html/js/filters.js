/**
 * A filter that can be used to filter data
 * @template {JavaType | Constructor | Method | Field | Parameter} T The type of data to filter
 * @class
 * @public
 */
class Filter {
    /**
     * Create a new filter
     * @param {function(T): boolean} predicate - The predicate function to use for filtering
     */
    constructor(predicate) {
        this.predicate = predicate;
    }

    /**
     * Filter the given data
     * @param {T[] | T} data - The data to filter
     * @return {T[]} The filtered data
     */
    filter(data) {
        if (Array.isArray(data)) {
            return data.filter(this.predicate);
        }
        if (this.predicate(data)) {
            return [data];
        }
        return [];
    }

    /**
     * Combine this filter with another filter using a logical OR
     * @param {Filter} filter - The filter to combine with
     * @return {Filter} The combined filter
     */
    or(filter) {
        return new OrFilter([this, filter]);
    }

    /**
     * Combine this filter with another filter using a logical AND
     * @param {Filter} filter - The filter to combine with
     * @return {Filter} The combined filter
     */
    and(filter) {
        return new AndFilter([this, filter]);
    }

    /**
     * Negate this filter
     * @return {Filter} The negated filter
     */
    not() {
        return new NotFilter(this);
    }
}

/**
 * A filter that is the logical OR of the given filters
 * @extends Filter
 * @class
 * @public
 */
class OrFilter extends Filter {
    /**
     * Create a new filter that is the logical OR of the given filters
     * @param {Array<Filter>} filters - The filters to combine
     */
    constructor(filters) {
        super((data) => filters.some((filter) => filter.predicate(data)));
    }
}

/**
 * A filter that is the logical AND of the given filters
 * @extends Filter
 * @class
 * @public
 */
class AndFilter extends Filter {
    /**
     * Create a new filter that is the logical AND of the given filters
     * @param {Array<Filter>} filters - The filters to combine
     */
    constructor(filters) {
        super((data) => filters.every((filter) => filter.predicate(data)));
    }
}

/**
 * A filter that is the logical NOT of the given filter
 * @extends Filter
 * @class
 * @public
 */
class NotFilter extends Filter {
    /**
     * Create a new filter that is the logical NOT of the given filter
     * @param {Filter} filter - The filter to negate
     */
    constructor(filter) {
        super((data) => !filter.predicate(data));
    }
}

class FilterBuilder {
    constructor() {
        this.filters = [];
    }

    addFilter(filter) {
        this.filters.push(filter);
        return this;
    }

    build() {
        return new AndFilter(this.filters);
    }
}

function createAttributeFilter(attribute, value, inclusive = true, case_sensitive = false) {
    if (case_sensitive && inclusive) {
        return new Filter((data) => {
            if (data[attribute] === undefined || data[attribute] === null) {
                return false;
            }
            if (typeof data[attribute] === 'function') {
                return `${data[attribute]()}` === `${value}`;
            }
            return `${data[attribute]}` === `${value}`;
        });
    }
    if (case_sensitive && !inclusive) {
        return new Filter((data) => {
            if (data[attribute] === undefined || data[attribute] === null) {
                return true;
            }
            if (typeof data[attribute] === 'function') {
                return `${data[attribute]()}`.includes(`${value}`);
            }
            return `${data[attribute]}`.includes(`${value}`);
        });
    }
    const lowerValue = `${value}`.toLowerCase();
    if (inclusive) {
        return new Filter((data) => {
            if (data[attribute] === undefined || data[attribute] === null) {
                return false;
            }
            if (typeof data[attribute] === 'function') {
                return `${data[attribute]()}`.toLowerCase() === lowerValue;
            }
            return `${data[attribute]}`.toLowerCase() === lowerValue;
        });
    }
    return new Filter((data) => {
        if (data[attribute] === undefined || data[attribute] === null) {
            return true;
        }
        if (typeof data[attribute] === 'function') {
            return `${data[attribute]()}`.toLowerCase().includes(lowerValue);
        }
        return `${data[attribute]}`.toLowerCase().includes(lowerValue);
    });
}

function createFilterFromJSON(json, value, inclusive, exact) {
    const filterBuilder = new FilterBuilder();
    for (const [key, filterAttribute] of Object.entries(json)) {
        if (key === 'or') {
            const orFilter = new FilterBuilder();
            if (!Array.isArray(filterAttribute)) {
                orFilter.addFilter(createFilterFromJSON(filterAttribute, value, inclusive, exact).build());
            } else {
                for (const filter of filterAttribute) {
                    orFilter.addFilter(createFilterFromJSON(filter, value, inclusive, exact).build());
                }
            }
            filterBuilder.addFilter(orFilter.build());
            continue;
        }
        if (key === 'and') {
            const andFilter = new FilterBuilder();
            if (!Array.isArray(filterAttribute)) {
                andFilter.addFilter(createFilterFromJSON(filterAttribute, value, inclusive, exact).build());
            } else {
                for (const filter of filterAttribute) {
                    andFilter.addFilter(createFilterFromJSON(filter, value, inclusive, exact).build());
                }
            }
            filterBuilder.addFilter(andFilter.build());
            continue;
        }
        if (key === 'not') {
            const notFilter = new FilterBuilder();
            if (!Array.isArray(filterAttribute)) {
                notFilter.addFilter(createFilterFromJSON(filterAttribute, value, inclusive, exact).build());
            } else {
                for (const filter of filterAttribute) {
                    notFilter.addFilter(createFilterFromJSON(filter, value, inclusive, exact).build());
                }
            }
            filterBuilder.addFilter(notFilter.build().not());
        }
        if (NEW_QUERY_TERMS[key] !== undefined) {
            if (typeof NEW_QUERY_TERMS[key] === 'function') {
                filterBuilder.addFilter(NEW_QUERY_TERMS[key](value, inclusive, exact));
                continue;
            }
            filterBuilder.addFilter(createFilterFromJSON(NEW_QUERY_TERMS[key], value, inclusive, exact));
            continue;
        }
        filterBuilder.addFilter(createAttributeFilter(key, value, inclusive, exact));
    }
    return filterBuilder.build();
}

/**
 * This class either creates a filter from URL parameters or serializes a filter to URL parameters
 * @class
 * @public
 */
class FilterSerializer {
    /**
     * Create a new filter serializer
     * @param {FilterBuilder} filterBuilder - The filter builder to use
     */
    constructor(filterBuilder) {
        this.filterBuilder = filterBuilder;
    }

    /**
     * Create a filter from the given URL parameters
     * @param {URLSearchParams} params - The URL parameters
     * @return {Filter} The filter
     */
    static fromURLParams(params) {
        const filterBuilder = new FilterBuilder();
        for (const [key, value] of params) {
            if (NEW_QUERY_TERMS[key] !== undefined) {
                filterBuilder.addFilter(createFilterFromJSON(NEW_QUERY_TERMS[key], value, FilterSerializer.isInclusive(params), FilterSerializer.isCaseSensitive(params)));
            }
        }
        return filterBuilder.build();
    }

    /**
     * Check if the given URL parameters are inclusive
     * @param {URLSearchParams} params - The URL parameters
     * @returns {boolean} True if the parameters are inclusive
     */
    static isInclusive(params) {
        // Default to inclusive
        if (params.has('inclusive')) return params.get('inclusive') === 'true';
        return true;
    }

    /**
     * Check if the given URL parameters are case sensitive
     * @param {URLSearchParams} params - The URL parameters
     * @returns {boolean} True if the parameters are case sensitive
     */
    static isCaseSensitive(params) {
        // Default to case-insensitive
        if (params.has('case_sensitive')) return params.get('case_sensitive') === 'true';
        if (params.has('exact')) return params.get('exact') === 'true';
        return false;
    }
}

