package pie.ilikepiefoo.kubejsoffline.api.datastructure;

public interface TypeData {
    boolean isRawType();

    boolean isParameterizedType();

    boolean isWildcardType();

    boolean isTypeVariable();

    RawClassData asRawType();

    ParameterizedTypeData asParameterizedType();

    WildcardTypeData asWildcardType();

    TypeVariableData asTypeVariable();
}
