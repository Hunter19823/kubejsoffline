package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.data.populate.DataMapper;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class ParametrizedData extends TypeData {
    private static final Logger LOG = LogManager.getLogger();
    protected ClassData rawType;
    protected List<TypeData> typeArguments;
    protected ParameterizedType type;

    public ParametrizedData(String name, ParameterizedType type, ClassData rawType) {
        super(name);
        this.type = type;
        this.rawType = rawType;
    }

    public ParametrizedData addTypeArguments(TypeData... typeArguments) {
        if (this.typeArguments == null) {
            this.typeArguments = new java.util.ArrayList<>();
        }
        this.typeArguments.addAll(List.of(typeArguments));
        return this;
    }

    @Override
    public JsonElement toJSON() {
        JsonObject object = super.toJSON().getAsJsonObject();
        object.add(JSONProperty.RAW_CLASS.jsName, getRawType().toReference());
        if (!getTypeArguments().isEmpty()) {
            var array = new JsonArray();
            for (TypeData typeArgument : getTypeArguments()) {
                array.add(typeArgument.toReference());
            }
            object.add(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName, array);
        } else {
            LOG.warn("Type Arguments cannot be empty!");
            throw new NullPointerException("Type Arguments cannot be empty!");
        }
        return object;
    }

    public ClassData getRawType() {
        return rawType;
    }

    public List<TypeData> getTypeArguments() {
        if (typeArguments == null) {
            return List.of();
        }
        return typeArguments;
    }

    @Override
    public JsonObject toReference() {
        JsonObject object = new JsonObject();
        var array = new JsonArray();
        for (TypeData typeArgument : getTypeArguments()) {
            array.add(DataMapper.getInstance().getIdentifier(typeArgument));
        }
        object.addProperty(JSONProperty.RAW_CLASS.jsName, getRawType().getId());
        object.add(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName, array);
        return object;
    }
}
