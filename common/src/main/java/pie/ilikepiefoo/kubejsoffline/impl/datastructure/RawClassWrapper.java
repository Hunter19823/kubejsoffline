package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.FieldData;
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
        if (annotations != null) {
            return annotations;
        }
        return this.annotations = collectionGroup.of(clazz.getAnnotations());
    }

    @Override
    public int getModifiers() {
        return this.clazz.getModifiers();
    }

    @Override
    public NameID getName() {
        if (name != null) {
            return name;
        }
        return this.name = collectionGroup.names().addName(clazz.getName());
    }

    @Override
    public List<TypeVariableID> getTypeParameters() {
        if (typeParameters != null) {
            return typeParameters;
        }
        return this.typeParameters = collectionGroup.of(clazz.getTypeParameters());
    }

    @Override
    public PackageID getPackage() {
        if (packageID != null) {
            return packageID;
        }
        return this.packageID = collectionGroup.packageOf(clazz.getPackage());
    }

    @Override
    public TypeID getSuperClass() {
        if (superClass != null) {
            return superClass;
        }
        return this.superClass = collectionGroup.of(clazz.getGenericSuperclass()).asType();
    }

    @Override
    public List<TypeID> getInterfaces() {
        if (interfaces != null) {
            return interfaces;
        }
        return this.interfaces = collectionGroup.ofTypes(clazz.getGenericInterfaces());
    }

    @Override
    public List<TypeID> getInnerClasses() {
        if (innerClasses != null) {
            return innerClasses;
        }
        return this.innerClasses = collectionGroup.ofTypes(clazz.getClasses());
    }

    @Override
    public TypeID getEnclosingClass() {
        if (enclosingClass != null) {
            return enclosingClass;
        }
        return this.enclosingClass = collectionGroup.of(clazz.getEnclosingClass()).asType();
    }

    @Override
    public TypeID getDeclaringClass() {
        if (declaringClass != null) {
            return declaringClass;
        }
        return this.declaringClass = collectionGroup.of(clazz.getDeclaringClass()).asType();
    }

    @Override
    public List<FieldData> getFields() {
        if (fields != null) {
            return fields;
        }
        return this.fields = collectionGroup.of(clazz.getFields());
    }

    @Override
    public List<ConstructorData> getConstructors() {
        if (constructors != null) {
            return constructors;
        }
        return this.constructors = collectionGroup.of(clazz.getConstructors());
    }

    @Override
    public List<MethodData> getMethods() {
        if (methods != null) {
            return methods;
        }
        return this.methods = collectionGroup.of(clazz.getMethods());
    }

    @Override
    public TypeID getIndex() {
        return index;
    }

    @Override
    public RawClassData setIndex(TypeOrTypeVariableID index) {
        this.index = index.asType();
        return this;
    }
}
