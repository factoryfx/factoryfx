# Factoryfx

*Data and Dependency Injection Framework for java.*

## Introduction

Factoryfx is a lightweight Dependency Injection Framework that extends dependency injection with data injection

Factoryfx is a replacement for the following technologies
* **Factoryfx vs Dependency Injection**<br>
Order independence, uniform structure
* **Factoryfx vs Dependency Injection Frameworks**<br>
Programmatic java API over annotations API.
* **Factoryfx vs JConsole MBeans**<br>
Lifecycle support, end user metadata(labels, validation), multi-user editing
* **Factoryfx vs Property files(text,xml,json or yaml)**<br>
Change history, change metadata (comment, user, date)

## Setup

| Group ID            | Artifact ID | Version |
| :-----------------: | :---------: | :-----: |
| io.github.factoryfx | factory  | 1.7.6  |

java 11+ required

### Dependencies

[Gradle example dependency setups](docu/src/main/java/de/factoryfx/docu/dependencysetup/usecase.md)



## Basic example
### Factory
```java
public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld,Void,HelloWorldFactory> {
    public final StringAttribute text = new StringAttribute().labelText("text");
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

    public HelloWorld(String text) {
        this.text = text;
    }

    public void print(){
        System.out.println(text);
    }
}
```
### Setup
```java
    FactoryTreeBuilder<Void,HelloWorld,HelloWorldFactory,Void> builder = new FactoryTreeBuilder<>(HelloWorldFactory.class);
    builder.addFactory(HelloWorldFactory.class, Scope.SINGLETON, ctx-> {
        HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
        helloWorldFactory.text.set("HelloWorld");
        return rootFactory;
    });

    Microservice<Void,Root,RootFactory,Void> microservice = builder.microservice().withInMemoryStorage().build();
    microservice.start();
```

## Motivation

### Background
Dependency injection requires 2 types of objects.
* business logic
* factory code which instantiate the business logic objects

Most existing dependency injection frameworks try to automate the factory code.
They scan the classpath and create business object tree.

There are a few drawback with that approach:
* A complete automation is not possible. You need additional information provided with annotations e.g to exclude classes/exclude from instantiation or polymorphism. Finally, you program in an annotation dsl instead of java.
* Annotations are not part of the type system and lack tooling
* Classpath scanning and reflection cause confusing stack traces
* Slow startup

### Factoryfx
Instead of annotations factoryfx use a functional java api with factories. 
The user creates factories following a simple structure convention. The factories also provides the dependencies and lifecycle control.

Creating the factories manually offer the following advantages:
#### Advantages:
* no reflection magic means easy to debug and no surprises at runtime
* validation at compile-time
* easy lifecycle control


### Data injection
The major advantage of factoryfx is data injection.

In many java application most data a stored inside a database. The data design is is determined by the database (relational model).
With that architecture it is hard to implement data encapsulation which is one of the main feature of oop.
Often you will end up with data only classes and other classes that implements business logic based on the data classes. That will result in a procedural design which seems wrong in an oop language. 

#### Which kind of data is suitable for injection?
##### Database
Since the framework loads all data into the memory the limit is the available RAM.
Typically a database contains few large tables (e.g more than 100000 rows) and many small tables (e.g less than 10000 rows).
The small tables are good candidates for data injection.
* basic data e.g. Products in a simple shop
* mass data e.g. Orders shop 

##### Configuration Data
Typical configuration data are ports, hostname, ssl certificates. Data which are typical stored in property files.

## Lifecycle control
Data update steps:
1. read the complete current configuration
2. data changes
3. update new the complete configuration

The framework always works with the complete configuration and no data record.
The advantage is easy cross validation over data records and complete historization of configuration changes.
The major disadvantages are addressed by the framework as well.

* server only execute delta update for the changed live objects
* expensive resources like sockets or database pools can be reused


## User interface
### Metadata
Factory supports adding ui metadata to the factories. Examples for metadata are i18n labels or validations.
The factoryfx configuration is exposed
### REST
The rest api supports editing the configuration in json format.
### Client
##### Rich client
Javafx data editing components.
##### Webapp
Typescript code generator.


## Documentation

* [Datainjection](docu/src/main/java/de/factoryfx/docu/datainjection/usecase.md)<br>Injection data into a liveobject
* [Dependencyinjection](docu/src/main/java/de/factoryfx/docu/dependencyinjection/usecase.md)<br>Injection dependency into a liveobject
* [Restserver](docu/src/main/java/de/factoryfx/docu/restserver/usecase.md)<br>Basic jetty server with a jersey REST resource
* [Lifecycle](docu/src/main/java/de/factoryfx/docu/lifecycle/usecase.md)<br>Lifecycle configuration (start/top/update)
* [Migration](docu/src/main/java/de/factoryfx/docu/migration/usecase.md)<br>Configuration data migration
* [Parametrized](docu/src/main/java/de/factoryfx/docu/parametrized/usecase.md)<br>Combining runtime data with factory data
* [Persistentstorage](docu/src/main/java/de/factoryfx/docu/persistentstorage/usecase.md)<br>Persistent configuration data storage using a database
* [Polymorphism](docu/src/main/java/de/factoryfx/docu/polymorphism/usecase.md)<br>Polymorphic factories
* [Runtime status](docu/src/main/java/de/factoryfx/docu/runtimestatus/usecase.md)<br>Pass runtime status over updates(e.g request counter)
* [Update](docu/src/main/java/de/factoryfx/docu/update/usecase.md)<br>Optimise Factory update/Server restart e.g. for liveobjects that need a lot of time for initialization
* [Monitoring](docu/src/main/java/de/factoryfx/docu/monitoring/usecase.md)<br>Request monitoring data 
* [Swagger](docu/src/main/java/de/factoryfx/docu/swagger/usecase.md)<br>Swagger example 
* [Permissions](docu/src/main/java/de/factoryfx/docu/permission/usecase.md)<br>Permissions on attribute level
* [Custom configuration REST API](docu/src/main/java/de/factoryfx/docu/customconfig/usecase.md)<br>Custom configuration REST API
* [Configuration data](docu/src/main/java/de/factoryfx/docu/configurationdata/usecase.md)<br>How to handle configuration data

## Example

[**code**](https://github.com/factoryfx/factoryfx/tree/master/example/src/main/java/de/factoryfx/example)

## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

## Professional support 

FactoryFx is the long-time development effort of the german company [**SCOOP Software GmbH**](https://www.scoop-software.de/en/). We are a consulting and software company with a constant direction towards state-of-the-art technologies and methods. Our high performance systems are used in multiple medium sized and large enterprises in branches such as online retail, telecommunications, financial services, logistics and local government.

If you are thinking about using FactoryFx in your own project and need more help, **SCOOP Software** is willing to offer **professional support** on a commercial basis. We offer workshops, coaching, or any other help to get your FactoryFx project up and running.