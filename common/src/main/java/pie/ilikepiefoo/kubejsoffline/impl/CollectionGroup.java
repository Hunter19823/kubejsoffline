package pie.ilikepiefoo.kubejsoffline.impl;


import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.collection.Parameters;
import pie.ilikepiefoo.kubejsoffline.api.collection.Types;
import pie.ilikepiefoo.kubejsoffline.impl.collection.AnnotationsWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.NamesWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.PackagesWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.ParametersWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.collection.TypesWrapper;

public record CollectionGroup(
        Types types,
        Parameters parameters,
        Packages packages,
        Names names,
        Annotations annotations) {
    public static final CollectionGroup INSTANCE = new CollectionGroup();

    public CollectionGroup() {
        this(new TypesWrapper(), new ParametersWrapper(), new PackagesWrapper(), new NamesWrapper(), new AnnotationsWrapper());
    }
}
