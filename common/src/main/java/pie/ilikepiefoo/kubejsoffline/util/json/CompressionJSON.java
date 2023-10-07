package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;

import java.util.function.Function;

public class CompressionJSON {
    private static CompressionJSON INSTANCE;
    private final LinkedTreeMap<String, Integer> EXISTING_TYPES = new LinkedTreeMap<>();
    private JsonArray DATA;
    private int CURRENT_ID = 0;

    public CompressionJSON() {
        DATA = new JsonArray();
    }

    public static CompressionJSON getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CompressionJSON();
        }
        return INSTANCE;
    }

    public String getId(String word) {
        return String.valueOf(EXISTING_TYPES.computeIfAbsent(word, (key) -> {
            synchronized (this) {
                DATA.add(word);
                return CURRENT_ID++;
            }
        }));
    }

    public String getWord(int id) {
        return DATA.get(id).getAsString();
    }

    public JsonArray getData() {
        return DATA;
    }

    public void clear() {
        DATA = new JsonArray();
        CURRENT_ID = 0;
    }

    // The following javadoc is from Javascript code. Use this as a reference to create the Java equivalent.
    // function splitModifyCollect(input, seperator, modifier){
    //    let parts = input.split(seperator);
    //    let output = [];
    //    for(let i=0; i<parts.length; i++) {
    //        output.push(modifier(parts[i]));
    //    }
    //    return output.join(seperator);
    //}
    //function compressFirstHalf(input, seperator, modifier){
    //    let parts = input.split(seperator);
    //    let lastIndexOf = input.lastIndexOf(seperator);
    //    if(lastIndexOf == -1){
    //        return modifier(input);
    //    }
    //    let start = modifier(input.substring(0,lastIndexOf));
    //    let end = modifier(input.substring(lastIndexOf+1));
    //    return  start + seperator + end;
    //}
    //let LOOK_UP = [];
    //function mapCompression(part){
    //    if(typeof(compression[part]) === 'number'){
    //        return compression[part];
    //    }else{
    //        compression[part] = counter;
    //        LOOK_UP[counter] = part;
    //        counter++;
    //        return mapCompression(part);
    //    }
    //}
    //function mapUncompress(part){
    //    return LOOK_UP[part];
    //}
    //function compress(input){
    //    //let result = splitModifyCollect(input, '<', (a) => splitModifyCollect(a, '>', (b) => splitModifyCollect(b, '$', (c) => compressFirstHalf(c, '.', mapCompression))));
    //    let result = splitModifyCollect(input, '<', (a) => splitModifyCollect(a, '>', (b) => compressFirstHalf(b, '.', mapCompression)));
    //    //let result = splitModifyCollect(input, '<', (a) => compressFirstHalf(a, '.', mapCompression));
    //    //let result = compressFirstHalf(input, '.', mapCompression);
    //
    //    return result;
    //    //return input;
    //}
    //function uncompress(input){
    //    //let result = splitModifyCollect(input, '<', (a) => splitModifyCollect(a, '>', (b) => splitModifyCollect(b, '$', (c) => compressFirstHalf(c, '.', mapUncompress))));
    //    let result = splitModifyCollect(input, '<', (a) => splitModifyCollect(a, '>', (b) => compressFirstHalf(b, '.', mapUncompress)));
    //    //let result = splitModifyCollect(input, '<', (a) => compressFirstHalf(a, '.', mapUncompress));
    //    //let result = compressFirstHalf(input, '.', mapUncompress);
    //
    //    return result;
    //    //return input;
    //}
    //function calculateTotalCompressionSize(){
    //    return JSON.stringify(compression).length;
    //}
    //function applyCompression(input){
    //    stats.beforeLength += input.length;
    //    let compressed = compress(input);
    //    stats.afterLength += compressed.length;
    //    let uncompressed = uncompress(compressed);
    //    if(uncompressed !== input){
    //        console.log(input);
    //        console.log(compressed);
    //        console.log(uncompressed);
    //        throw new Error("Decompression Failed.");
    //    }
    //}
    //let counter = 0;
    //let compression = {};
    //let stats = {beforeLength: 0, afterLength: 0};
    //for(let i=0; i<DATA.length; i++){
    //    let target = getClass(i);
    //    let methods = target.methods(true);
    //    let fields = target.fields(true);
    //    applyCompression(target.type());
    //    applyCompression(target.name());
    //    applyCompression(target.package());
    //    for(let i=0; i<methods.length; i++){
    //        let meth = getMethod(methods[i]);
    //        applyCompression(meth.name());
    //        let params = meth.parameters();
    //        for(let j=0; j<params.length; j++){
    //            let param = getParameter(params[j]);
    //            applyCompression(param.name());
    //        }
    //    }
    //    for (let i=0; i<fields.length; i++){
    //        let field = getField(fields[i]);
    //        applyCompression(field.name());
    //    }
    //}
    //let compressionSize = calculateTotalCompressionSize();
    //let before = stats.beforeLength;
    //let after = stats.afterLength;
    //console.log(`Before Length: ${before} After Length: ${after} Compression Size: ${compressionSize} Total Size: ${after + compressionSize} Difference: ${before - (after + compressionSize)} (%${((after + compressionSize)/before)*100})`);
    //console.log(stats);
    //console.log(JSON.stringify(DATA).length);

    public String splitModifyCollect(String input, String separator, Function<String, String> modifier) {
        String[] parts = input.split(separator);
        String[] output = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            output[i] = modifier.apply(parts[i]);
        }
        return String.join(separator, output);
    }

    public String compressFirstHalf(String input, String separator, Function<String, String> modifier) {
        int lastIndexOf = input.lastIndexOf(separator);
        if (lastIndexOf == -1) {
            return modifier.apply(input);
        }
        String start = modifier.apply(input.substring(0, lastIndexOf));
        String end = modifier.apply(input.substring(lastIndexOf + 1));
        return start + separator + end;
    }

    public String compress(String input) {
        return input;
//		return
//				splitModifyCollect(
//						input,
//						"<",
//						(a) -> splitModifyCollect(
//								a,
//								">",
//								(b) -> splitModifyCollect(
//										b,
//										"$",
//										(c) -> compressFirstHalf(
//												c,
//												".",
//												this::getId
//										)
//								)
//						)
//				);
    }
}
