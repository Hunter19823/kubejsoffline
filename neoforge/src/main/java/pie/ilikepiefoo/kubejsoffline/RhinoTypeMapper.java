package pie.ilikepiefoo.kubejsoffline;

import pie.ilikepiefoo.kubejsoffline.core.api.TypeNameMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RhinoTypeMapper implements TypeNameMapper {

    @Override
    public String getMappedClass(Class<?> from) {
        return "";
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        return "";
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        return "";
    }
}
