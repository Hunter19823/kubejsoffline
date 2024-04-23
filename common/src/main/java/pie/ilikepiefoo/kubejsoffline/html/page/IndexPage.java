package pie.ilikepiefoo.kubejsoffline.html.page;

import com.google.gson.Gson;
import pie.ilikepiefoo.kubejsoffline.html.tag.CustomAssetTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.collection.JSONDataTag;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.DocumentationBridge;
import pie.ilikepiefoo.kubejsoffline.util.RelationType;
import pie.ilikepiefoo.kubejsoffline.util.json.BindingsJSON;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public class IndexPage extends HTMLFile {

    public IndexPage(final Gson gson, final DocumentationBridge documentationBridge) {
        this.HEADER_TAG.add(new CustomAssetTag("title", "html/title.txt", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("style", "html/css/styling.css", documentationBridge));
        this.HEADER_TAG.add(new JSONDataTag("DATA", CollectionGroup.INSTANCE.toJSON(), gson).id("data"));
        this.HEADER_TAG.add(new JSONDataTag("BINDINGS", BindingsJSON.get(), gson).id("bindings"));
        this.HEADER_TAG.add(new JSONDataTag("RELATIONS", RelationType.getRelationTypeData(), gson).id("relationships"));
        this.HEADER_TAG.add(new JSONDataTag("PROPERTY", JSONProperty.createTranslation(), gson).id("properties"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/indexingworker.js", documentationBridge).id("worker-script"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/constants.js", documentationBridge).id("constants"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/class_data_documentation.js", documentationBridge).id("class-documentation-tools"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/relationship_graph.js", documentationBridge).id("relationship-graphs"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/stickytools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/pagination_tools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/compressiontools.js", documentationBridge).id("compression-tools"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/classdatatools.js", documentationBridge).id("class-data-tools"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createhtmltools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createsignaturetools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createhomepagetools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createtabletools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/sortingtools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/searchingtools.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/searching_and_sorting_constants.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/indexpagelogic.js", documentationBridge));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/contextmenu.js", documentationBridge));
    }
}
