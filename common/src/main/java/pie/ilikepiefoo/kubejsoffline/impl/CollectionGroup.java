package pie.ilikepiefoo.kubejsoffline.impl;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.collection.Parameters;
import pie.ilikepiefoo.kubejsoffline.api.collection.Types;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.FieldData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.MethodData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.collection.AnnotationsWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.NamesWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.PackagesWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.ParametersWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.TypesWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.AnnotationWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.ConstructorWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.FieldWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.MethodWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.ParameterWrapper;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public record CollectionGroup(
        Types types,
        Parameters parameters,
        Packages packages,
        Names names,
        Annotations annotations) implements JSONSerializable {
    public static final Logger LOG = LogManager.getLogger();
    public static final CollectionGroup INSTANCE = new CollectionGroup();

    public CollectionGroup() {
        this(new TypesWrapper(), new ParametersWrapper(), new PackagesWrapper(), new NamesWrapper(), new AnnotationsWrapper());
    }

    public List<AnnotationID> of(Annotation[] annotations) {
        LinkedList<AnnotationID> annotationList = new LinkedList<>();
        for (Annotation annotation : annotations) {
            if (annotation == null) {
                continue;
            }
            annotationList.add(annotations().addAnnotation(new AnnotationWrapper(this, annotation)));
        }
        return annotationList;
    }

    public List<ParameterID> of(Parameter[] parameters, Type[] genericTypes) {
        LinkedList<ParameterID> parameterList = new LinkedList<>();
        if (genericTypes.length < parameters.length) {
            Type[] newGenericTypes = new Type[parameters.length];
            int i;
            for (i = 0; i < genericTypes.length; i++) {
                newGenericTypes[i] = genericTypes[i];
            }
            for (; i < parameters.length; i++) {
                newGenericTypes[i] = parameters[i].getType();
            }
            genericTypes = newGenericTypes;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null || genericTypes[i] == null) {
                continue;
            }
            parameterList.add(parameters().addParameter(new ParameterWrapper(this, parameters[i], genericTypes[i])));
        }
        return parameterList;
    }

    public List<TypeOrTypeVariableID> of(Type[] types, Predicate<Type> ignoreType) {
        LinkedList<TypeOrTypeVariableID> typeList = new LinkedList<>();
        for (Type type : types) {
            if (type == null) {
                continue;
            }
            if (!SafeOperations.isTypePresent(type)) {
                continue;
            }
            if (ignoreType.test(type)) {
                continue;
            }
            typeList.add(of(type));
        }
        return typeList;
    }

    public TypeOrTypeVariableID of(Type type) {
        return TypeManager.INSTANCE.getID(type);
    }

    public List<TypeOrTypeVariableID> of(Type[] types) {
        LinkedList<TypeOrTypeVariableID> typeList = new LinkedList<>();
        for (Type type : types) {
            if (type == null) {
                continue;
            }
            if (!SafeOperations.isTypePresent(type)) {
                continue;
            }
            typeList.add(of(type));
        }
        return typeList;
    }

    public List<TypeID> ofTypes(Type[] types) {
        LinkedList<TypeID> typeList = new LinkedList<>();
        for (Type type : types) {
            if (type == null) {
                continue;
            }
            if (!SafeOperations.isTypePresent(type)) {
                continue;
            }
            typeList.add(of(type).asType());
        }
        return typeList;
    }

    public List<TypeID> of(Class<?>[] types) {
        LinkedList<TypeID> typeList = new LinkedList<>();
        for (Class<?> type : types) {
            if (type == null) {
                continue;
            }
            if (!SafeOperations.isClassPresent(type)) {
                continue;
            }
            typeList.add(of(type).asType());
        }
        return typeList;
    }

    public List<TypeVariableID> of(TypeVariable<?>[] typeVariables) {
        LinkedList<TypeVariableID> typeVariableList = new LinkedList<>();
        for (TypeVariable<?> typeVariable : typeVariables) {
            if (typeVariable == null) {
                throw new NullPointerException("TypeVariable cannot be null");
            }
            if (!SafeOperations.isTypeVariablePresent(typeVariable)) {
                throw new IllegalStateException("TypeVariable " + typeVariable + " is not fully loaded");
            }
            typeVariableList.add(of(typeVariable).asTypeVariable());
        }
        return typeVariableList;
    }

    public NameID nameOf(String name) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        return names().addName(name);
    }

    public PackageID packageOf(Package pack) {
        if (pack == null) {
            throw new NullPointerException("Package cannot be null");
        }
        return packages().addPackage(pack.getName());
    }

    public List<FieldData> of(Field[] fields) {
        LinkedList<FieldData> fieldList = new LinkedList<>();
        for (Field field : fields) {
            if (field == null) {
                continue;
            }
            if (!SafeOperations.isFieldPresent(field)) {
                continue;
            }
            fieldList.add(new FieldWrapper(this, field));
        }
        return fieldList;
    }

    public List<MethodData> of(Method[] methods) {
        LinkedList<MethodData> methodList = new LinkedList<>();
        for (Method method : methods) {
            if (method == null) {
                continue;
            }
            if (!SafeOperations.isMethodPresent(method)) {
                continue;
            }
            methodList.add(new MethodWrapper(this, method));
        }
        return methodList;
    }

    public List<ConstructorData> of(Constructor<?>[] constructors) {
        LinkedList<ConstructorData> constructorList = new LinkedList<>();
        for (Constructor<?> constructor : constructors) {
            if (constructor == null) {
                continue;
            }
            if (!SafeOperations.isConstructorPresent(constructor)) {
                continue;
            }
            constructorList.add(new ConstructorWrapper(this, constructor));
        }
        return constructorList;
    }


    public void clear() {
        types().clear();
        parameters().clear();
        packages().clear();
        names().clear();
        annotations().clear();
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        json.add("types", types().toJSON());
        json.add("parameters", parameters().toJSON());
        json.add("packages", packages().toJSON());
        json.add("names", names().toJSON());
        json.add("annotations", annotations().toJSON());
        return json;
    }
}
