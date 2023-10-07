package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class MethodJSON {
    @Nonnull
    public static JsonArray of(@Nullable Method[] methods) {
        if (methods == null) {
            return new JsonArray();
        }
        JsonArray object = new JsonArray();
        for (var method : methods) {
            var methodJson = of(method);
            if (methodJson != null) {
                object.add(methodJson);
            }
        }
        return object;
    }

    @Nullable
    public static JsonObject of(@Nonnull Method method) {
        JsonObject object = new JsonObject();
        // Name of the method
        var temp = SafeOperations.safeUnwrapName(method);
        if (temp != null) {
            object.addProperty(JSONProperty.METHOD_NAME.jsName, CompressionJSON.getInstance().compress(temp));
        }

        // Return type of the method
        var type = SafeOperations.safeUnwrapReturnType(method);
        if (type == null) // Throw out any methods that don't have a return type.
        {
            return null;
        }

        var typeObject = TypeJSON.of(type);
        if (typeObject == null) // Throw out any methods that don't have a return type.
        {
            return null;
        }

        TypeJSON.attachGenericAndArrayData(typeObject, method::getReturnType);
        TypeJSON.attachGenericAndArrayData(typeObject, method::getGenericReturnType);

        object.addProperty(JSONProperty.METHOD_RETURN_TYPE.jsName, typeObject.get(JSONProperty.TYPE_ID.jsName).getAsInt());

        // Attach Parameters, annotations, and modifiers.
        ExecutableJSON.attach(object, method);
        if (!object.has(JSONProperty.PARAMETERS.jsName)) // Throw out any methods that have brocken parameters.
        {
            return null;
        }

        return object;
    }
}
