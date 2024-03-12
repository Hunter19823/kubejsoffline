package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;


public interface RawClassData {
    NameID getName();

    int getModifiers();

    List<TypeVariableID> getTypeParameters();

    PackageID getPackage();

    TypeID getSuperClass();

    List<TypeID> getInterfaces();

    List<TypeID> getAnnotations();

    List<TypeID> getInnerClasses();

    TypeID getEnclosingClass();

    TypeID getDeclaringClass();

    List<FieldData> getFields();

    List<ConstructorData> getConstructors();

    List<MethodData> getMethods();

}
