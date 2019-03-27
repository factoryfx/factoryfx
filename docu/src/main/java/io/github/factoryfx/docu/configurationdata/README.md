# Configuration data/properties in factoryfx

## Introduction
In factoryfx configuration data are treated like dependencies.
If a liveobject needs configuration data (for example jdbc url for a database) the data is stored in the corresponding Factory.
The basic dependencies injection principle: "Don’t Look For Things" also applies to data. So you don't ask a property storage for the data instead they are passed directly from the factory.

## Example
The example consist of a jetty server with a jersey resource. The jersey resource creates a connection to a database.
For the database connection the resource needs a jdbc url and user/password.

```java
public class DatabaseResource {
    private final String url;
    private final String user;
    private final String password;
    public DatabaseResource(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    @GET
    public String get() {
        //connect with url,user and password and do something with database
        return "";
    }
}

public class DatabaseResourceFactory  extends SimpleFactoryBase<DatabaseResource,Void, RootFactory> {
    public final StringAttribute url = new StringAttribute();
    public final StringAttribute user = new StringAttribute();
    public final StringAttribute password = new StringAttribute();
    @Override
    public DatabaseResource createImpl() {
        return new DatabaseResource(url.get(),user.get(),password.get());
    }
}
```
The configuration data are part of the DatabaseResourceFactory and are passed directly to the DatabaseResource.

## Initial Configuration 
```java
public class Main {
    public static void main(String[] args) {
        FactoryTreeBuilder<RootFactory> builder = new FactoryTreeBuilder<>(RootFactory.class);
        builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new RootFactory())
                .withHost("localhost").widthPort(8005)
                .withResource(ctx.get(DatabaseResourceFactory.class)).build());

        builder.addFactory(DatabaseResourceFactory.class, Scope.SINGLETON, ctx->{
            DatabaseResourceFactory databaseResource = new DatabaseResourceFactory();
            databaseResource.url.set("jdbc:postgresql://host/database");
            databaseResource.user.set("user");
            databaseResource.password.set("123");
            return databaseResource;
        });

        Microservice<Void, Server,RootFactory,Void> microservice = MicroserviceBuilder.buildFilesystemMicroservice(builder.buildTree(),Paths.get("./"));
        microservice.start();
    }
}
```
The initial configuration is defined with the FactoryTreeBuilder. 
To store the configuration we use the use the filesystem storage. That means the configuration is stored in the file: 'currentFactory.json'.
If no configuration is present the initial configuration is created by means of the FactoryTreeBuilder provided. Every consequtive start-up is based on the configuration stored in the file.

Please note that the creating of the initial configuration only happens upon the very first application start. 
Afterwards the configuration changes are carried out with the microservice api which stores the changes persistingly.

## Comparison with property files
For the configuration no additional property files are required. The following example serves as an explanation.

A property file containing the configuration data
```
resource.database.url = "jdbc:postgresql://host/database"
resource.database.user = user
resource.database.password = 123
```
Reading and using the properties.
```java
Properties property = new Properties();
prop.load(...);
...
builder.addFactory(WebResourceFactory.class, Scope.SINGLETON, ctx->{
    String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
    WebResourceFactory webResourceFactory = new WebResourceFactory();
    webResourceFactory.url.set(property.getProperty("resource.database.url"));
    webResourceFactory.user.set(property.getProperty("resource.database.user"));
    webResourceFactory.password.set(property.getProperty("resource.database.password"));
    return webResourceFactory;
});
```

## Problems
There are several problems with this approach.
### Specific problems in this example
* **Only works for initial configuration**<br>
In this example it would only work for the first start. (possible to work-around but cumbersome)
* **Data duplication**<br>
The data is stored in currentFactory.json and in the property file.
The configuration data are duplicated.

### General problems with property files
* **No validation**<br>
User input in the property file is not validated
* **Fantasy structure**<br>
The structure of the ini file is arbitrary and there is no correlation with the real application structure.
* **No change log**<br>
There is no history and documentation of user changes
* **No migration**<br>
There's no ready-made way to migrate old property data.
* **No lifecycle**<br>
To apply changes you have to restart the application.
* **Complex installation process**<br>
The installation process becomes more complicated and therefore automation gets harder.
* **Unused properties**<br>
Hard to detect unused properties and it's hard to figure out where the data is being used.

## But there are only 3 values that can't be that important
A good analogy  is the broken windows theory.
> Consider a building with a few broken windows. If the windows are not repaired, the tendency is for vandals to break a few more windows. Eventually, they may even break into the building, and if it's unoccupied, perhaps become squatters or light fires inside. 

If you already have a property file you will likely add more properties to it. 
For example why not add a property to configure the database schema, a connection timeout, or even a feature switch? Over time you will add more and more properties and they become harder and harder to manage.


## Change the configuration data
### From inside the same jvm
```java
DataAndNewMetadata<RootFactory> update = microservice.prepareNewFactory();
update.root.getResource(DatabaseResourceFactory.class).url.set("jdbc:postgresql://host/databasenew");
microservice.updateCurrentFactory(update,"user","comment",(p)->true);
```
### Rest
To change the configuration from outside there is a REST interface

Add REST interface to the server:
```java
builder.addFactory(RootFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>(new RootFactory())
        .withHost("localhost").widthPort(8005)
        .withResource(ctx.get(SpecificMicroserviceResource.class))
        .withResource(ctx.get(DatabaseResourceFactory.class)).build());
builder.addFactory(SpecificMicroserviceResource.class, Scope.SINGLETON);
```
Using the REST interface with the MicroserviceRestClient.
```java
MicroserviceRestClient<Void, RootFactory, Void> microserviceRestClient = MicroserviceRestClientBuilder.build("localhost", 8005, "", "", RootFactory.class);
DataAndNewMetadata<RootFactory> update = microserviceRestClient.prepareNewFactory();
update.root.getResource(DatabaseResourceFactory.class).url.set("jdbc:postgresql://host/databasenew");
microservice.updateCurrentFactory(update, "user", "comment", (p) -> true);
```

## Complete code
[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/configurationdata)

## Conclusion
Fixing the configuration data problems with the coherent approach implementd in factoryfx is one of it's major advantages.
Thus it makes no sense to combine factoryfx and property files because factoryfx is a superior replacement for property files and duplicated configuration data are harmful.


