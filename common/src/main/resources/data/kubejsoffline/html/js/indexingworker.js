self.onmessage = function (e) {
    optimizeDataSearch().then(r => {
        postMessage({data: DATA, cache: LOOK_UP_CACHE, RELATIONSHIP_GRAPH: getRelationshipGraphAsJSON(RELATIONSHIP_GRAPH)});
    });
}