package pie.ilikepiefoo.kubejsoffline.data;

public class ArrayTypeData extends TypeData {
    protected TypeData type;
    protected int depth;

    public ArrayTypeData( String name, TypeData type, int depth ) {
        super(name);
        this.type = type;
        this.depth = depth;
    }

    public TypeData getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

}
