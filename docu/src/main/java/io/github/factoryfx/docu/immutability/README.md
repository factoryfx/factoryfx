# Immutability

Immutable objects are objects whose data are unchangeable after instantiation.

## Advantage
### Performance
### Traceability

## Immutability for business logic
For utility class Immutability is simple to implement. The best-known example are the String class or the Date API.
For business logic the implementation becomes more challenging. Data for business logic is usually mutable which means it changes at runtime.

```java
public class Product {
    public final int price;
    
    public Printer(int price) {
        this.text=text;
    }
    
    public buy(){
        System.out.print(text);
    }
}
```
-want to change the price at runtime
-need recreation
-but in the surrounding class you also want to have immutability
```java
public class ProductManger {
    public final Product product;
    
    public ProductManger(Product product) {
        this.product=product;
    }
}
```
-this cascade till the dependency root
-For runtime update the whole application is recreated



## Similar to dependency injection pattern
-from dependency injection to data injection

## Slow to recreate all
### Reuse what has not changed
### Control lifecycle to optimise recreation

## End user GUI
### Multiuser / conflict detection
### Edit metadata (Validation,Label,etc)

## That's where a frameworks comes in handy