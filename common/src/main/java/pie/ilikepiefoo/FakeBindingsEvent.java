package pie.ilikepiefoo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.DynamicFunction;
import pie.ilikepiefoo.util.json.ClassJSONManager;

import java.util.EnumMap;
import java.util.Map;

public class FakeBindingsEvent extends BindingsEvent {
	public static final EnumMap<ScriptType, JsonObject> bindingsJSON = new EnumMap<>(ScriptType.class);

	public FakeBindingsEvent(FakeScriptManager manager) {
		super(manager, null, null);
	}

	@Override
	public ScriptType getType() {
		return manager.type;
	}

	@Override
	public void add(String name, Object value) {
		if(value == null)
			return;
		if(value instanceof BaseFunction || value instanceof DynamicFunction.Callback)
			return;

		var obj = bindingsJSON.computeIfAbsent(type, t -> new JsonObject());
		if(obj.has(name))
			return;
		var temp = new JsonObject();
		obj.add(name, temp);
		obj = temp;
		obj.addProperty("type", ClassJSONManager.getInstance().getTypeID(value.getClass()));
		if(value instanceof Map) {
			obj.addProperty("btype", "map");
			obj.addProperty("toString", value.toString());
			return;
		}
		if(value instanceof Number || value instanceof Boolean || value instanceof String) {
			obj.addProperty("btype", "primitive");
			obj.addProperty("toString", value.toString());
			return;
		}
		if(value instanceof Enum<?> enumValue) {
			obj.addProperty("btype", "enum");
			obj.addProperty("toString", enumValue.name());
			return;
		}
		if(value instanceof Class<?> clazz) {
			if(clazz.isEnum()){
				obj.addProperty("btype", "enum");
			}else {
				obj.addProperty("btype", "class");
			}
		}
	}

	@Override
	public void addFunction(String name, DynamicFunction.Callback callback) {
		getData(name, "callback");
	}

	@Override
	public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {
		var obj = getData(name, "callback");
		if(obj == null)
			return;
		var temp = new JsonArray();
		for(var param : types) {
			temp.add(ClassJSONManager.getInstance().getTypeID(param));
		}
		obj.add("params", temp);
	}

	@Override
	public void addFunction(String name, BaseFunction function) {
		getData(name, "function");
	}

	private JsonObject getData(String name, String type) {
		var obj = bindingsJSON.computeIfAbsent(this.type, t -> new JsonObject());
		if(obj.has(name))
			return null;
		var temp = new JsonObject();
		obj.add(name, temp);
		obj = temp;
		obj.addProperty("btype", type);
		return obj;
	}
}
