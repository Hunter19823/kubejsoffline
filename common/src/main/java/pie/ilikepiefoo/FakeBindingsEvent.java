package pie.ilikepiefoo;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.DynamicFunction;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FakeBindingsEvent extends BindingsEvent {
	public static final Map<String, Map<Class<?>, Set<ScriptType>>> bindings = new TreeMap<>();

	public FakeBindingsEvent(FakeScriptManager manager) {
		super(manager, null, null);
	}

	@Override
	public ScriptType getType() {
		return manager.type;
	}

	@Override
	public void add(String name, Object value) {
		bindings.computeIfAbsent(name, k -> new HashMap<>()).computeIfAbsent(value.getClass(), k -> EnumSet.noneOf(ScriptType.class)).add(getType());
	}

	@Override
	public void addFunction(String name, DynamicFunction.Callback callback) {
		bindings.computeIfAbsent(name, k -> new HashMap<>()).computeIfAbsent(DynamicFunction.Callback.class, k -> EnumSet.noneOf(ScriptType.class)).add(getType());
	}

	@Override
	public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {
		bindings.computeIfAbsent(name, k -> new HashMap<>()).computeIfAbsent(DynamicFunction.Callback.class, k -> EnumSet.noneOf(ScriptType.class)).add(getType());
	}

	@Override
	public void addFunction(String name, BaseFunction function) {
		bindings.computeIfAbsent(name, k -> new HashMap<>()).computeIfAbsent(BaseFunction.class, k -> EnumSet.noneOf(ScriptType.class)).add(getType());
	}
}
