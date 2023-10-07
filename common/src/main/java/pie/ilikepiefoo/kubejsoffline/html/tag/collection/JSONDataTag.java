package pie.ilikepiefoo.kubejsoffline.html.tag.collection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.html.tag.CustomTag;

import java.io.IOException;
import java.io.Writer;

public class JSONDataTag extends CustomTag {
    private final String variableName;
    private final JsonElement jsonElement;
    private final Gson gson;

    public JSONDataTag(String variableName, JsonElement element, Gson gson) {
        super("script", true);
        this.variableName = variableName;
        this.jsonElement = element;
        this.gson = gson;
    }

    @Override
    public void writeContent(Writer writer) throws IOException {
        writer.write("const " + variableName + " = ");
        gson.toJson(jsonElement, writer);
        writer.write(";");
        writer.flush();
    }
}
