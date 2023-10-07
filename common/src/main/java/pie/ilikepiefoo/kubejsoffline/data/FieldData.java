package pie.ilikepiefoo.kubejsoffline.data;

public class FieldData extends CommonData {
    public FieldData(int modifiers, String name, TypeData type) {
        super(name, modifiers);
        setType(type);
    }
}
