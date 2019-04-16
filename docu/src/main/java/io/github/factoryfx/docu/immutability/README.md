# Immutability

Immutable objects are objects whose data are unchangeable after instantiation.

## Advantage

Immutable objects are safe for concurrent use as the is no reader/writer problem to take into account.
All mutlihtreading issues can be accounted for by the need to control access to shared resources. Of course,
applications that throughout have no write semantics must be meaningless, as they would never produce any output.

But in general, immutability is favorable over dynamic objects as they completely remove any aspect of time-bound behavior.
Within factoryFX, all changes applied to a running application are reduced to a single write operation, which is the minimal
solution in terms of reducing concurrent execution complexity. Indeed it is reduced to a single pointer-write, which even
eliminates the need to synchronize.

The ability to change a compound of immutable objects dynamically opens the concept of dependency injection to a larger
portion of the system concerned. In most cases, the bussiness data can be handled within the memory of current computers,
so the business data may be contained in business objects owning it. This allows for dependency injection for all objects
that are changed on low frequencies.

As to our knowledge there is no other framework combining immutabílity and reconfigurability.

### Performance

The before-mention business objects would - in most real-life systems - be hosted in databased, be it relations or no-SQL
or whatever. Those system include various costly indirections, at least the protocol overhead to move the data from one address
space to the other, in most cases I/O subsystem involvement. In factoryFX, the data would direclty reside in the business
objects needing it. The business objects themselves carry all dependencies needed to fulfill their contracts. All those
are hostes in-memory in the same address space without any need to synchronize, which is basically the highest-performance
solution.

And if the imagined alternative solution would include relations databases, it even eliminates any search within the data,
as the objects needed are at hand and not to be searched for.

Finding things is always faster than searching for them.

### Traceability

Any input in the system built with factoryFX is being processed based on one exact system configuration. This is due to
the atomicity of configuration change which inevitably bundles all changes to a virtually instant flip of the system from
the view of a single request hitting the system boundaries. Therefore the system behaviour can be fully reconstructed by
using the same configuration as the one that the respective request had been processed with. As all state then is identical
you can carry out a post-mortem debug session on historic processings.

### Minimal recreation

FactoryFX limits the need for recreation of objects to the minimum that is needed to reconfigure a system. It will change
only those object affected by a change in configuration or data.
 

## Immutability for business logic by example

For utility classes, immutability is easy to implement. The best-known examples are the String class or the Date API.
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

This class does not allow for changes of the price of it's instances. And we want this, as we want immutabilty. But yet, we want to change
it. The only way to do so would be to recreate the complete object. Let's do it that way! Sorry, we have a problem, how do we
put that new object in place?
The simple answer is: Replace the object containing it with a new one. Let's imagine we have a ProductManager containing the product:

```java
public class ProductManger {
    public final Product product;
    
    public ProductManger(Product product) {
        this.product=product;
    }
}
```

Obviously we have to repeat replacing objects, until we hit the system root object. This is exactly what factoryFX does.
But most objects will do not see a change in data or dependencies. In most applications, the circumfence of a changes only
affects very few objects. FactoryFX will reuse those objects and not recreate them, if they have not been changes, nor
have their transitive dependents. If we thus imagine our ProductManager was accounting for the complete list of products we know:  

```java
public class ProductManger {
    public final List<Product> products;
    
    public ProductManger(List<Product> products) {
        this.product=products;
    }
}
```

factoryFX would recreate the ProductManager with a list consisting of products, that did not change and changes ones that
are freshly created instances. The java garbage collector will take care of the obsolete products that are no longer referenced.

## End user GUI

FactoryFX comes with an out-of-the-box user interface that allows for changing the system's configuration. It is based on
metadata you can add to the meta-level (factories). 

The user interface is based on an interface that you can use to change to system's configuration with any arbitrary graphical
user interface, so you are not limited.


### Multiuser / conflict detection

Though factoryFX realizes a single-source concept for the system's configuration, it does allow for concurrent changes.
It is quite the same as every developer already is used to: Imagine it as something like your source code versioning system.
If someone changes the files in your projects and checks them in, the versioning system will allow for the change if
there are no conflicting intermediate changes. If there are, it will try to merge the conflicts. FactoryFX does the same
so there is no drawback in moving business data from the database to the system configuration.

Indeed the opposite is true: The configuration change is more comprehensive as it includes all data involved, the technical
configuraion and the business data. There are no longer things to distinguish, all changes are carried out following the
same procedure.   

### Edit metadata (Validation,Label,etc)

The factories metadata does not only contain label information. You may add arbitrary validation that you will find much
easier to transfer to your favority programming language than constraint checks and validation code in a remote database.
You can stay in one domain without having to master the gap of different technologies. 

