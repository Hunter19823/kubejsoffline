package pie.ilikepiefoo.kubejsoffline.html.page;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;
import pie.ilikepiefoo.kubejsoffline.html.tag.CustomAssetTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.collection.JSONDataTag;
import pie.ilikepiefoo.kubejsoffline.util.RelationType;
import pie.ilikepiefoo.kubejsoffline.util.json.BindingsJSON;
import pie.ilikepiefoo.kubejsoffline.util.json.ClassJSONManager;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

public class IndexPage extends HTMLFile {

	public IndexPage(Gson gson) {
		HEADER_TAG.add(new CustomAssetTag("style","html/css/styling.css"));
		HEADER_TAG.add(new JSONDataTag("DATA", ClassJSONManager.getInstance().getTypeData(), gson));
		JsonObject object = new JsonObject();
		for(Class<?> subject : KubeJSOffline.HELPER.getEventClasses()) {
			object.add(subject.getName(), ClassJSONManager.getInstance().findAllRelationsOf(subject, RelationType.SUPER_CLASS_OF));
		}
		HEADER_TAG.add(new JSONDataTag("EVENTS", object, gson));
		HEADER_TAG.add(new JSONDataTag("RELATIONS", RelationType.getRelationTypeData(), gson));
		HEADER_TAG.add(new JSONDataTag("BINDINGS", BindingsJSON.get(), gson));
		HEADER_TAG.add(new JSONDataTag("PROPERTY", JSONProperty.createTranslation(), gson));
		HEADER_TAG.add(new CustomAssetTag("script","html/js/modifier.js"));
//		HEADER_TAG.add(new CustomAssetTag("script","html/js/debugtools.js"));
		HEADER_TAG.add(new CustomAssetTag("script","html/js/classdatatools.js"));
		HEADER_TAG.add(new CustomAssetTag("script","html/js/indexpagelogic.js"));
	}
}
