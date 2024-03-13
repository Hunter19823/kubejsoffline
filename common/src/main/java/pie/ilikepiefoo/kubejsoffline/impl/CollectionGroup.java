package pie.ilikepiefoo.kubejsoffline.impl;


import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.collection.Parameters;
import pie.ilikepiefoo.kubejsoffline.api.collection.Types;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
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
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.ParameterWrapper;

import java.lang.annotation.Annotation;
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
        Annotations annotations) {
    public static final CollectionGroup INSTANCE = new CollectionGroup();

    public CollectionGroup() {
        this(new TypesWrapper(), new ParametersWrapper(), new PackagesWrapper(), new NamesWrapper(), new AnnotationsWrapper());
    }

    public List<AnnotationID> of(Annotation[] annotations) {
        LinkedList<AnnotationID> annotationList = new LinkedList<>();
        for (Annotation annotation : annotations) {
            annotationList.add(annotations().addAnnotation(new AnnotationWrapper(this, annotation)));
        }
        return annotationList;
    }

    public List<ParameterID> of(Parameter[] parameters, Type[] genericTypes) {
        LinkedList<ParameterID> parameterList = new LinkedList<>();
        for (int i = 0; i < parameters.length; i++) {
            parameterList.add(parameters().addParameter(new ParameterWrapper(this, parameters[i], genericTypes[i])));
        }
        return parameterList;
    }

    public List<TypeOrTypeVariableID> of(Type[] types, Predicate<Type> ignoreType) {
        LinkedList<TypeOrTypeVariableID> typeList = new LinkedList<>();
        for (Type type : types) {
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
            typeList.add(of(type));
        }
        return typeList;
    }

    public List<TypeID> of(Class<?>[] types) {
        LinkedList<TypeID> typeList = new LinkedList<>();
        for (Class<?> type : types) {
            typeList.add(of(type).asType());
        }
        return typeList;
    }

    public List<TypeVariableID> of(TypeVariable<?>[] typeVariables) {
        LinkedList<TypeVariableID> typeVariableList = new LinkedList<>();
        for (TypeVariable<?> typeVariable : typeVariables) {
            typeVariableList.add(of(typeVariable).asTypeVariable());
        }
        return typeVariableList;
    }

    public NameID nameOf(String name) {
        return names().addName(name);
    }

    public void clear() {
        types().clear();
        parameters().clear();
        packages().clear();
        names().clear();
        annotations().clear();
    }
}
