package pie.ilikepiefoo.kubejsoffline;

import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import pie.ilikepiefoo.kubejsoffline.core.api.TypeNameMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RhinoTypeMapper implements TypeNameMapper {

    private final MinecraftRemapper remapper;

    public RhinoTypeMapper() {
        this.remapper = RemappingHelper.getMinecraftRemapper();
    }

    @Override
    public String getMappedClass(Class<?> from) {
        return remapper.getMappedClass(from);
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        return remapper.getMappedField(from, field);
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        return remapper.getMappedMethod(from, method);
    }
}
