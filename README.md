# Factoryfx

*Data and Dependency Injection Framework for java.*

## Introduction

Factoryfx is a lightweight dependency injection framework that expands dependency injection to include data injection.
Applications built with factory fx can benefit from the stateless design yet allowing for structural and data changes during runtime.

Factoryfx is a replacement for:
* **manual dependency injection**
  * Order independence
  * Uniform application structure
* **framework dependency injection (guava, spring, dagger)**
  * Programmatic java API over annotations API
  * Java first
  * Fast startup (no classpath scanning)
* **JConsole MBeans (runtime data/structure changes)**
  * Lifecycle support
  * Metadata for an end user presentable GUI (labels, validation)
  * Multi-user editing
* **Property files (text,xml,json or yaml)**
  * Change history
  * Change metadata (comment, user, date)
  * Complex validation
* **master data in database**
  * Real data encapsulation in java
  * Immutable business objects

## Setup

| Group ID            | Artifact ID | Version |
| :-----------------: | :---------: | :-----: |
| io.github.factoryfx | factory  | 1.7.6  |

java 11+ required

### Changelog
[CHANGELOG](CHANGELOG.md)

### Dependencies
[Gradle example dependency setups](docu/src/main/java/io/github/factoryfx/docu/dependencysetup)

## Basic example "Hello World" 
#### Factory
```java
public class PrinterFactory extends SimpleFactoryBase<Printer,PrinterFactory> {
    public final StringAttribute text = new StringAttribute();
    @Override
    protected Printer create() {
        return new Printer(text.get());
    }
}
```
[Factory explanation](docu/src/main/java/io/github/factoryfx/docu/factorylayer)
#### Live object
```java
public class Printer{
    private final String text;
    public Printer(String text) {
        this.text = text;
    }
    public void print(){
        System.out.println(text);
    }
}
```
[Live object explanation](docu/src/main/java/io/github/factoryfx/docu/liveobjects)
#### Setup the dependency tree
```java
    new FactoryTreeBuilder<Printer,PrinterFactory,Void>(PrinterFactory.class)
        .addFactory(PrinterFactory.class, Scope.SINGLETON, ctx-> {
            PrinterFactory printerFactory = new PrinterFactory();
            printerFactory.text.set("Hello World");
            return printerFactory;
        })
    .microservice().withInMemoryStorage().build().start().print();
```
The complete [example](docu/src/main/java/io/github/factoryfx/docu/helloworld) 

## Documentation

### Motivation
* [Motivation](docu/src/main/java/io/github/factoryfx/docu/motivation)  
Why use factoryfx?

### Fundamentals
* [Dependency injection](docu/src/main/java/io/github/factoryfx/docu/dependencyinjection)  
Injecting a dependency into a live object
* [Data injection](docu/src/main/java/io/github/factoryfx/docu/datainjection)  
Injection data into a live object
* [Factory](docu/src/main/java/io/github/factoryfx/docu/factorylayer)  
Explanation of factories
* [Liveobject](docu/src/main/java/io/github/factoryfx/docu/liveobjects)  
Explanation of live objects
* [initializr](docu/src/main/java/io/github/factoryfx/docu/initializr)  
Code generator for initial project setup

### Advanced
* [Comparison](docu/src/main/java/io/github/factoryfx/docu/comparison)  
Comparison to other dependency injection frameworks
* [REST server](docu/src/main/java/io/github/factoryfx/docu/restserver)  
Basic jetty server with a jersey REST resource
* [Lifecycle](docu/src/main/java/io/github/factoryfx/docu/lifecycle)  
Lifecycle configuration (start/stop/update)
* [Migration](docu/src/main/java/io/github/factoryfx/docu/migration)  
Configuration data migration
* [Parametrized](docu/src/main/java/io/github/factoryfx/docu/parametrized)  
Combining runtime data with factory data
* [Persistent storage](docu/src/main/java/io/github/factoryfx/docu/persistentstorage)  
Persistent configuration data storage using a database
* [Polymorphism](docu/src/main/java/io/github/factoryfx/docu/polymorphism)  
Polymorphic factories
* [Runtime status](docu/src/main/java/io/github/factoryfx/docu/runtimestatus)  
Passing runtime status across configuration updates (e.g request counter)
* [Update](docu/src/main/java/io/github/factoryfx/docu/update)  
Optimize factory update/server restart e.g. for live objects whose initialization is very time-consuming
* [Monitoring](docu/src/main/java/io/github/factoryfx/docu/monitoring)  
Request monitoring data 
* [Swagger](docu/src/main/java/io/github/factoryfx/docu/swagger)  
Swagger example 
* [Permissions](docu/src/main/java/io/github/factoryfx/docu/permission)  
Permissions on attribute level
* [Custom configuration REST API](docu/src/main/java/io/github/factoryfx/docu/customconfig)  
(Re-)configuration over a custom REST API
* [Configuration data](docu/src/main/java/io/github/factoryfx/docu/configurationdata)  
How to handle configuration data
* [User Interface](docu/src/main/java/io/github/factoryfx/docu/gui)  
graphical user interface

## Example

[Example](https://github.com/factoryfx/factoryfx/tree/master/example/src/main/java/io/github/factoryfx/example)<br>An example that implements a simplified online store.

## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

## Professional support 

FactoryFx is the long-time development effort of the german company [**SCOOP Software GmbH**](https://www.scoop-software.de/en/). We are a consulting and software company with a constant direction towards state-of-the-art technologies and methods. Our high performance systems are used in multiple medium sized and large enterprises in branches such as online retail, telecommunications, financial services, logistics and local government.

If you are thinking about using FactoryFx in your own project and need more help, **SCOOP Software** is willing to offer **professional support** on a commercial basis. We offer workshops, coaching, or any other help to get your FactoryFx project up and running.
