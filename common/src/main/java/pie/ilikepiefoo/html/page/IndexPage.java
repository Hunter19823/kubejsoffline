package pie.ilikepiefoo.html.page;

import dev.latvian.mods.kubejs.script.ScriptType;
import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;

import java.util.Map;
import java.util.Set;

public class IndexPage extends HTMLFile {
	private final Map<String, Class<?>> bindings;
	private final Map<String, Set<ScriptType>> scriptSpecificBindings;
	private final Class<?>[] eventClasses;

	public IndexPage(Map<String, Class<?>> bindings, Map<String, Set<ScriptType>> scriptSpecificBindings, Class<?>[] eventClasses) {
		this.bindings = bindings;
		this.scriptSpecificBindings = scriptSpecificBindings;
		this.eventClasses = eventClasses;
		HEADER_TAG.add(new CustomTag("style").setContent(ClassPage.CSS));
		generateBody();
		ClassPage.addHrefFixingScript(BODY_TAG, "/index.html");
	}

	private void generateBody() {
		ClassPage.addTitle(BODY_TAG);

		// Add the table of all events.
		var temp = generateEventTable();
		if(temp != null)
			BODY_TAG.br().add(temp);

		// Add the table of all bindings.
		temp = generateBindingTable();
		if(temp != null)
			BODY_TAG.br().add(temp);
	}

	private Tag<?> generateEventTable() {
		if(eventClasses == null || eventClasses.length == 0)
			return null;
		var table = new CustomTag("table");
		var body = table.add(new CustomTag("tbody"));
		var row = body.add(new CustomTag("tr"));
		row.add(new CustomTag("th").setContent("Events"));
		row.add(new CustomTag("th").setContent("Type"));

		for (Class<?> eventClass : eventClasses) {
			row = body.add(new CustomTag("tr"));
			row.add(new CustomTag("td"))
					.add(ClassPage.getShortSignature(eventClass));
			row.add(new CustomTag("td"))
					.add(ClassPage.getFullSignature(eventClass));
		}

		return table;
	}

	private Tag<?> generateBindingTable() {
		if(bindings == null || bindings.size() == 0)
			return null;
		var table = new CustomTag("table");
		var body = table.add(new CustomTag("tbody"));
		var row = body.add(new CustomTag("tr"));
		row.add(new CustomTag("th").setContent("Bindings"));
		row.add(new CustomTag("th").setContent("Type"));
		row.add(new CustomTag("th").setContent("Scope"));
		for(Map.Entry<String, Class<?>> entry : bindings.entrySet()){
			row = body.add(new CustomTag("tr"));
			row.add(new CustomTag("td"))
					.add(
							ClassPage.wrapInLink(
									ClassPage.getURL(entry.getValue()),
									new CustomTag("span").setContent(entry.getKey())
							)
					);

			row.add(new CustomTag("td"))
					.add(ClassPage.getFullSignature(entry.getValue()));

			StringBuilder out = new StringBuilder();
			for(int i = 0; i < scriptSpecificBindings.get(entry.getKey()).size(); i++){
				out.append(scriptSpecificBindings.get(entry.getKey()).toArray()[i]);
				if(i != scriptSpecificBindings.get(entry.getKey()).size()-1)
					out.append(", ");
			}
			row.add(new CustomTag("td"))
					.add(out.toString());
		}

		return table;
	}
}
