package pie.ilikepiefoo.html.page;

import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import pie.ilikepiefoo.KubeJSOffline;
import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.functional.ScriptTag;
import pie.ilikepiefoo.html.tag.text.HeaderTag;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
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
		Tag<?> temp = new HeaderTag(2);

		temp.add(getAccessModifiers(target));

		temp.add(getFullSignature(target));

		var super_class = safeGetSuperType();
		if(super_class != null) {
			temp.add(" extends ");
			temp.add(getFullSignature(super_class));
		}
		var interfaces = safeGetInterfaces();
		if(interfaces.length != 0) {
			temp.add(" implements ");
			for(int i = 0; i < interfaces.length; i++) {
				temp.add(getFullSignature(interfaces[i]));
				if(i != interfaces.length - 1)
					temp.add(", ");
			}
		}
		body.add(temp);

		// Add Innerclass Table
		temp = generateInnerClassTable(getInnerClasses());
		if(temp != null)
			body.br().add(temp);

		// Add Constructor Table
		temp = generateConstructorTable(getConstructors());
		if(temp != null)
			body.br().add(temp);

		// Add Fields
		temp = generateFieldTable(getFields());
		if(temp != null)
			body.br().add(temp);

		// Add Methods
		temp = generateMethodTable(getMethods());
		if(temp != null)
			body.br().add(temp);

		// TODO: Add extended classes
	}

	private Tag<?> generateInnerClassTable(Class<?>[] innerClasses) {
		if(innerClasses == null || innerClasses.length == 0)
			return null;
		Tag<?> table = new CustomTag("table");
		Tag<?> body = new CustomTag("tbody");
		table.add(body);
		Tag<?> row = new CustomTag("tr");
		body.add(row);
		row.add(new CustomTag("th").setContent("Inner Classes"));
		for(Class<?> innerClass : innerClasses) {
			row = new CustomTag("tr");
			body.add(row);
			row.add(new CustomTag("td").add(getFullSignature(innerClass)));
		}

		return table;
	}


	private Tag<?> generateConstructorTable(Constructor<?>[] constructors) {
		if(constructors == null || constructors.length == 0)
			return null;
		Tag<?> table = new CustomTag("table");
		Tag<?> body = table.add(new CustomTag("tbody"));
		Tag<?> row = body.add(new CustomTag("tr"));

		row.add(new CustomTag("th").setContent("Constructors"));
		row.add(new CustomTag("th").setContent("Modifiers"));

		for(Constructor<?> constructor : constructors) {
			row = body.add(new CustomTag("tr"));
			row.add(new CustomTag("td"))
					.add(getFullSignature(constructor));
			row.add(new CustomTag("td"))
					.add(""+constructor.getModifiers()));
		}

		return table;
	}

	private Tag<?> generateMethodTable(Method[] methods) {
		if(methods == null || methods.length == 0)
			return null;
		Tag<?> table = new CustomTag("table");
		Tag<?> body = table.add(new CustomTag("tbody"));
		Tag<?> tr = body.add(new CustomTag("tr"));
		tr.add(new CustomTag("th"))
				.add("Method");
		tr.add(new CustomTag("th"))
				.add("Return Type");
		tr.add(new CustomTag("th"))
				.add("Access Modifiers");

		for(Method method : methods) {
			tr = body.add(new CustomTag("tr"));
			tr.add(new CustomTag("td"))
					.add(getFullSignature(method, target));
			tr.add(new CustomTag("td"))
					.add(getFullSignature(safeGetType(method)));
			tr.add(new CustomTag("td"))
					.add(""+method.getModifiers());
		}

		return table;
	}

	private static Type safeGetType(Method method) {
		try {
			return method.getGenericReturnType();
		} catch (Exception e) {
			return method.getReturnType();
		}
	}

	private Tag<?> generateFieldTable(Field[] fields) {
		if(fields == null || fields.length == 0)
			return null;

		var out = new CustomTag("table");
		var tableBody = out.add(new CustomTag("tbody"));

		var header = tableBody.add(new CustomTag("tr"));
		header.add(new CustomTag("th").setContent("Fields"));
		header.add(new CustomTag("th").setContent("Type"));
		header.add(new CustomTag("th").setContent("Modifiers"));

		for (Field field : fields) {
			var row = tableBody.add(new CustomTag("tr"));
			var type = safeGetType(field);

			// Field Name
			row.add(getFullSignature(field, target));

			// Field Type
			row.add(new CustomTag("td"))
					.add(getFullSignature(type));

			// Field Modifiers
			row.add(new CustomTag("td"))
					.add("" + field.getModifiers());
		}
		out.add(header);

		return out;
	}

	private String getAccessModifiers(Class<?> target) {
		return Modifier.toString(target.getModifiers());
	}

	private Class<?>[] getInnerClasses() {
		try{
			return target.getClasses();
		} catch (Throwable e) {
			try {
				return target.getDeclaredClasses();
			}catch (Throwable e2) {
				return new Class[0];
			}
		}
	}



	private Constructor<?>[] getConstructors() {
		try {
			return target.getConstructors();
		} catch (Throwable e) {
			try {
				return target.getDeclaredConstructors();
			} catch (Throwable e2) {
				return new Constructor[0];
			}
		}
	}

	private Field[] getFields() {
		try {
			return target.getFields();
		} catch (Throwable e) {
			try {
				return target.getDeclaredFields();
			} catch (Throwable e2) {
				return new Field[0];
			}
		}
	}

	private Method[] getMethods() {
		try {
			return target.getMethods();
		} catch (Throwable e) {
			try{
				return target.getDeclaredMethods();
			} catch (Throwable e2) {
				return new Method[0];
			}
		}
	}

	private static Type safeGetType(Field field) {
		try {
			return field.getGenericType();
		} catch (Throwable e) {
			return field.getType();
		}
	}

	private static Type[] safeGetParameters(Method method) {
		try {
			return method.getGenericParameterTypes().length == 0 ? method.getParameterTypes() : method.getGenericParameterTypes();
		} catch (Throwable e) {
			return method.getParameterTypes();
		}
	}

	private static String safeGetSimpleTypeName(Type in) {
		if(in == null)
			return "null";
		if(in instanceof Class<?> type) {
			try {
				return type.getSimpleName();
			} catch (Throwable e) {
				try {
					return type.getName().substring(type.getName().lastIndexOf('.') + 1);
				} catch (Throwable e2) {
					return "null";
				}
			}
		}else {
			try {
				return in.getTypeName();
			} catch (Throwable e) {
				try {
					return in.toString();
				} catch (Throwable e2) {
					return "null";
				}
			}
		}
	}

	private Type safeGetSuperType() {
		try {
			return target.getGenericSuperclass();
		} catch (Throwable e) {
			return target.getSuperclass();
		}
	}

	private Type[] safeGetInterfaces() {
		try {
			return (target.getGenericInterfaces().length == 0) ? target.getInterfaces() : target.getGenericInterfaces();
		} catch (Throwable e) {
			return target.getInterfaces();
		}
	}

	public static Tag<?> getFullSignature(@Nonnull Field field, Class<?> source) {
		Tag<?> out = new CustomTag("td");
		out.add(Modifier.toString(field.getModifiers()));
		out.add(" ");
		out.add(getShortSignature(safeGetType(field)));
		out.add(" ");
		String possibleName = RemappingHelper.getMinecraftRemapper().remapField(source, field, field.getName());
		if (possibleName == null || possibleName.isBlank())
			possibleName = field.getName();
		out.add(possibleName);
		return out;
	}

	public static Tag<?> getFullSignature(@Nonnull Method method, Class<?> source) {
		Tag<?> out = new CustomTag("td");
		out.add(Modifier.toString(method.getModifiers()));
		out.add(" ");
		out.add(getShortSignature(method.getReturnType()));
		out.add(" ");
		String possibleName = RemappingHelper.getMinecraftRemapper().remapMethod(source, method, method.getName());
		if (possibleName == null || possibleName.isBlank())
			possibleName = method.getName();
		out.add(possibleName);
		out.add("(");
		var parameters = safeGetParameters(method);
		for (int i = 0; i < parameters.length; i++) {
			if (i != 0 && i < parameters.length-1)
				out.add(", ");
			out.add(getShortSignature(method.getGenericParameterTypes()[i]));
		}
		out.add(")");

		return out;
	}


	private Tag<?> getFullSignature(Constructor<?> constructor) {
		Tag<?> out = new CustomTag("td");
		out.add(Modifier.toString(constructor.getModifiers()));
		out.add(" ");
		out.add(getShortSignature(target));
		out.add("(");
		// TODO add constructor parameters
		var parameters = safeGetParameters(constructor);
		for (int i = 0; i < parameters.length; i++) {
			if (i != 0 && i < parameters.length-1)
				out.add(", ");
			out.add(getShortSignature(parameters[i]));
		}
		out.add(")");

		return out;
	}

	public static Tag<?> getFullSignature(Type type) {
		if(type == null)
			return new CustomTag("span").setContent("null");
		Tag<?> out;
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
				out = new CustomTag("span").setContent(safeGetSimpleTypeName(clazz));
			else
				out = wrapInLink(url, new CustomTag("span").setContent(safeGetSimpleTypeName(clazz)));
		} else {
			out = new CustomTag("span").setContent(safeGetSimpleTypeName(type));
		}

		return out;
	}

	public static Tag<?> getShortSignature(Type type) {
		if(type == null)
			return new CustomTag("span").setContent("null");
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
				out = new CustomTag("span").setContent(safeGetSimpleTypeName(clazz));
			else
				out = wrapInLink(url, new CustomTag("span").setContent(safeGetSimpleTypeName(clazz)));
		} else {
			out = new CustomTag("span").setContent(safeGetSimpleTypeName(type));
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
			"}\n" +
			".logo {\n" +
			"    height: 7em;\n" +
			"}";
}
