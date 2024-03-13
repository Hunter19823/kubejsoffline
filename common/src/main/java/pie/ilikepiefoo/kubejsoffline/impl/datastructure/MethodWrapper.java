package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.MethodData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.lang.reflect.Method;
import java.util.List;

public class MethodWrapper implements MethodData {
    protected final CollectionGroup collectionGroup;
    protected final Method method;
    protected List<AnnotationID> annotations;
    protected List<TypeVariableID> typeParameters;
    protected List<TypeID> exceptions;
    protected List<ParameterID> parameters;
    protected TypeOrTypeVariableID type;
    protected NameID name;


    public MethodWrapper(CollectionGroup collectionGroup, Method method) {
        this.collectionGroup = collectionGroup;
        this.method = method;
    }


    @Override
    public List<AnnotationID> getAnnotations() {
        if (annotations != null) {
            return annotations;
        }
        return this.annotations = collectionGroup.of(method.getAnnotations());
    }

    @Override
    public List<TypeVariableID> getTypeParameters() {
        if (typeParameters != null) {
            return typeParameters;
        }
        return this.typeParameters = collectionGroup.of(method.getTypeParameters());
    }

    @Override
    public List<TypeID> getExceptions() {
        if (exceptions != null) {
            return exceptions;
        }
        return this.exceptions = collectionGroup.of(method.getExceptionTypes());
    }

    @Override
    public List<ParameterID> getParameters() {
        if (parameters != null) {
            return parameters;
        }
        return this.parameters = collectionGroup.of(method.getParameters(), method.getGenericParameterTypes());
    }

    @Override
    public TypeOrTypeVariableID getType() {
        if (type != null) {
            return type;
        }
        return this.type = collectionGroup.of(method.getGenericReturnType());
    }

    @Override
    public NameID getName() {
        if (name != null) {
            return name;
        }
        return this.name = collectionGroup.names().addName(method.getName());
    }
}
