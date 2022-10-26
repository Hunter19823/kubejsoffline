package pie.ilikepiefoo.html.page;

import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import pie.ilikepiefoo.KubeJSOffline;
import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.functional.ScriptTag;
import pie.ilikepiefoo.html.tag.text.HeaderTag;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassPage extends HTMLFile {
	public static final String LOGO = "https://aurora.latvian.dev/logo.gif";
	private final Class<?> target;

	public ClassPage(Class<?> target) {
		this.target = target;
		addCSS();
		generateBody();
		addHrefFixingScript();
	}

	public static String getURL(Class<?> type) {
		if(type == null)
			return "";
		while(type.isArray())
			type = type.getComponentType();
		if(type.isPrimitive())
			return "";
		return "/types/"+type.getName().replace('.','/')+".html";
	}

	private void generateBody() {
		Tag<?> body = BODY_TAG;
		// Add logo;
		body.add(getLogoImage());
		body.br();
		// Add Mod Name
		body.add(wrapInLink("/index.html", new HeaderTag(1).setContent(KubeJSOffline.MOD_NAME)));
		body.br();
		// Add Class Name and Access Modifiers
		var temp = new HeaderTag(2);
		temp.add(getAccessModifiers(target));
		temp.add(getFullSignature(target));
		// TODO: Wrap in try catch
		var super_class = (target.getGenericSuperclass() == null) ? target.getSuperclass() : target.getGenericSuperclass();
		if(super_class != null) {
			temp.br();
			temp.add("extends ");
			temp.br();
			temp.add(getFullSignature(super_class));
		}
		// TODO: Wrap in try catch
		var interfaces = (target.getGenericInterfaces().length == 0) ? target.getInterfaces() : target.getGenericInterfaces();
		if(interfaces.length != 0) {
			temp.br();
			temp.add("implements ");
			for(int i = 0; i < interfaces.length; i++) {
				temp.br();
				temp.add(getFullSignature(interfaces[i]));
				if(i != interfaces.length - 1)
					temp.add(", ");
			}
		}
		body.add(temp);

		// Add Innerclass Table
		body.br();
		body.add(getInnerClassTable(target.getClasses()));

		// Add Class Fields
		body.br();
		body.add(getFieldTable(target.getFields()));

		// Add Class Methods
		body.br();
		body.add(getMethodTable(target.getMethods()));
	}

	private Tag<?> getInnerClassTable(Class<?>[] nestMembers) {
		return new CustomTag("span").setContent("WIP");
	}

	private Tag<?> getMethodTable(Method[] methods) {
		return new CustomTag("span").setContent("WIP");
	}

	private Tag<?> getFieldTable(Field[] fields) {
		var out = new CustomTag("table");
		var tableBody = new CustomTag("tbody");
		out.add(tableBody);
		var header = new CustomTag("tr");
		header.add(new CustomTag("th").setContent("Fields"));
		header.add(new CustomTag("th").setContent("Type"));
		header.add(new CustomTag("th").setContent("Modifiers"));
		tableBody.add(header);

		for(Field field : fields) {
			var row = new CustomTag("tr");
			String possibleName = RemappingHelper.getMinecraftRemapper().remapField(target, field, field.getName());
			if(possibleName == null || possibleName.isBlank())
				possibleName = field.getName();
			row.add(new CustomTag("td").add(possibleName));
			row.add(new CustomTag("td").add(getFullSignature(field.getGenericType())));
			row.add(new CustomTag("td").setContent(""+field.getModifiers()));
			tableBody.add(row);
		}
		out.add(header);

		return out;
	}

	private String getAccessModifiers(Class<?> target) {
		return Modifier.toString(target.getModifiers());
	}

	public static Tag<?> getFullSignature(Type type) {
		Tag<?> out;
		if(type == null)
			return new CustomTag("span").setContent("null");
		// Get a tag that contains the full signature of the type.
		// Ex: java.util.List<java.lang.String>
		if(type instanceof ParameterizedType ptype) {
			out = getFullSignature(ptype.getRawType());
			var typeArgs = ptype.getActualTypeArguments();
			out.add("<");
			for (int i = 0; i < typeArgs.length; i++) {
				out.add(getFullSignature(typeArgs[i]));
				if(i != typeArgs.length-1)
					out.add(", ");
			}
			out.add(">");
		} else if (type instanceof Class<?> clazz) {
			var url = getURL(clazz);
			if(url.isEmpty())
				out = new CustomTag("span").setContent(clazz.getSimpleName());
			else
				out = wrapInLink(url, new CustomTag("span").setContent(clazz.getSimpleName()));
		} else {
			out = new CustomTag("span").setContent(type.getTypeName());
		}

		return out;
	}

	public static Tag<?> getShortSignature(Type type) {
		Tag<?> out;
		// Get a tag that contains the short signature of the type.
		// Ex: List<String>
		if(type instanceof ParameterizedType ptype) {
			out = getShortSignature(ptype.getRawType());
			var typeArgs = ptype.getActualTypeArguments();
			out.add("<");
			for (int i = 0; i < typeArgs.length; i++) {
				out.add(getShortSignature(typeArgs[i]));
				if(i != typeArgs.length-1)
					out.add(", ");
			}
			out.add(">");
		} else if (type instanceof Class<?> clazz) {
			var url = getURL(clazz);
			if(url.isEmpty())
				out = new CustomTag("span").setContent(clazz.getSimpleName());
			else
				out = wrapInLink(url, new CustomTag("span").setContent(clazz.getSimpleName()));
		} else {
			out = new CustomTag("span").setContent(type.getTypeName());
		}

		return out;
	}

	private static Tag<?> getLogoImage() {
		return wrapInLink("/index.html",new CustomTag("img").setClass("logo").src(LOGO));
	}

	private static Tag<?> wrapInLink(String url, Tag<?> tag) {
		var out = new CustomTag("a").href(url).setAttributeString("target","_blank");
		out.add(tag);
		return out;
	}

	private void addCSS() {
		Tag<?> tag = HEADER_TAG;
//		tag.add(new CustomTag("link").setAttributeString("rel", "stylesheet").setAttributeString("href", "https://aurora.latvian.dev/style.css"));
		// Styling credit goes to the LatvianModder
		tag.add(new CustomTag("style").setContent(CSS));
	}

	private void addHrefFixingScript() {
		Tag<?> tag = BODY_TAG;
		tag.add(new ScriptTag().setContent("const FILE_PATHNAME = '"+getURL(target)+"';\n" +
				"\n" +
				"const url = `${window.location.protocol}//${window.location.host}${window.location.pathname.slice(0,-FILE_PATHNAME.length)}`\n" +
				"\n" +
				"function fixHREF(tag) {\n" +
				"\ttag.href = url+tag.getAttribute(\"href\");\n" +
				"}\n" +
				"function fixHREFofTag(tagName){\n" +
				"\tlet tags = document.getElementsByTagName(tagName);\n" +
				"\tfor(let i=0; i<tags.length; i++) fixHREF(tags[i]);\n" +
				"}\n" +
				"fixHREFofTag('a');\n" +
				"fixHREFofTag('img');"));
	}

	private static final String CSS = "/* A direct download of https://aurora.latvian.dev/style.css \n All Credit goes to it's author LatvianModder.*/" +
			"/* https://github.com/lonekorean/gist-syntax-themes */\n" +
			"@import url('https://fonts.googleapis.com/css?family=Open+Sans');\n" +
			"\n" +
			"html {\n" +
			"    background-color: #201c21;\n" +
			"}\n" +
			"\n" +
			"body {\n" +
			"    margin: 20px;\n" +
			"    color: #afa0ab;\n" +
			"    font: 16px 'Open Sans', sans-serif;\n" +
			"    font-family: Open Sans, Arial;\n" +
			"    font-size: 16px;\n" +
			"    margin: 2em auto;\n" +
			"    max-width: 800px;\n" +
			"    padding: 1em;\n" +
			"    line-height: 1.4;\n" +
			"    text-align: justify;\n" +
			"}\n" +
			"\n" +
			"a {\n" +
			"    color: #4791b1;\n" +
			"    text-decoration: none;\n" +
			"}\n" +
			"\n" +
			"p {\n" +
			"    margin: 0.2em 0;\n" +
			"}\n" +
			"\n" +
			"a:visited {\n" +
			"    color: #4791b1;\n" +
			"}\n" +
			"\n" +
			".type, .type:visited {\n" +
			"    color: #c13479;\n" +
			"}\n" +
			"\n" +
			"ul li img {\n" +
			"    height: 1em;\n" +
			"}\n" +
			"\n" +
			"ul,\n" +
			"ol {\n" +
			"    padding-left: 1em;\n" +
			"}\n" +
			"\n" +
			"blockquote {\n" +
			"    color: #456;\n" +
			"    margin-left: 0;\n" +
			"    margin-top: 2em;\n" +
			"    margin-bottom: 2em;\n" +
			"}\n" +
			"\n" +
			"blockquote span {\n" +
			"    float: left;\n" +
			"    margin-left: 1rem;\n" +
			"    padding-top: 1rem;\n" +
			"}\n" +
			"\n" +
			"blockquote author {\n" +
			"    display: block;\n" +
			"    clear: both;\n" +
			"    font-size: .6em;\n" +
			"    margin-left: 2.4rem;\n" +
			"    font-style: oblique;\n" +
			"}\n" +
			"\n" +
			"blockquote author:before {\n" +
			"    content: \"- \";\n" +
			"    margin-right: 1em;\n" +
			"}\n" +
			"\n" +
			"blockquote:before {\n" +
			"    font-family: Times New Roman, Times, Arial;\n" +
			"    color: #666;\n" +
			"    content: open-quote;\n" +
			"    font-size: 2.2em;\n" +
			"    font-weight: 600;\n" +
			"    float: left;\n" +
			"    margin-top: 0;\n" +
			"    margin-right: .2rem;\n" +
			"    width: 1.2rem;\n" +
			"}\n" +
			"\n" +
			"blockquote:after {\n" +
			"    content: \"\";\n" +
			"    display: block;\n" +
			"    clear: both;\n" +
			"}\n" +
			"\n" +
			"@media screen and (max-width:500px) {\n" +
			"    body {\n" +
			"        text-align: left;\n" +
			"    }\n" +
			"\n" +
			"    div.fancyPositioning div.picture-left,\n" +
			"    div.fancyPositioning div.tleft {\n" +
			"        float: none;\n" +
			"        width: inherit;\n" +
			"    }\n" +
			"\n" +
			"    blockquote span {\n" +
			"        width: 80%;\n" +
			"    }\n" +
			"\n" +
			"    blockquote author {\n" +
			"        padding-top: 1em;\n" +
			"        width: 80%;\n" +
			"        margin-left: 15%;\n" +
			"    }\n" +
			"\n" +
			"    blockquote author:before {\n" +
			"        content: \"\";\n" +
			"        margin-right: inherit;\n" +
			"    }\n" +
			"}\n" +
			"\n" +
			"span.yes {\n" +
			"    color: #26b36c;\n" +
			"}\n" +
			"\n" +
			"span.no {\n" +
			"    color: #ab495f;\n" +
			"}\n" +
			"\n" +
			"span.other {\n" +
			"    color: #42a3ff;\n" +
			"}\n" +
			"\n" +
			".error {\n" +
			"    color: #ce3434;\n" +
			"}\n" +
			"\n" +
			"table {\n" +
			"    font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;\n" +
			"    border-collapse: collapse;\n" +
			"    width: 100%;\n" +
			"    text-align: left;\n" +
			"}\n" +
			"\n" +
			"table td,\n" +
			"table th {\n" +
			"    border: 1px solid #1f1414;\n" +
			"    padding: 8px;\n" +
			"}\n" +
			"\n" +
			"table tr:nth-child(even) {\n" +
			"    background-color: #00000024;\n" +
			"}\n" +
			"\n" +
			"table tr:hover {\n" +
			"    background-color: #424250;\n" +
			"}\n" +
			"\n" +
			"table th {\n" +
			"    padding-top: 12px;\n" +
			"    padding-bottom: 12px;\n" +
			"    text-align: left;\n" +
			"    background-color: #383944;\n" +
			"    color: #ddd;\n" +
			"}\n" +
			"\n" +
			"pre {\n" +
			"    background-color: #f2f2f2;\n" +
			"    padding: 0.3em;\n" +
			"    overflow-x: scroll;\n" +
			"}\n" +
			"\n" +
			"img.tooltip, div.tooltip {\n" +
			"    display: inline-block;\n" +
			"}\n" +
			"\n" +
			".tooltip .tooltiptext {\n" +
			"    visibility: hidden;\n" +
			"    max-width: 20em;\n" +
			"    background-color: #2d2a2a;\n" +
			"    color: #ead8da;\n" +
			"    border-radius: 6px;\n" +
			"    border: #101010 1px solid;\n" +
			"    padding: 0.7em 1em;\n" +
			"    position: absolute;\n" +
			"    z-index: 1;\n" +
			"}\n" +
			"\n" +
			".tooltip:hover .tooltiptext {\n" +
			"    visibility: visible;\n" +
			"}\n" +
			"\n" +
			"img.icon {\n" +
			"    height: 1em;\n" +
			"    padding: 0;\n" +
			"    margin: 0 0.3em 0 0;\n" +
			"    display: inline-block;\n" +
			"    vertical-align: middle;\n" +
			"}\n" +
			"\n" +
			".center-text {\n" +
			"    text-align: center;\n" +
			"}";
}
