package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.FieldData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.IndexedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.MethodData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.util.List;

public class RawClassWrapper implements RawClassData {
    protected final Class<?> clazz;
    protected final CollectionGroup collectionGroup;
    protected List<AnnotationID> annotations;
    protected NameID name;
    protected List<TypeVariableID> typeParameters;
    protected PackageID packageID;
    protected TypeID superClass;
    protected List<TypeID> interfaces;
    protected List<TypeID> innerClasses;
    protected TypeID enclosingClass;
    protected TypeID declaringClass;
    protected List<FieldData> fields;
    protected List<ConstructorData> constructors;
    protected List<MethodData> methods;
    protected TypeID index;

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
        return this.clazz.getModifiers();
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

    @Override
    public TypeID getIndex() {
        return null;
    }

    @Override
    public IndexedData<TypeOrTypeVariableID> setIndex(TypeOrTypeVariableID index) {
        return null;
    }
}
