# FactoryFX

Lightweight dependency and data (re-)injection framework for **all** kinds of Java applications.

## Introduction
* **Lightweight**  
Fully debuggable programmatic Java API (no annotations)  
 Fast startup (no classpath scanning)  

* **Dependency injection**  
Factory based implementation of the dependency injection pattern.

* **Data (re-)injection**  
(Like dependencies) Data can be injected into Java objects.  
Supports OOP and avoids OR mapping or external configuration files.  
Allows optimized (partial) reconfiguration at runtime.  

### Key concepts
* [Key concepts](docu/src/main/java/io/github/factoryfx/docu/keyconcepts)  
Why use FactoryFX?

## Setup

| Group ID            | Artifact ID | Version |
| :-----------------: | :---------: | :-----: |
| io.github.factoryfx | factory  | 2.1.0  |

java 16+ required

### Dependencies
[Gradle example dependency setups](docu/src/main/java/io/github/factoryfx/docu/dependencysetup)

### New project template
[Template](https://github.com/factoryfx/factoryfx-project-template)

## Changelog
[CHANGELOG](CHANGELOG.md)

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
        .addSingleton(PrinterFactory.class, ctx-> {
            PrinterFactory printerFactory = new PrinterFactory();
            printerFactory.text.set("Hello World");
            return printerFactory;
        })
    .microservice().build().start().print();
```
The complete [example](docu/src/main/java/io/github/factoryfx/docu/helloworld) 

## Documentation

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

### Common applications
* [REST server](docu/src/main/java/io/github/factoryfx/docu/restserver)  
Basic jetty server with a jersey REST resource

### Advanced
* [Comparison](docu/src/main/java/io/github/factoryfx/docu/comparison)  
Comparison to other frameworks
* [Lifecycle](docu/src/main/java/io/github/factoryfx/docu/lifecycle)  
Lifecycle configuration (start/stop/update)
* [Migration](docu/src/main/java/io/github/factoryfx/docu/migration)  
Configuration data migration
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
* [Permissions](docu/src/main/java/io/github/factoryfx/docu/permission)  
Permissions on attribute level
* [Custom configuration REST API](docu/src/main/java/io/github/factoryfx/docu/customconfig)  
(Re-)configuration over a custom REST API
* [Configuration data](docu/src/main/java/io/github/factoryfx/docu/configurationdata)  
How to handle configuration data
* [Runtime configuration change](docu/src/main/java/io/github/factoryfx/docu/configurationapi)  
Different ways to change the configuration at runtime
* [Configuration via web application](docu/src/main/java/io/github/factoryfx/docu/configurationwebapp)  
Configuration via web application
* [Reuse Factories in mutiple projects](docu/src/main/java/io/github/factoryfx/docu/reusability)  
Factory with generic RootFactory
* [Encrypted attributes](docu/src/main/java/io/github/factoryfx/docu/encryptedattributes)  
Hiding information from the client like passwords

## Example

[Example](https://github.com/factoryfx/factoryfx/tree/master/example/src/main/java/io/github/factoryfx/example)<br>An example that implements a simplified online store.

## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

## Professional support 

FactoryFX is the long-time development effort of the german company [**SCOOP Software GmbH**](https://www.scoop-software.de/en/). We are a consulting and software company with a constant direction towards state-of-the-art technologies and methods. Our high performance systems are used in multiple medium sized and large enterprises in branches such as online retail, telecommunications, financial services, logistics and local government.

If you are thinking about using FactoryFX in your own project and need more help, **SCOOP Software** is willing to offer **professional support** on a commercial basis. We offer workshops, coaching, or any other help to get your FactoryFx project up and running.
