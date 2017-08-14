# Factoryfx

*Data and Dependency Injection Framework for java.*

## Introduction

Factoryfx is a lightweight Dependency Injection Framework that combines data and dependency injection.:

dependency injection requires 2 types of objects.
* business logic
* factory code which instantiate the business logic objects

Most dependency injection frameworks try to automate the Factory code.
thats makes it hard to pass configuration data into the application.
Instead of automating the creation process Factoryfx takes a different approach by focusing on an explicit defined lifecycle and end user editable data structure.
Since the Factory is not automated you can edit metadata like validation, labeltext and permissions directly to model.

## Example
### Factory
```java
public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld,Visitor> {
    public final StringAttribute text = new StringAttribute(new AttributeMetadata().labelText("text"));

    @Override
    protected HelloWorld create() {
        return new HelloWorld(text.get());
    }
}
```
### Live object
```java
public class HelloWorld{
    private final String text;

    public Shop(String text) {
        this.text = text;
    }

    public void print(){
        System.out.println(text);
    }
}
```
Why do we need 2 classes? There are 2 conflicting requirements. The Model should be editable but the runtime should have immutable attributes .
That's why the ShopFactory have the shopTitle Attribute with Label text and so on. And the shop liveobject simply have a final shopTitle String field.

## Dependency injection
[datainjection](docu/src/main/java/de/factoryfx/docu/dependencyinjection)



## Data injection
[datainjection](docu/src/main/java/de/factoryfx/docu/datainjection)

### Why domain specificity?
* configuration data
* editable with gui
* no extra format /language

##### Advantages:
* no reflection magic means easy to debug and no surprises at runtime
* configuration included in the factories
* validates at compile-time
* easy Multitenancy support
*

### Configuration Data
With factoryfx you can add user editable data to the factories.
Typical configuration data are ports, hostname, ssl certificates but why not add even more data? Data which are typical in the database.
if you look at typical database you 2 types of data.

* basic data e.g Products in a simple shop
* mass data e.g. Orders shop

#### Data integrity
Copy Data as default fits great to nosql databases.

![Alt text](docu/comparison.png "Optional Title")

### Multitenancy

At least development and test. Difference with implemented with polymorphism.

## User interface


## Setup

| Group ID            | Artifact ID | Version |
| :-----------------: | :---------: | :-----: |
| io.github.factoryfx | factory  | 0.4  |

## Examples

[datainjection](docu/src/main/java/de/factoryfx/docu/datainjection)


## Contribute


## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

