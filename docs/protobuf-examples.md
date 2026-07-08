# EMF ⇄ Protobuf — Examples

> **Status: v1.** These examples illustrate the API of the
> [EMF ⇄ Protobuf](/guides/protobuf) utility.

## A small model

Consider a minimal shop model (`shop.ecore`) with a `Product` and a `Category`:

```
Category
  name : EString                 // fieldNumber 1
  products : Product [0..*]       // fieldNumber 2, containment

Product
  name  : EString                // fieldNumber 1
  price : EDouble                // fieldNumber 2
  category : Category [0..1]      // fieldNumber 3, non-containment reference
```

Field numbers are pinned via an `EAnnotation` (source
`http://eclipse.org/fennec/protobuf`, key `fieldNumber`) on each feature.

## Derived `.proto`

`ProtobufSchema.forPackage(shopPackage).toProtoSource()` yields:

```proto
syntax = "proto3";
package shop;

message Category {
  string name = 1;
  repeated Product products = 2;   // containment -> embedded message
}

message Product {
  string name = 1;
  double price = 2;
  string category = 3;             // non-containment -> URI fragment reference
}
```

## Round-trip in plain Java

```java
ResourceSet rs = new ResourceSetImpl();
Resource r = rs.createResource(URI.createURI("shop"));
Category food = ShopFactory.eINSTANCE.createCategory();
food.setName("Food");
Product milk = ShopFactory.eINSTANCE.createProduct();
milk.setName("Milk");
milk.setPrice(1.29);
food.getProducts().add(milk);
r.getContents().add(food);

ProtobufSchema schema = ProtobufSchema.forPackage(ShopPackage.eINSTANCE);

byte[] bytes = schema.writer().toBytes(food);
Category restored = (Category) schema.reader().fromBytes(bytes, ShopPackage.Literals.CATEGORY);

assert restored.getProducts().get(0).getPrice() == 1.29;
```

## Round-trip through a `Resource` (OSGi)

With the `…​.protobuf.osgi` bundle installed, a Protobuf resource is created just like
any other EMF resource — the factory is bound by file extension:

```java
ResourceSet rs = ...; // from the Fennec EMF OSGi ResourceSet service
Resource r = rs.createResource(URI.createURI("shop.protobin"));
r.getContents().add(food);
r.save(null);          // writes protobuf wire bytes

Resource loaded = rs.createResource(URI.createURI("shop.protobin"));
loaded.load(null);     // reads them back
```

## Inheritance / polymorphism

Given an abstract `Animal` with concrete subtypes `Dog` and `Cat`, and a `Shelter` with a
polymorphic containment `animals : Animal [0..*]`:

```java
Dog rex = ShelterFactory.eINSTANCE.createDog();
rex.setName("Rex");        // inherited from Animal
rex.setBreed("Labrador");  // own
Cat mimi = ShelterFactory.eINSTANCE.createCat();
mimi.setName("Mimi");
mimi.setIndoor(true);

Shelter shelter = ShelterFactory.eINSTANCE.createShelter();
shelter.getAnimals().add(rex);
shelter.getAnimals().add(mimi);

ProtobufSchema schema = ProtobufSchema.forPackage(ShelterPackage.eINSTANCE);
Shelter read = (Shelter) schema.reader()
        .fromBytes(schema.writer().toBytes(shelter), ShelterPackage.Literals.SHELTER);

// Concrete subtypes and their inherited + own features are preserved:
assert read.getAnimals().get(0) instanceof Dog;
assert ((Dog) read.getAnimals().get(0)).getBreed().equals("Labrador");
assert read.getAnimals().get(1) instanceof Cat;
```

The `animals` field is emitted as an `EObjectAny` wrapper carrying the concrete type
(`Dog` / `Cat`) plus each object's bytes — see
[References and inheritance](/guides/protobuf#references-and-inheritance).

## Choosing a compact payload

The type discriminator defaults to `NAME` + smart compression. For the smallest payload on
a stable, versioned model, switch to `NUMERIC`; for maximum portability, `URI`:

```java
ProtobufContext ctx = ProtobufContext.defaults()
        .withTypeStrategy(ProtobufTypeStrategy.NUMERIC);
byte[] compact = schema.writer(ctx).toBytes(shelter);

// Through a ResourceSet (OSGi), set it once:
rs.getSaveOptions().put(ProtobufResource.OPTION_TYPE_STRATEGY, ProtobufTypeStrategy.NAME);
```

See the [user guide](/guides/protobuf#configuration) for the full configuration reference.
