# Factoryfx

*Data and Dependency Injection Framework for java.*

## Introduction

Factoryfx is a lightweight dependency injection framework that extends dependency injection with data injection

Factoryfx is a replacement for:
* **manuel dependency injection**<br>
Order independence, uniform structure
* **framework dependency injection**<br>
Programmatic java API over annotations API, java first
* **JConsole MBeans (runtime data/structure changes)**<br>
Lifecycle support, metadata for a end user presentable GUI (labels, validation), multi-user editing
* **Property files (text,xml,json or yaml)**<br>
Change history, change metadata (comment, user, date), complex validation
* **master data in database**<br>
real data encapsulation in java, immutable business objects

## Setup

| Group ID            | Artifact ID | Version |
| :-----------------: | :---------: | :-----: |
| io.github.factoryfx | factory  | 1.7.6  |

java 11+ required

### Changelog
[CHANGELOG](CHANGELOG.md)

### Dependencies
[Gradle example dependency setups](docu/src/main/java/de/factoryfx/docu/dependencysetup/usecase.md)

## Basic example
#### Factory
```java
public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld,Void,HelloWorldFactory> {
    public final StringAttribute text = new StringAttribute().labelText("text");
    @Override
    protected HelloWorld create() {
        return new HelloWorld(text.get());
    }
}
```
[Factory explanation](docu/src/main/java/de/factoryfx/docu/factorylayer/usecase.md)
#### Live object
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
[Live object explanation](docu/src/main/java/de/factoryfx/docu/liveobjects/usecase.md)
#### Setup
```java
    new FactoryTreeBuilder<Void,HelloWorld,HelloWorldFactory,Void>(HelloWorldFactory.class)
    .addFactory(HelloWorldFactory.class, Scope.SINGLETON, ctx-> {
        HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
        helloWorldFactory.text.set("HelloWorld");
        return helloWorldFactory;
    })
    .microservice().withInMemoryStorage().build().start();
```
## Documentation

* [Factory](docu/src/main/java/de/factoryfx/docu/factorylayer/usecase.md)<br>Factory explanation
* [Liveobject](docu/src/main/java/de/factoryfx/docu/liveobjects/usecase.md)<br>Liveobject explanation
* [Comparison](docu/src/main/java/de/factoryfx/docu/comparison/usecase.md)<br>Comparison to other dependency injection frameworks
* [Data injection](docu/src/main/java/de/factoryfx/docu/datainjection/usecase.md)<br>Injection data into a liveobject
* [Dependency injection](docu/src/main/java/de/factoryfx/docu/dependencyinjection/usecase.md)<br>Injection dependency into a liveobject
* [REST server](docu/src/main/java/de/factoryfx/docu/restserver/usecase.md)<br>Basic jetty server with a jersey REST resource
* [Lifecycle](docu/src/main/java/de/factoryfx/docu/lifecycle/usecase.md)<br>Lifecycle configuration (start/top/update)
* [Migration](docu/src/main/java/de/factoryfx/docu/migration/usecase.md)<br>Configuration data migration
* [Parametrized](docu/src/main/java/de/factoryfx/docu/parametrized/usecase.md)<br>Combining runtime data with factory data
* [Persistent storage](docu/src/main/java/de/factoryfx/docu/persistentstorage/usecase.md)<br>Persistent configuration data storage using a database
* [Polymorphism](docu/src/main/java/de/factoryfx/docu/polymorphism/usecase.md)<br>Polymorphic factories
* [Runtime status](docu/src/main/java/de/factoryfx/docu/runtimestatus/usecase.md)<br>Pass runtime status over updates(e.g request counter)
* [Update](docu/src/main/java/de/factoryfx/docu/update/usecase.md)<br>Optimise Factory update/Server restart e.g. for liveobjects that need a lot of time for initialization
* [Monitoring](docu/src/main/java/de/factoryfx/docu/monitoring/usecase.md)<br>Request monitoring data 
* [Swagger](docu/src/main/java/de/factoryfx/docu/swagger/usecase.md)<br>Swagger example 
* [Permissions](docu/src/main/java/de/factoryfx/docu/permission/usecase.md)<br>Permissions on attribute level
* [Custom configuration REST API](docu/src/main/java/de/factoryfx/docu/customconfig/usecase.md)<br>Configuration over a custom REST API
* [Configuration data](docu/src/main/java/de/factoryfx/docu/configurationdata/usecase.md)<br>How to handle configuration data
* [User Interface](docu/src/main/java/de/factoryfx/docu/gui/usecase.md)<br>graphical user interface

## Example

[Example](https://github.com/factoryfx/factoryfx/tree/master/example/src/main/java/de/factoryfx/example)<br>An example that implements a simplified online store.

## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

## Professional support 

FactoryFx is the long-time development effort of the german company [**SCOOP Software GmbH**](https://www.scoop-software.de/en/). We are a consulting and software company with a constant direction towards state-of-the-art technologies and methods. Our high performance systems are used in multiple medium sized and large enterprises in branches such as online retail, telecommunications, financial services, logistics and local government.

If you are thinking about using FactoryFx in your own project and need more help, **SCOOP Software** is willing to offer **professional support** on a commercial basis. We offer workshops, coaching, or any other help to get your FactoryFx project up and running.