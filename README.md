# Factoryfx

*Data and Dependency Injection Framework for java.*

## Introduction

Factoryfx is a lightweight dependency injection framework that extends dependency injection with data injection.

Factoryfx is a replacement for:
* **manuel dependency injection**
  * Order independence
  * Uniform application structure
* **framework dependency injection (guava, spring, dagger)**
  * Programmatic java API over annotations API
  * Java first
  * Fast startup (no classpath scanning)
* **JConsole MBeans (runtime data/structure changes)**
  * Lifecycle support
  * Metadata for a end user presentable GUI (labels, validation)
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

## Basic example
#### Factory
```java
public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld,HelloWorldFactory> {
    public final StringAttribute text = new StringAttribute().labelText("text");
    @Override
    protected HelloWorld create() {
        return new HelloWorld(text.get());
    }
}
```
[Factory explanation](docu/src/main/java/io/github/factoryfx/docu/factorylayer)
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
[Live object explanation](docu/src/main/java/io/github/factoryfx/docu/liveobjects)
#### Setup
```java
    new FactoryTreeBuilder<HelloWorld,HelloWorldFactory,Void>(HelloWorldFactory.class)
        .addFactory(HelloWorldFactory.class, Scope.SINGLETON, ctx-> {
            HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
            helloWorldFactory.text.set("HelloWorld");
            return helloWorldFactory;
        })
    .microservice().withInMemoryStorage().build().start();
```
## Documentation

### Fundamentals
* [Dependency injection](docu/src/main/java/io/github/factoryfx/docu/dependencyinjection)<br>Injection dependency into a liveobject
* [Data injection](docu/src/main/java/io/github/factoryfx/docu/datainjection)<br>Injection data into a liveobject
* [Factory](docu/src/main/java/io/github/factoryfx/docu/factorylayer)<br>Factory explanation
* [Liveobject](docu/src/main/java/io/github/factoryfx/docu/liveobjects)<br>Liveobject explanation

### Advanced
* [Comparison](docu/src/main/java/io/github/factoryfx/docu/comparison)<br>Comparison to other dependency injection frameworks
* [REST server](docu/src/main/java/io/github/factoryfx/docu/restserver)<br>Basic jetty server with a jersey REST resource
* [Lifecycle](docu/src/main/java/io/github/factoryfx/docu/lifecycle)<br>Lifecycle configuration (start/top/update)
* [Migration](docu/src/main/java/io/github/factoryfx/docu/migration)<br>Configuration data migration
* [Parametrized](docu/src/main/java/io/github/factoryfx/docu/parametrized)<br>Combining runtime data with factory data
* [Persistent storage](docu/src/main/java/io/github/factoryfx/docu/persistentstorage)<br>Persistent configuration data storage using a database
* [Polymorphism](docu/src/main/java/io/github/factoryfx/docu/polymorphism)<br>Polymorphic factories
* [Runtime status](docu/src/main/java/io/github/factoryfx/docu/runtimestatus)<br>Pass runtime status over updates(e.g request counter)
* [Update](docu/src/main/java/io/github/factoryfx/docu/update)<br>Optimise Factory update/Server restart e.g. for liveobjects that need a lot of time for initialization
* [Monitoring](docu/src/main/java/io/github/factoryfx/docu/monitoring)<br>Request monitoring data 
* [Swagger](docu/src/main/java/io/github/factoryfx/docu/swagger)<br>Swagger example 
* [Permissions](docu/src/main/java/io/github/factoryfx/docu/permission)<br>Permissions on attribute level
* [Custom configuration REST API](docu/src/main/java/io/github/factoryfx/docu/customconfig)<br>Configuration over a custom REST API
* [Configuration data](docu/src/main/java/io/github/factoryfx/docu/configurationdata)<br>How to handle configuration data
* [User Interface](docu/src/main/java/io/github/factoryfx/docu/gui)<br>graphical user interface

## Example

[Example](https://github.com/factoryfx/factoryfx/tree/master/example/src/main/java/io/github/factoryfx/example)<br>An example that implements a simplified online store.

## License

Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

## Professional support 

FactoryFx is the long-time development effort of the german company [**SCOOP Software GmbH**](https://www.scoop-software.de/en/). We are a consulting and software company with a constant direction towards state-of-the-art technologies and methods. Our high performance systems are used in multiple medium sized and large enterprises in branches such as online retail, telecommunications, financial services, logistics and local government.

If you are thinking about using FactoryFx in your own project and need more help, **SCOOP Software** is willing to offer **professional support** on a commercial basis. We offer workshops, coaching, or any other help to get your FactoryFx project up and running.