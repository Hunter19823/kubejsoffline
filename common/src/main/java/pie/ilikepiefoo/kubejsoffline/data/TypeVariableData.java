package pie.ilikepiefoo.kubejsoffline.data;

import java.util.Arrays;
import java.util.List;

public class TypeVariableData extends TypeData {

    protected String variableName;
    protected List<TypeData> bounds;

    public TypeVariableData( String name, String variableName ) {
        super(name);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<TypeData> getBounds() {
        if (this.bounds == null) return List.of();
        return bounds;
    }

    public TypeVariableData addBound( TypeData bound ) {
        if (this.bounds == null) this.bounds = new java.util.ArrayList<>();
        this.bounds.add(bound);
        return this;
    }

    public TypeVariableData addBounds( TypeData... bounds ) {
        if (this.bounds == null) this.bounds = new java.util.ArrayList<>();
        this.bounds.addAll(Arrays.asList(bounds));
        return this;
    }

    @Override
    protected boolean isTypeVariable() {
        return true;
    }

}
