package pie.ilikepiefoo.kubejsoffline.html.page;

import com.google.gson.Gson;
import pie.ilikepiefoo.kubejsoffline.html.tag.CustomAssetTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.collection.JSONDataTag;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.json.BindingsJSON;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public class IndexPage extends HTMLFile {

    public IndexPage(final Gson gson) {
        this.HEADER_TAG.add(new CustomAssetTag("title", "html/title.txt"));
        this.HEADER_TAG.add(new CustomAssetTag("style", "html/css/styling.css"));
        this.HEADER_TAG.add(new JSONDataTag("DATA", CollectionGroup.INSTANCE.toJSON(), gson));
        this.HEADER_TAG.add(new JSONDataTag("BINDINGS", BindingsJSON.get(), gson));
        this.HEADER_TAG.add(new JSONDataTag("PROPERTY", JSONProperty.createTranslation(), gson));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/projectinfo.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/modifier.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/globalsettings.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/stickytools.js"));
//		HEADER_TAG.add(new CustomAssetTag("script","html/js/debugtools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/compressiontools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/packagetools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/classdatatools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createhtmltools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createsignaturetools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createhomepagetools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createtabletools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/sortingtools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/searchingtools.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/indexpagelogic.js"));
        this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/contextmenu.js"));
    }
}
