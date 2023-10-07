package pie.ilikepiefoo.kubejsoffline.util;

@ExpectedTypeName("pie.ilikepiefoo.kubejsoffline.util.WildcardGenerics")
public class WildcardGenerics {
    @ExpectedTypeName("java.lang.String")
    public static final String PACKAGE = "pie.ilikepiefoo.kubejsoffline.util.";

    @ExpectedTypeName(PACKAGE + "GenericsExample<?>")
    public GenericsExample<?> defaultGeneric = new GenericsExample<>();

    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Number>")
    public GenericsExample<? super Number> superAbstractClass = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Number>[]")
    public GenericsExample<? super Number>[] arraySuperAbstractClass = new GenericsExample[0];
    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Number[]>")
    public GenericsExample<? super Number[]> superAbstractClassArray = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Number[]>[]")
    public GenericsExample<? super Number[]>[] arraySuperAbstractClassArray = new GenericsExample[0];

    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends java.lang.Number>")
    public GenericsExample<? extends Number> extendsAbstractClass = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends java.lang.Number>[]")
    public GenericsExample<? extends Number>[] arrayExtendsAbstractClass = new GenericsExample[0];
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends java.lang.Number[]>")
    public GenericsExample<? extends Number[]> extendsAbstractClassArray = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends java.lang.Number[]>[]")
    public GenericsExample<? extends Number[]>[] arrayExtendsAbstractClassArray = new GenericsExample[0];

    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Object>")
    public GenericsExample<? super Object> superObject = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Object>[]")
    public GenericsExample<? super Object>[] arraySuperObject = new GenericsExample[0];
    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Object[]>")
    public GenericsExample<? super Object[]> superObjectArray = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? super java.lang.Object[]>[]")
    public GenericsExample<? super Object[]>[] arraySuperObjectArray = new GenericsExample[0];

    @ExpectedTypeName(PACKAGE + "GenericsExample<?>")
    public GenericsExample<? extends Object> extendsObject = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<?>[]")
    public GenericsExample<? extends Object>[] arrayExtendsObject = new GenericsExample[0];
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends java.lang.Object[]>")
    public GenericsExample<? extends Object[]> extendsObjectArray = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends java.lang.Object[]>[]")
    public GenericsExample<? extends Object[]>[] arrayExtendsObjectArray = new GenericsExample[0];

    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends " + PACKAGE + "GenericsExample<?>>")
    public GenericsExample<? extends GenericsExample<?>> extendsWildcard = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends " + PACKAGE + "GenericsExample<?>>[]")
    public GenericsExample<? extends GenericsExample<?>>[] arrayExtendsWildcard = new GenericsExample[0];
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends " + PACKAGE + "GenericsExample<? super java.lang.Object>>")
    public GenericsExample<? extends GenericsExample<? super Object>> extendsWildcardSuper = new GenericsExample<>();
    @ExpectedTypeName(PACKAGE + "GenericsExample<? extends " + PACKAGE + "GenericsExample<? super java.lang.Object>>[]")
    public GenericsExample<? extends GenericsExample<? super Object>>[] arrayExtendsWildcardSuper = new GenericsExample[0];


}
