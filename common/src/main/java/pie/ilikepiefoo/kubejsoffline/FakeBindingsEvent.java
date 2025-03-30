package pie.ilikepiefoo.kubejsoffline;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
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
    private ScriptType scriptType;

    public FakeBindingsEvent() {
        super(null, null);
    }

    @Override
    public Iterable<Binding> getBindings() {
        for (ScriptType type : ScriptType.values()) {
            setType(type);
            for (var plugin : KubeJSPlugins.getAll()) {
                plugin.registerBindings(this);
            }
            KubeJSPlugins.addSidedBindings(this);
        }
        return BINDING_MAP.values().stream().map(SimpleBinding.Builder::build).collect(Collectors.toList());
    }    @Override
    public ScriptType getType() {
        return this.scriptType;
    }

    public void setType(ScriptType scriptType) {
        this.scriptType = scriptType;
    }

    @Override
    public void add(String name, Object value) {
        if (name == null || value == null) {
            return;
        }
        try {
            if (value instanceof EventGroupWrapper eventGroup) {
                List<EventHandler> eventHandlers = eventGroup.keySet().stream().map(eventGroup::get).filter(event -> event instanceof EventHandler).map(event -> (EventHandler) event).toList();
                for (EventHandler handler : eventHandlers) {
                    String uniqueName = handler.extra == null ? String.format("%s.%s(%s)", name, handler.name, handler.eventType.get().getSimpleName()) : String.format("%s.%s(%s, %s)", name, handler.name, handler.eventType.get().getSimpleName(), handler.extra.describeType == null ? "???" : handler.extra.describeType.apply(DescriptionContext.DEFAULT));
                    if (BINDING_MAP.containsKey(uniqueName)) {
                        BINDING_MAP.get(uniqueName).addScope(getType().name);
                        continue;
                    }
                    SimpleBinding.Builder handlerBuilder = SimpleBinding.Builder.from(uniqueName, handler.eventType.get());
                    if (handler.extra != null) {
                        handlerBuilder.setData(handler.extra);
                    }
                    handlerBuilder.addScope(getType().name);
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
