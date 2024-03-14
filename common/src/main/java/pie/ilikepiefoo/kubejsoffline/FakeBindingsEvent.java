package pie.ilikepiefoo.kubejsoffline;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.DynamicFunction;

import java.util.EnumMap;

public class FakeBindingsEvent extends BindingsEvent {
    public static final EnumMap<ScriptType, JsonObject> bindingsJSON = new EnumMap<>(ScriptType.class);

    public FakeBindingsEvent(FakeScriptManager manager) {
        super(manager, null);
    }

    @Override
    public ScriptType getType() {
        return manager.type;
    }

    @Override
    public void add(String name, Object value) {
//        if (value == null) {
//            return;
//        }
//        if (value instanceof BaseFunction || value instanceof DynamicFunction.Callback) {
//            return;
//        }
//
//        var obj = bindingsJSON.computeIfAbsent(type, t -> new JsonObject());
//        if (obj.has(name)) {
//            return;
//        }
//        var temp = new JsonObject();
//        obj.add(name, temp);
//        obj = temp;
//        obj.addProperty(JSONProperty.TYPE_IDENTIFIER.jsName, ClassJSONManager.getInstance().getTypeID(value.getClass()));
//        if (value instanceof Map) {
//            obj.addProperty(JSONProperty.BINDING_TYPE.jsName, JSONProperty.BINDING_TYPE_MAP.jsName);
//            obj.addProperty(JSONProperty.BINDING_STRING.jsName, value.toString());
//            return;
//        }
//        if (value instanceof Number || value instanceof Boolean || value instanceof String) {
//            obj.addProperty(JSONProperty.BINDING_TYPE.jsName, JSONProperty.BINDING_TYPE_PRIMITIVE.jsName);
//            obj.addProperty(JSONProperty.BINDING_STRING.jsName, value.toString());
//            return;
//        }
//        if (value instanceof Enum<?> enumValue) {
//            obj.addProperty(JSONProperty.BINDING_TYPE.jsName, JSONProperty.BINDING_TYPE_ENUM.jsName);
//            obj.addProperty(JSONProperty.BINDING_STRING.jsName, enumValue.name());
//            return;
//        }
//        if (value instanceof Class<?> clazz) {
//            if (clazz.isEnum()) {
//                obj.addProperty(JSONProperty.BINDING_TYPE.jsName, JSONProperty.BINDING_TYPE_ENUM.jsName);
//            } else {
//                obj.addProperty(JSONProperty.BINDING_TYPE.jsName, JSONProperty.BINDING_TYPE_CLASS.jsName);
//            }
//        }
    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback) {
//        getData(name, JSONProperty.BINDING_FUNCTION.jsName);
    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {
//        var obj = getData(name, JSONProperty.BINDING_FUNCTION.jsName);
//        if (obj == null) {
//            return;
//        }
//        var temp = new JsonArray();
//        for (var param : types) {
//            temp.add(ClassJSONManager.getInstance().getTypeID(param));
//        }
//        obj.add(JSONProperty.PARAMETERS.jsName, temp);
    }

    @Override
    public void addFunction(String name, BaseFunction function) {
//        getData(name, JSONProperty.BINDING_FUNCTION.jsName);
    }

//    private JsonObject getData(String name, String type) {
//        var obj = bindingsJSON.computeIfAbsent(this.type, t -> new JsonObject());
//        if (obj.has(name)) {
//            return null;
//        }
//        var temp = new JsonObject();
//        obj.add(name, temp);
//        obj = temp;
//        obj.addProperty(JSONProperty.BINDING_TYPE.jsName, type);
//        return obj;
//    }
}
