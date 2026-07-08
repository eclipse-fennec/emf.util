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
`http://www.eclipse.org/fennec/protobuf`, key `fieldNumber`) on each feature.

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
