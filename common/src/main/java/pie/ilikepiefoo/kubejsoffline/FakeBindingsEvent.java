package pie.ilikepiefoo.kubejsoffline;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.DynamicFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.core.api.context.Binding;
import pie.ilikepiefoo.kubejsoffline.core.api.context.BindingsProvider;
import pie.ilikepiefoo.kubejsoffline.core.impl.context.SimpleBinding;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeBindingsEvent extends BindingsEvent implements BindingsProvider {
    public static final Map<String, SimpleBinding.Builder> BINDING_MAP = new HashMap<>();
    private static final Logger LOG = LogManager.getLogger();
    public ScriptType type;


    public FakeBindingsEvent() {
        this(ScriptType.STARTUP);
    }

    public FakeBindingsEvent(ScriptType scriptType) {
        super(new FakeScriptManager(scriptType), null);
    }

    @Override
    public ScriptType getType() {
        return manager.type;
    }

    @Override
    public Iterable<Binding> getBindings() {
        for (ScriptType type : ScriptType.values()) {
            FakeBindingsEvent event = new FakeBindingsEvent(type);
            event.setType(type);
            KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));
            BindingsEvent.EVENT.invoker().accept(event);
        }
        List<Binding> result = BINDING_MAP.values().stream().map(SimpleBinding.Builder::build).collect(Collectors.toList());
        BINDING_MAP.clear();
        return result;
    }

    public void setType(ScriptType type) {
        this.type = type;
    }    @Override
    public void add(String name, Object value) {
        if (name == null || value == null) {
            return;
        }
        try {
            getBinding(name, value).addScope(type.name);
        } catch (Exception e) {
            LOG.error("Failed to add binding: {} of type {}. Error: {}", name, value.getClass().getName(), e.getMessage());
        }
    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback) {
        add(name, callback);
    }

    @Override
    public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {
        add(name, types);
    }

    @Override
    public void addFunction(String name, BaseFunction function) {
        add(name, function);
    }

    public SimpleBinding.Builder getBinding(String name, Object value) {
        if (value == null || name == null) {
            return null;
        }
        if (BINDING_MAP.containsKey(name)) {
            return BINDING_MAP.get(name);
        }
        LOG.info("Binding Found: {} of type {}", name, value.getClass().getName());
        SimpleBinding.Builder builder;

        if (value instanceof Class<?> clazz) {
            builder = SimpleBinding.Builder.from(name, (Type) value);
            if (clazz.isEnum()) {
                builder.setData(Arrays.stream(clazz.getEnumConstants()).map(Enum.class::cast).map(Enum::name).toArray());
            }
        } else if (value instanceof JsonElement jsonObject) {
            builder = SimpleBinding.Builder.from(name, jsonObject.getClass()).setData(jsonObject);
        } else if (value instanceof Enum<?> enumValue) {
            builder = SimpleBinding.Builder.from(name, enumValue.getDeclaringClass()).setData(enumValue.name());
        } else {
            builder = SimpleBinding.Builder.from(name, value.getClass()).setData(value);
        }

        BINDING_MAP.put(name, builder);
        return builder;
    }



}
