package pie.ilikepiefoo.util.json;

import com.google.gson.JsonObject;

import java.lang.reflect.Modifier;

public class ModifierJSON {
	public static JsonObject of(int modifer) {
		JsonObject out = new JsonObject();
		out.addProperty("public", Modifier.isPublic(modifer));
		out.addProperty("private", Modifier.isPrivate(modifer));
		out.addProperty("protected", Modifier.isProtected(modifer));
		out.addProperty("static", Modifier.isStatic(modifer));
		out.addProperty("final", Modifier.isFinal(modifer));
		out.addProperty("synchronized", Modifier.isSynchronized(modifer));
		out.addProperty("volatile", Modifier.isVolatile(modifer));
		out.addProperty("transient", Modifier.isTransient(modifer));
		out.addProperty("native", Modifier.isNative(modifer));
		out.addProperty("interface", Modifier.isInterface(modifer));
		out.addProperty("abstract", Modifier.isAbstract(modifer));
		out.addProperty("strict", Modifier.isStrict(modifer));
		return out;
	}
}
