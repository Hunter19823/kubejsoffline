package pie.ilikepiefoo.kubejsoffline.data;

public class WildcardData extends TypeData {
    protected TypeData wildcardType;

    public WildcardData( String name, TypeData wildcardType ) {
        super(name);
        this.wildcardType = wildcardType;
    }

    public TypeData getWildcardType() {
        return wildcardType;
    }

    @Override
    protected boolean isWildcard() {
        return true;
    }


}
