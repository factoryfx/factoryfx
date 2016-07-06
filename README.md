# Factoryfx

*Domain specificity Dependency Injection Framework for java.*

## Introduction

Factoryfx is a lightweight Dependency Injection Framework.:

when you manually use dependency injection you have 2 types of objects.
* business logic
* Factory code which instantiate the business logic objects
explained here: https://www.youtube.com/watch?v=RlfLCWKxHJ0&index=3&list=PL693EFD059797C21E#t=30m

Most dependency injection frameworks automate the Factory code.
Instead of automating the creation process Factoryfx takes a different approach by focusing on an explicit defined lifecycle and end user ediable datastructure.

###Example
####Factory
```java
public class ShopFactory extends FactoryBase<Shop,ShopFactory> {
    public final StringAttribute shopTitle = new StringAttribute(new AttributeMetadata().labelText("stageTitle"));

    @Override
    protected Shop createImp(Optional<Shop> previousLiveObject) {
        ...
        return new Shop(stageTitle.get(), ...);
    }
}
```
####Live Object
```java
public class Shop  implements LiveObject<OrderCollector> {
    private final String shopTitle;

    public Shop(String shopTitle, ...) {
        this.stageTitle = stageTitle;
    }

    @Override
    public void start() {
        ...
    }

    @Override
    public void stop() {
        ...
    }

    @Override
    public void accept(OrderCollector visitor) {
        ...
    }
}
```
Why do we need 2 classes? There are 2 conflicting requirements. The Model should be editable but during the in the running application the attributes should be final.
That's why the ShopFactory have the shopTitle Attribute with Label text and so on. And the shop liveobject simply have a final shopTitle String field.

###Why domain specificity?
* configuration data
* editable with gui
* no extra format /language

###Example

#####Advantages:
* no reflection magic means easy to debug and no surprises at runtime
* configuration included in the factories
* validates at compile-time
* easy Multitenancy support
*

###Configuartion Data
With factoryfx you can add user editable data to the factories.
Typical configuration data are ports, hostname, ssl certificates but why not add even more data? Data which are typical in the database.
if you look at typical database you 2 types of data.

* basic data e.g Products in a simple shop
* mass data e.g. Orders shop

####Data integrity
Copy Data as default fits great to nosql databases.

![Alt text](docu/comparison.png "Optional Title")

###Multitenancy

At least development and test. Difference with implemented with polymorphism.

##User interface


##Setup

TODO maven

##Usage

##Contribute


##License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

