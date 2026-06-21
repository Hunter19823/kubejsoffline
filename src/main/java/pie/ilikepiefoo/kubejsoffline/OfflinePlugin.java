package pie.ilikepiefoo.kubejsoffline;


import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.NativeJavaObject;
import pie.ilikepiefoo.kubejsoffline.core.api.context.Binding;
import pie.ilikepiefoo.kubejsoffline.core.api.context.BindingsProvider;
import pie.ilikepiefoo.kubejsoffline.core.impl.context.SimpleBinding;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OfflinePlugin implements KubeJSPlugin, BindingsProvider {
    public static final Map<String, SimpleBinding.Builder> BINDING_MAP = new HashMap<>();
    public static final Map<ScriptType, List<Map.Entry<String, Object>>> BINDINGS = Collections.synchronizedMap(new EnumMap<>(ScriptType.class));

    @Override
    public Iterable<Binding> getBindings() {
        for (var scriptBasedBindings : BINDINGS.entrySet()) {
            for (var binding : scriptBasedBindings.getValue()) {
                if (binding == null) {
                    continue;
                }
                var type = binding.getKey();
                var value = binding.getValue();
                if (value == null) {
                    continue;
                }
                addBinding(type, value, scriptBasedBindings.getKey());
            }
        }
        return BINDING_MAP.values().stream().map(SimpleBinding.Builder::build).collect(Collectors.toList());
    }

    public void addBinding(String name, Object value, ScriptType scriptType) {
        if (name == null || value == null) {
            return;
        }
        if (value instanceof NativeJavaObject nativeJavaObject) {
            value = nativeJavaObject.unwrap();
        }
        try {
            if (value instanceof EventGroupWrapper eventGroup) {
                List<dev.latvian.mods.kubejs.event.EventHandler> eventHandlers =
                        eventGroup
                                .keySet()
                                .stream()
                                .map(eventGroup::get)
                                .filter(event -> event instanceof dev.latvian.mods.kubejs.event.EventHandler)
                                .map(event -> (dev.latvian.mods.kubejs.event.EventHandler) event)
                                .toList();
                for (EventHandler handler : eventHandlers) {
                    String uniqueName =
                            handler.target == null ?
                                    String.format("%s.%s(%s)", name, handler.name, handler.eventType.get().getSimpleName()) :
                                    String.format(
                                            "%s.%s(%s, %s)",
                                            name,
                                            handler.name,
                                            handler.eventType.get().getSimpleName(),
                                            handler.target.describeType == null ?
                                                    "???" : handler.target.describeType.toString()
                                    );
                    if (BINDING_MAP.containsKey(uniqueName)) {
                        BINDING_MAP.get(uniqueName).addScope(scriptType.name);
                        continue;
                    }
                    SimpleBinding.Builder handlerBuilder = SimpleBinding.Builder.from(uniqueName, handler.eventType.get());
                    if (handler.target != null) {
                        handlerBuilder.setData(handler.target.type);
                    }
                    handlerBuilder.addScope(scriptType.name);
                    BINDING_MAP.put(uniqueName, handlerBuilder);
                }
            }
            getBinding(name, value).addScope(scriptType.name);
        } catch (Exception e) {
            LOG.error("Failed to add binding: {} of type {}. Error: {}", name, value.getClass().getName(), e.getMessage());
        }
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

        switch (value) {
            case Class<?> clazz -> {
                builder = SimpleBinding.Builder.from(name, (Type) value);
                if (clazz.isEnum()) {
                    builder.setData(Arrays.stream(clazz.getEnumConstants()).map(Enum.class::cast).map(Enum::name).toArray());
                }
            }
            case JsonElement jsonObject -> builder = SimpleBinding.Builder.from(name, jsonObject.getClass()).setData(jsonObject);
            case Enum<?> enumValue -> builder = SimpleBinding.Builder.from(name, enumValue.getDeclaringClass()).setData(enumValue.name());
            default -> builder = SimpleBinding.Builder.from(name, value.getClass()).setData(value);
        }

        BINDING_MAP.put(name, builder);
        return builder;
    }

}
