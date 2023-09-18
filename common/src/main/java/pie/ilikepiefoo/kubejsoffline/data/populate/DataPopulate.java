package pie.ilikepiefoo.kubejsoffline.data.populate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.data.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.data.ArrayTypeData;
import pie.ilikepiefoo.kubejsoffline.data.ClassData;
import pie.ilikepiefoo.kubejsoffline.data.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.data.FieldData;
import pie.ilikepiefoo.kubejsoffline.data.MethodData;
import pie.ilikepiefoo.kubejsoffline.data.ParameterData;
import pie.ilikepiefoo.kubejsoffline.data.TypeData;
import pie.ilikepiefoo.kubejsoffline.data.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.data.WildcardData;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataPopulate {
	private static final Logger LOG = LogManager.getLogger();
	private static DataPopulate instance;
	private final TreeMap<String,TypeData> CLASS_DATA;
	private final Map<TypeVariable<?>,TypeVariableData> TYPE_VARIABLES;
	private int classCount;

	public DataPopulate() {
		this.classCount = 0;
		this.CLASS_DATA = new TreeMap<>();
		this.TYPE_VARIABLES = new HashMap<>();
	}

	public static DataPopulate getInstance() {
		if (instance == null) {
			instance = new DataPopulate();
		}
		return instance;
	}

	/**
	 * The purpose of this method is to wrap a reference to a class, generic type, array type,
	 * or wildcard type into a TypeData object.
	 * All ClassData objects will only wrap their super classes and implemented interfaces.
	 * All methods, fields, constructors, and annotations should be handled by the populate method.
	 *
	 * @param subject The type to wrap.
	 * @return The wrapped type.
	 */
	public synchronized TypeData wrap( Type subject ) {
		if (subject == null) {
			return null;
		}
		String uniqueName = SafeOperations.safeUniqueTypeName(subject);
		if (CLASS_DATA.containsKey(uniqueName)) {
			return CLASS_DATA.get(uniqueName);
		}

		if (subject instanceof WildcardType wildcardType) {
			return getWildcard(wildcardType);
		}

		if (subject instanceof TypeVariable<?> parameterizedType) {
			return getTypeVariable(parameterizedType);
		}

		if (subject instanceof ParameterizedType parameterizedType) {
			return getParameterizedType(parameterizedType);
		}

		int depth = 0;
		Type componentType = subject;
		while (componentType instanceof GenericArrayType) {
			componentType = ( (GenericArrayType) componentType ).getGenericComponentType();
			depth++;
		}
		if (!( componentType instanceof Class<?> clazz )) {
			LOG.warn("Component type '{}' is not a class!", componentType);
			return null;
		}
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			depth++;
		}

		if (depth > 0) {
			return new ArrayTypeData(uniqueName, wrap(componentType), depth);
		}

		return getClassData(clazz);
	}

	@Nonnull
	private TypeData getWildcard( WildcardType wildcardType ) {
		var upper = SafeOperations.tryGet(wildcardType::getUpperBounds).filter(( bounds ) -> bounds.length > 1).map(( bounds ) -> bounds[ 0 ]);
		var lower = SafeOperations.tryGet(wildcardType::getLowerBounds).filter(( bounds ) -> bounds.length > 1).map(( bounds ) -> bounds[ 0 ]);
		if (lower.isEmpty() && upper.isEmpty()) {
			return wrap(Object.class);
		}
		return new WildcardData(SafeOperations.safeUniqueTypeName(wildcardType), wrap(lower.or(() -> upper).get()));
	}

	@Nonnull
	private TypeData getTypeVariable( TypeVariable<?> parameterizedType ) {
		if (TYPE_VARIABLES.containsKey(parameterizedType)) {
			return TYPE_VARIABLES.get(parameterizedType);
		}
		var type = new TypeVariableData(SafeOperations.safeUniqueTypeName(parameterizedType), parameterizedType.getName());
		TYPE_VARIABLES.put(parameterizedType, type);

		var result = SafeOperations.tryGet(parameterizedType::getBounds).map(Arrays::stream).map(( t ) -> t.map(this::wrap)).map(
				( t ) -> t.toArray(TypeData[]::new));

		result.ifPresent(type::addBounds);

		return type;
	}

	@Nonnull
	private TypeData getParameterizedType( ParameterizedType parameterizedType ) {
		ClassData rootClass = wrapToClassData(parameterizedType.getRawType());
		var subClassType = rootClass.getSourceClass();

		var type = new ClassData(SafeOperations.safeUniqueTypeName(parameterizedType), rootClass.getModifiers(), subClassType, classCount++);
		CLASS_DATA.put(type.getFullyQualifiedName(), type);
		type.setSourceClass(subClassType);
		SafeOperations.tryGet(parameterizedType::getOwnerType).ifPresent(( ownerType ) -> type.setOuterClass((ClassData) wrap(ownerType)));

		var result = SafeOperations.tryGet(parameterizedType::getActualTypeArguments).map(Arrays::stream).map(( t ) -> t.map(this::wrap)).map(
				( t ) -> t.toArray(TypeData[]::new));

		result.ifPresent(type::addGenericParameters);

		return type;
	}

	private ClassData getClassData( Class<?> clazz ) {
		String name = SafeOperations.safeUniqueTypeName(clazz);
		if (CLASS_DATA.containsKey(name)) {
			return (ClassData) CLASS_DATA.get(name);
		}
		var type = new ClassData(name, clazz.getModifiers(), clazz, classCount++);
		CLASS_DATA.put(name, type);
		type.setSourceClass(clazz);

		SafeOperations.tryGetFirst(clazz::getGenericSuperclass, clazz::getSuperclass).map(this::wrapToClassData).ifPresent(type::addSuperClasses);

		SafeOperations.tryGet(clazz::getGenericInterfaces).map(Arrays::stream).map(( t ) -> t.map(this::wrapToClassData)).map(
				( t ) -> t.toArray(ClassData[]::new)).ifPresent(type::addImplementedInterfaces);

		SafeOperations.tryGet(clazz::getInterfaces).map(Arrays::stream).map(( t ) -> t.map(this::wrapToClassData)).map(
				( t ) -> t.toArray(ClassData[]::new)).ifPresent(type::addImplementedInterfaces);

		return type;
	}

	private ClassData wrapToClassData( Type type ) {
		var result = wrap(type);
		if (result instanceof ClassData) {
			return (ClassData) result;
		}
		LOG.warn("Unable to wrap '{}' to ClassData!", type);
		return null;
	}

	public void clear() {
		CLASS_DATA.clear();
		TYPE_VARIABLES.clear();
	}

	/**
	 * The goal of this method is to populate the data of a class.
	 * This includes fields, methods, constructors, and annotations.
	 *
	 * @param data The class data to populate.
	 */
	public void populate( ClassData data ) {
		if (data == null) {
			return;
		}
		var clazz = data.getSourceClass();

		// Add Annotations
		data.addAnnotations(this.getAnnotations(clazz));

		// Add Fields
		data.addFields(this.getFields(clazz.getDeclaredFields()));

		// Add Methods
		data.addMethods(this.getMethods(clazz.getDeclaredMethods()));

		// Add Constructors
		data.addConstructors(this.getConstructors(clazz.getDeclaredConstructors()));
	}

	private ConstructorData[] getConstructors( Constructor<?>[] data ) {
		if (data == null || data.length == 0) {
			return new ConstructorData[ 0 ];
		}
		ConstructorData[] array = new ConstructorData[ data.length ];
		for (int i = 0; i < data.length; i++) {
			array[ i ] = new ConstructorData(data[ i ].getModifiers());
			array[ i ].addParameters(this.getParameters(data[ i ].getParameters()));
			array[ i ].addAnnotations(this.getAnnotations(data[ i ]));
		}
		return array;
	}

	private MethodData[] getMethods( Method[] declaredMethods ) {
		if (declaredMethods == null || declaredMethods.length == 0) {
			return new MethodData[ 0 ];
		}
		MethodData[] array = new MethodData[ declaredMethods.length ];
		for (int i = 0; i < declaredMethods.length; i++) {
			array[ i ] = new MethodData(declaredMethods[ i ].getModifiers(), SafeOperations.safeUnwrapName(declaredMethods[ i ]),
										wrap(declaredMethods[ i ].getGenericReturnType()));
			array[ i ].addParameters(this.getParameters(declaredMethods[ i ].getParameters()));
			array[ i ].addAnnotations(this.getAnnotations(declaredMethods[ i ]));
		}
		return array;
	}

	private FieldData[] getFields( Field[] declaredFields ) {
		if (declaredFields == null || declaredFields.length == 0) {
			return new FieldData[ 0 ];
		}
		FieldData[] array = new FieldData[ declaredFields.length ];
		for (int i = 0; i < declaredFields.length; i++) {
			array[ i ] = new FieldData(declaredFields[ i ].getModifiers(), SafeOperations.safeUnwrapName(declaredFields[ i ]),
									   wrap(declaredFields[ i ].getGenericType()));
			array[ i ].addAnnotations(this.getAnnotations(declaredFields[ i ]));
		}
		return array;
	}

	private ParameterData[] getParameters( Parameter[] parameters ) {
		if (parameters == null || parameters.length == 0) {
			return new ParameterData[ 0 ];
		}
		ParameterData[] array = new ParameterData[ parameters.length ];
		for (int i = 0; i < parameters.length; i++) {
			array[ i ] = new ParameterData(parameters[ i ].getModifiers(), SafeOperations.safeUnwrapName(parameters[ i ]),
										   wrap(parameters[ i ].getParameterizedType()));
			array[ i ].addAnnotations(this.getAnnotations(parameters[ i ]));
		}
		return array;
	}

	public AnnotationData[] getAnnotations( AnnotatedElement element ) {
		if (element == null) {
			return new AnnotationData[ 0 ];
		}
		var data = element.getAnnotations();
		if (data == null || data.length == 0) {
			return new AnnotationData[ 0 ];
		}

		AnnotationData[] array = new AnnotationData[ data.length ];
		for (int i = 0; i < data.length; i++) {
			array[ i ] = new AnnotationData(wrapToClassData(data[ i ].annotationType()), data[ i ].toString());
		}
		return array;
	}

}
