package pie.ilikepiefoo.kubejsoffline.data.populate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.data.ArrayTypeData;
import pie.ilikepiefoo.kubejsoffline.data.ClassData;
import pie.ilikepiefoo.kubejsoffline.data.ParametrizedData;
import pie.ilikepiefoo.kubejsoffline.data.TypeData;
import pie.ilikepiefoo.kubejsoffline.data.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.data.WildcardData;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DataMapper {
    private static final Logger LOG = LogManager.getLogger();
    private final static DataMapper INSTANCE = new DataMapper();
    private final Map<TypeDataIdentifier, Map<String, TypeData>> TYPE_DATA_MAPS;
    private final Map<TypeDataIdentifier, Map<TypeData, Integer>> IDENTIFIERS;
    private final Map<TypeDataIdentifier, AtomicInteger> COUNTERS;

    private DataMapper() {
        this.TYPE_DATA_MAPS = new java.util.EnumMap<>(TypeDataIdentifier.class);
        this.IDENTIFIERS = new java.util.EnumMap<>(TypeDataIdentifier.class);
        this.COUNTERS = new java.util.EnumMap<>(TypeDataIdentifier.class);
    }

    public static DataMapper getInstance() {
        return INSTANCE;
    }

    public TypeData save(Type type, TypeData data) {
        if (contains(type)) {
            LOG.warn("Type {} already exists!", data.getName());
            throw new IllegalArgumentException("Type already exists!");
        }
        String key = map(type);
        getMap(TypeDataIdentifier.ALL).put(key, data);
        getMap(identify(data)).put(key, data);
        return data;
    }

    public boolean contains(Type type) {
        return getMap(TypeDataIdentifier.ALL).containsKey(type);
    }

    public String map(Type type) {
        return SafeOperations.safeUniqueTypeName(type);
    }

    public Map<String, TypeData> getMap(TypeDataIdentifier identifier) {
        return Objects.requireNonNullElseGet(TYPE_DATA_MAPS.get(identifier), () -> {
            Map<String, TypeData> map = new java.util.HashMap<>();
            TYPE_DATA_MAPS.put(identifier, map);
            return map;
        });
    }

    public TypeDataIdentifier identify(TypeData data) {
        if (data instanceof ParametrizedData) {
            return TypeDataIdentifier.PARAMETRIZED;
        } else if (data instanceof TypeVariableData) {
            return TypeDataIdentifier.TYPE_VARIABLE;
        } else if (data instanceof ArrayTypeData) {
            return TypeDataIdentifier.ARRAY;
        } else if (data instanceof WildcardData) {
            return TypeDataIdentifier.WILDCARD;
        } else if (data instanceof ClassData) {
            return TypeDataIdentifier.CLASS;
        } else {
            LOG.warn("Type {} is not a known type!", data.getName());
            throw new IllegalArgumentException("Type is not a known type!");
        }
    }

    public TypeData get(Type type) {
        return getMap(TypeDataIdentifier.ALL).get(type);
    }

    public ClassData getClassData(Type type) {
        return (ClassData) getMap(TypeDataIdentifier.CLASS).get(type);
    }

    public boolean containsClassData(Type type) {
        return getMap(TypeDataIdentifier.CLASS).containsKey(type);
    }

    public List<ClassData> getClassData() {
        return getMap(TypeDataIdentifier.CLASS)
                .values()
                .parallelStream()
                .map((t) -> (ClassData) t)
                .collect(Collectors.toList());
    }

    public void clear() {
        TYPE_DATA_MAPS.forEach(
                (identifier, map) -> map.clear()
        );
        TYPE_DATA_MAPS.clear();
        COUNTERS.clear();
        IDENTIFIERS.clear();
    }

    public synchronized int getIdentifier(TypeData typeData) {
        if (!this.COUNTERS.containsKey(TypeDataIdentifier.ALL)) {
            this.COUNTERS.put(TypeDataIdentifier.ALL, new AtomicInteger(0));
            this.IDENTIFIERS.put(TypeDataIdentifier.ALL, new java.util.HashMap<>());
        }
        var identity = identify(typeData);
        if (!this.COUNTERS.containsKey(identity)) {
            this.COUNTERS.put(identity, new AtomicInteger(0));
            this.IDENTIFIERS.put(identity, new java.util.HashMap<>());
        }
        var map = this.IDENTIFIERS.get(identity);
        if (!map.containsKey(typeData)) {
            map.put(typeData, this.COUNTERS.get(identity).getAndIncrement());
        }
        return map.get(typeData);
    }

    public enum TypeDataIdentifier {
        ALL,
        CLASS,
        TYPE_VARIABLE,
        PARAMETRIZED,
        ARRAY,
        WILDCARD
    }
}
