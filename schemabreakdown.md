# KubeJS Offline Rewrite

# Collections

## Types

```
index: *(RawClass | Parameterized Type | Wildcard Type)
```

## Names

```
index: *string
```

## Annotations

```
index: *string
```

## Packages

```
index: *(nameID | [*nameID, *packageID])
  parsing instructions:
    1. If the item at the index is a string, return the name with that ID.
    2. If the item at the index is an array, return the name of the first item in that array, appended by a period, appended by the package name in the second index.
```

# Identifiers & References

### TypeVariableReference

```
*(typeVariableID | [*typeVariableID, *depth])
```

### TypeReference

```
*(typeID | [*typeID, *depth])
```

### TypeOrTypeVariableReference

```
*(TypeReference | TypeVariableReference)
```

# Data Structures

### Parameter

```
  *name: *nameID
  *TypeOrTypeVariableID
  annotations: *[*annotationID]
```

### Raw Class

```
index:
  *name: *nameID
  *id: *classID
  *modifier: *int
  package: *packageID
  superClass: *TypeID
  interfaces: *[*TypeID]
  annotations: *[*annotationID]
  *(typeVariables): *[*TypeVariableReference]
  (innerClasses | outerClass): *([*classID] | classID)

  fields:
    *name: *nameID
    *modifier: *int
    *(type | typeVariable): *TypeOrTypeVariableID
    annotations: *[*annotationID]

  constructors:
    *modifier: *int
    annotations: *[*annotationID]
    *(type | typeVariable): *TypeOrTypeVariableID
    parameters: *[*Parameter]

  methods:
    *modifier: int
    annotations: *[*annotationID]
    *(typeVariables): *[*TypeVariableReference]
    *(type | typeVariable): *TypeOrTypeVariableID
    parameters: *[*Parameter]
```

### Parametrized Type

```
index:
  *id: *typeID
  *rawType: *classID
  *actualTypes:
    *(type | typeVariable): *TypeOrTypeVariableID
```

### Type Variable

```
index:
  *id: *typeVariableID
  *typeVariableName: *nameID
  *(extends | supers):
    *(type | typeVariable): *TypeOrTypeVariableID
```

### Wildcard Type

```
index:
  *id: *typeID
  *(extends | supers):
    *(type | typeVariable): *TypeOrTypeVariableID
```