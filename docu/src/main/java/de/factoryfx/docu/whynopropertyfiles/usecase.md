# Configuration data/properties in factoryfx

##Introduction
In factoryfx configuration data are treated like dependencies.
If a liveobject needs configuration data the data is stored in the corresponding Factory.
(for example jdbc url for a database).
The basic dependencies injection principle: "Don’t Look For Things" also applies to data. So you don't ask a property storage for the data instead they are passed directly from the factory.

##Example
The example consist of a jetty server with a jersey resource. The jersey resource creates a connection to a database.
For the database connection the resource needs a jdbc url and user/password.

```java
public class SimpleHttpServer extends JettyServerFactory<Void, SimpleHttpServer> {
    public final FactoryReferenceAttribute<WebResource,WebResourceFactory> resource = new FactoryReferenceAttribute<>(WebResourceFactory.class);

    @Override
    protected void setupServlets(ServletBuilder servletBuilder) {
        defaultSetupServlets(servletBuilder,resource.instance());
    }
}

public class WebResourceFactory extends SimpleFactoryBase<WebResource,Void, SimpleHttpServer> {

    public final StringAttribute url = new StringAttribute();
    public final StringAttribute user = new StringAttribute();
    public final StringAttribute password = new StringAttribute();

    @Override
    public WebResource createImpl() {
        return new WebResource(url.get(),user.get(),password.get());
    }

}
```
The Configuration is part of the WebResourceFactory and the configuration data is passed directly to the WebResource.

The main method looks like this:
```java
public class Main {
    public static void main(String[] args) {
        FactoryTreeBuilder<SimpleHttpServer> builder = new FactoryTreeBuilder<>(SimpleHttpServer.class);
        builder.addFactory(SimpleHttpServer.class, Scope.SINGLETON,ctx->{
            SimpleHttpServer simpleHttpServer=new SimpleHttpServer();
            HttpServerConnectorFactory<Void, SimpleHttpServer> serverConnectorFactory = new HttpServerConnectorFactory<>();
            serverConnectorFactory.host.set("localhost");
            serverConnectorFactory.port.set(8005);
            simpleHttpServer.connectors.add(serverConnectorFactory);
            simpleHttpServer.resource.set(ctx.get(WebResourceFactory.class));
            return  simpleHttpServer;
        });
        builder.addFactory(WebResourceFactory.class, Scope.SINGLETON, ctx->{
            String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
            WebResourceFactory webResourceFactory = new WebResourceFactory();
            webResourceFactory.url.set("jdbc:postgresql://host/database");
            webResourceFactory.user.set("user");
            webResourceFactory.password.set("123");
            return webResourceFactory;
        });

        Microservice<Void,JettyServer,SimpleHttpServer,Void> microservice = MicroserviceBuilder.buildFilesystemMicroservice(builder.buildTree(),(Paths).get("./"));
        microservice.start();
    }
}
```
The initial configuration is defined with the FactoryTreeBuilder. 
To save the configuration we use the use the Filesystem. That means the Configuration is stored in the file: 'currentFactory.json'.
For the first start the configuration created from the FactoryTreeBuilder is used. For teh following starts the configuration stored in the file is used.


##Comparison with property
A property file containing the configuration would look like this.
```
resource.database.url = "jdbc:postgresql://host/database"
resource.database.user = user
resource.database.password = 123
```
Reading would like this.
```java
public class Main {
    public static void main(String[] args) {
...
        load property
...
        builder.addFactory(WebResourceFactory.class, Scope.SINGLETON, ctx->{
            String time = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm:ss.SSS").toFormatter().format(LocalDateTime.now());
            WebResourceFactory webResourceFactory = new WebResourceFactory();
            webResourceFactory.url.set(property.getProperty("resource.database.url"));
            webResourceFactory.user.set(property.getProperty("resource.database.user"));
            webResourceFactory.password.set(property.getProperty("resource.database.password"));
            return webResourceFactory;
        });
...
    }
}
```
##Problems
There are several problems with this approach.
###Specific problems in this example
* **Only works for initial configuration**
In this example it would only work for the first start. (possible to work around but cumbersome)
* **Data duplication**<br>
The data is stored in configuration.json and in the property file.
The configuration is duplicated.

###General problems
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
the installation process becomes more complicated and automation gets harder.

##But there are only 3 values that can't be that important
A good comparision is the broken windows theory.
> Consider a building with a few broken windows. If the windows are not repaired, the tendency is for vandals to break a few more windows. Eventually, they may even break into the building, and if it's unoccupied, perhaps become squatters or light fires inside. 

If you already have a property file you will likely add more properties to it. For example why not add a property to configure the database schema, a connection timeout, or even a feature switch? Over time you will add more and more properties and they become harder and harder to manage.

##But can't i just use a libary or different format to fix it?
Can i just fix the problems by using a different format like yaml, xml, json etc? Or by using a property file library? 
No! Although some problems can be fixed with those technologies non of them fixes all.
It's an architecture problem that can't be solved by just focusing on the property file.

##Exception from the rule

##But how do i change the configuration without a property file

#Conclusion
When using factoryfx many users still use property files. That's wrong because factoryfx is a superior replacement for property files.
