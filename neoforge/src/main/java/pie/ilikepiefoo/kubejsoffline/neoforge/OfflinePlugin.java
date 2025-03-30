package pie.ilikepiefoo.kubejsoffline.neoforge;


import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.ScriptableObject;
import pie.ilikepiefoo.kubejsoffline.core.api.context.Binding;
import pie.ilikepiefoo.kubejsoffline.core.api.context.BindingsProvider;
import pie.ilikepiefoo.kubejsoffline.core.impl.context.SimpleBinding;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OfflinePlugin implements KubeJSPlugin, BindingsProvider {
    public static final Map<String, SimpleBinding.Builder> BINDING_MAP = new HashMap<>();
    private final EnumMap<ScriptType, Set<BindingRegistry>> REGISTRIES = new EnumMap<>(ScriptType.class);

    @Override
    public Iterable<Binding> getBindings() {
        for (Map.Entry<ScriptType, Set<BindingRegistry>> entry : REGISTRIES.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            Set<BindingRegistry> registries = entry.getValue();
            for (BindingRegistry registry : registries) {
                if (registry == null) {
                    continue;
                }
                var objectIds = ScriptableObject.getPropertyIds(registry.context(), registry.scope());
                for (Object objectId : objectIds) {
                    if (objectId == null) {
                        continue;
                    }
                    if (objectId instanceof String namedProperty) {
                        var property = ScriptableObject.getProperty(registry.scope(), namedProperty, registry.context());
                        addBinding(namedProperty, property, registry.context().getType());
                    }
                }
            }
        }
        List<Binding> result = BINDING_MAP.values().stream().map(SimpleBinding.Builder::build).collect(Collectors.toList());
        BINDING_MAP.clear();
        REGISTRIES.clear();
        return result;
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

    @Override
    public void registerBindings(BindingRegistry bindings) {
        REGISTRIES.computeIfAbsent(bindings.context().getType(), t -> new HashSet<>());
        REGISTRIES.get(bindings.context().getType()).add(bindings);
    }

}
