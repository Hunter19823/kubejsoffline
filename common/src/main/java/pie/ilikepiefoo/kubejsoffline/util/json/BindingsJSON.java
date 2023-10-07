package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.script.ScriptType;
import pie.ilikepiefoo.kubejsoffline.FakeBindingsEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class BindingsJSON {
    @Nonnull
    public static JsonObject get() {
        var obj = new JsonObject();
        for (Map.Entry<ScriptType, JsonObject> entry : FakeBindingsEvent.bindingsJSON.entrySet()) {
            obj.add(entry.getKey().name(), entry.getValue());
        }
        return obj;
    }
}
