package pie.ilikepiefoo.kubejsoffline.html.page;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;
import pie.ilikepiefoo.kubejsoffline.html.tag.CustomAssetTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.collection.JSONDataTag;
import pie.ilikepiefoo.kubejsoffline.util.DocumentationBridge;
import pie.ilikepiefoo.kubejsoffline.util.RelationType;
import pie.ilikepiefoo.kubejsoffline.util.json.BindingsJSON;
import pie.ilikepiefoo.kubejsoffline.util.json.ClassJSONManager;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public class IndexPage extends HTMLFile {

	public IndexPage(final Gson gson, final DocumentationBridge bridge) {
		this.HEADER_TAG.add(new CustomAssetTag("title", "html/title.txt", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("style", "html/css/styling.css", bridge));
		final JsonObject object = new JsonObject();
		for (final Class<?> subject : KubeJSOffline.HELPER.getEventClasses()) {
			object.add(subject.getName(), ClassJSONManager.getInstance().findAllRelationsOf(subject, RelationType.SUPER_CLASS_OF, RelationType.IMPLEMENTATION_OF, RelationType.COMPONENT_OF));
		}
		ClassJSONManager.getInstance().filterRelationshipData();
		this.HEADER_TAG.add(new JSONDataTag("DATA", ClassJSONManager.getInstance().getTypeData(), gson));
		this.HEADER_TAG.add(new JSONDataTag("EVENTS", object, gson));
		this.HEADER_TAG.add(new JSONDataTag("RELATIONS", RelationType.getRelationTypeData(), gson));
		this.HEADER_TAG.add(new JSONDataTag("BINDINGS", BindingsJSON.get(), gson));
		this.HEADER_TAG.add(new JSONDataTag("PROPERTY", JSONProperty.createTranslation(), gson));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/projectinfo.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/modifier.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/globalsettings.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/stickytools.js", bridge));
//		HEADER_TAG.add(new CustomAssetTag("script","html/js/debugtools.js"));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/classdatatools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createhtmltools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createsignaturetools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createhomepagetools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/createtabletools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/sortingtools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/searchingtools.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/indexpagelogic.js", bridge));
		this.HEADER_TAG.add(new CustomAssetTag("script", "html/js/contextmenu.js", bridge));
	}
}
