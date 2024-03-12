package pie.ilikepiefoo.kubejsoffline.impl;


import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.collection.Parameters;
import pie.ilikepiefoo.kubejsoffline.api.collection.TypeVariables;
import pie.ilikepiefoo.kubejsoffline.api.collection.Types;

public record CollectionGroup(
        Types types,
        TypeVariables typeVariables,
        Parameters parameters,
        Packages packages,
        Names names,
        Annotations annotations) {
}
