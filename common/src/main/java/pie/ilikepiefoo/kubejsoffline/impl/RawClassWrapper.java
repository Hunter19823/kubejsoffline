package pie.ilikepiefoo.kubejsoffline.impl;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.FieldData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.MethodData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public class RawClassWrapper implements RawClassData {
    private final Class<?> clazz;
    private final CollectionGroup collectionGroup;

    public RawClassWrapper(CollectionGroup group, Class<?> clazz) {
        this.collectionGroup = group;
        this.clazz = clazz;
    }


    @Override
    public List<AnnotationID> getAnnotations() {
        return null;
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public NameID getName() {
        return null;
    }

    @Override
    public List<TypeVariableID> getTypeParameters() {
        return null;
    }

    @Override
    public PackageID getPackage() {
        return null;
    }

    @Override
    public TypeID getSuperClass() {
        return null;
    }

    @Override
    public List<TypeID> getInterfaces() {
        return null;
    }

    @Override
    public List<TypeID> getInnerClasses() {
        return null;
    }

    @Override
    public TypeID getEnclosingClass() {
        return null;
    }

    @Override
    public TypeID getDeclaringClass() {
        return null;
    }

    @Override
    public List<FieldData> getFields() {
        return null;
    }

    @Override
    public List<ConstructorData> getConstructors() {
        return null;
    }

    @Override
    public List<MethodData> getMethods() {
        return null;
    }
}
