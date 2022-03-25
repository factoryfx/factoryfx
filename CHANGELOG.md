# 3.0.0

### Features
* **improved factory runtime**
  * improved performance for factory update in the same process

* **http2 jetty server builder:**
  * http2 configuration added

* **clean up javax.rs modules:**
  * replace module java.ws.rs with jakarta.ws.rs, in our project and update used libraries

### BREAKING CHANGES
* java 17 or higher is required

# 2.2.24
* Factories can be marked as catalog item to support semantic copy.

```java
public static class FactoryCatalogItem extends SimpleFactoryBase  {
    public FactoryCatalogItem {
        this.config().markAsCatalogItem();
    }
}
```

# 2.2.16

### BREAKING CHANGES
* **json format**
    * json format now includes null values, v=null (JsonInclude.Include.NON_NULL)
    (normally this should be compatible with the old format) 

# 2.2.10

### Features
* **javafxFactoryEditing**
  * Select copy support
  * show factory usage in order to clarify the implications of data changes
  
### BREAKING CHANGES
* **semantic copy rework**
    semantic now respect the scope from the FactoryTreeBuilder. Nested singleton factories are not copied. 
    The semantic copy configuration on the attributes is removed. This also affect Attributes marked as catalog.
    For catalog attributes you have to make sure that they are registered as singleton in the builder.
    

# 2.2.9

### BREAKING CHANGES
* **ParametrizedObjectCreatorAttribute**
    removed ParametrizedObjectCreatorAttribute because it was not thread safe.

# 2.2.8

### BREAKING CHANGES
* **PolymorphicFactoryAttribute**  
    simplified FactoryPolymorphicAttribute constructor  
    **old**
    ```java
    public final FactoryPolymorphicAttribute<Printer> reference = new FactoryPolymorphicAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    ```
    **new**
    the possible factories are now determined via the FactoryTreeBuilder and therefore need no longer be specified
    ```java
    public final FactoryPolymorphicAttribute<Printer> reference = new FactoryPolymorphicAttribute<>();
    ```
* **PolymorphicFactoryBase**  
    The class is removed and can be replaced with FactoryBase/SimpleFactoryBase 

# 2.2.1

### BREAKING CHANGES
* **EnumAttribute**  
    simplified EnumAttribute constructor 
    **old**
    ```java
    public final EnumListAttribute<TestEnum> enumAttribute= new EnumListAttribute<>(TestEnum.class);
    ```
    **new**
    ```java
    public final EnumListAttribute<TestEnum> enumAttribute= new EnumListAttribute<>();
    ```
        
# 2.2.0

### Features
* **FactoryTreeBuilder migration improvement:**  
  Data changes in the FactoryTreeBuilder are now automatically applied.
  For example if you add a new jersey resource in the builder the new resource is also added to existing configuration after start.
  This simplifies structural application changes.
  
* **migration improvements:**  
  * restore data with path to ListAttribute
  * support for attribute type change

### BREAKING CHANGES

* **redesigned  jetty server builder:**  
  improved builder integration  into the FactoryTreeBuilder

  * factory JettyServerFactory.class registration  
    **old**
    ```java
    addFactory(JettyServerFactory.class, Scope.SINGLETON, context -> new JettyServerBuilder<ServerFactory>()
                .withHostWildcard()
                .withResource(ctx.get(ResourceFactory.class))
                .build());
    ```
    **new**
    ```java          
    builder.addBuilder(ctx->new SimpleJettyServerBuilder<RootFactory>()
            .withHostWildcard()
            .withResource(ctx.get(ResourceFactory.class))      
    ```
    The server can be used in other factory build templates with  ```ctx.get(JettyServerFactory.class)```   
      
  * derived factory from JettyServerFactory.class registration  
    **old**
    ```java
    addFactory(DerivedJettyServerFactory.class, Scope.SINGLETON, context -> new JettyServerBuilder<ServerFactory>()
                .withHostWildcard()
                .withResource(ctx.get(ResourceFactory.class))
                .build());
    ```
    **new**
    ```java          
    builder.addBuilder(ctx->new JettyServerBuilder<Root,RootFactory>(new FactoryTemplateId<>(null, DerivedJettyServerFactory.class), DerivedJettyServerFactory::new)
            .withHostWildcard()
            .withResource(ctx.get(ResourceFactory.class))        
    ```  
  * root factory is JettyServerFactory  
    **new**
    ```java          
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                    .withHost("localhost").withPort(8005)
                    .withResource(ctx.get(ResourceFactory.class))
                );

        builder.addFactory(ResourceFactory.class, Scope.SINGLETON);  
    ```  
    This is the recommended solution if the application is primarily used as jetty/REST server.
   
* **gzip handler used as wrapper**   
  GzipHandler is removed from the HandlerCollection and is now used as a wrapper.
  This affect factory model navigation to resources.
   

# 2.1.0

### Features

* **configuration web application:** new configuration via web application
  [Quickstart](docu/src/main/java/io/github/factoryfx/docu/configurationwebapp)  

### BREAKING CHANGES

* **removed summary generic parameter:** 
  This was intended as simple index for the factory storage but never used.  
  
  example:  
  **old**
  ```java
  FactoryTreeBuilder<Server, ServerRootFactory,Void> serverBuilder = ...
  ```
  **new**
  ```java
  FactoryTreeBuilder<Server, ServerRootFactory> serverBuilder = ...
  ```
  
* **removed root generic parameter in FactoryAttribute:**  
  Root Parameter was used in ```ReferenceBaseAttribute#possibleValueProvider```. Which now requires a cast.
  This affects: FactoryAttribute/FactoryListAttribute/FactoryPolymorphicAttribute/FactoryPolymorphicListAttribute
  
  example:  
  **old** 
  ```java
  FactoryAttribute<ServerRoot,Test,TestFactory> attribute = ...
  ```
  **new**
  ```java
  FactoryAttribute<Test,TestFactory> attribute = ...
  ```

* **renamed subproject**  
  From microserviceRestServer to microserviceRest**Resource**

# 2.0.6

### BREAKING CHANGES
* **JettyServerBuilder:**
removed generic parameter JettyServerBuilder and constructor parameter. To create a derived JettyServerFactory a new buildTo method is added.

    **old**
    ```java
    new JettyServerBuilder<RootFactory,JettyServerDerivedFactory>(new JettyServerDerivedFactory()).withHost("localhost").withPort(8015).build()
    ```
    **new**
    ```java
    new JettyServerBuilder<RootFactory>().withHost("localhost").withPort(8015).buildTo(new JettyServerDerivedFactory())
    ```

# 2.0.5

### Features

* **jetty server:** the builder now supports ObjectMapper configuration.

* **jetty server:** support for thread pool configuration

### BREAKING CHANGES

* **factory:** SimpleFactoryBase method createImpl(); is no protected to avoid accidental misuse.  
 (wrong ```attribute.get().createImpl()``` instead of the correct ``` attribute.instance()``` )

  

# 2.0.0


### Bug Fixes

### Features

* **migration:** new migration system 
  *  automatic support for removing attributes
  *  FactoryTreeBuilder is used for new attributes
  *  Restoring old data from deleted attributes
  *  easy renaming of attributes and factories

* **jetty server:** rework the jetty configuration. The complete jetty structure is now represented in a factory structure. A new builder can be used for creation.
  
* **builder:** new builder API for microservice setup

* **initializr:** One time java code generator for initial project setup to simplify the first steps with FactoryFX.

* **testing:** Added the ability to set a mock liveobject for a factory.

* **data:** Removed the data editing layer (Factory editing still works the same). In the past, factories were a layer above a general data editing layer. The general data editing layer is removed to focus on the factory dependency injection.


### BREAKING CHANGES

* **factory:** removed observer, the observer was not used and overcomplicated the generics declaration. In most cases it is sufficient to remove Void from generic declaration e.g.: FactoryTreeBuilder<**~~Void~~**,Root,RootFactory,Void>

* **module:** rename module and packages from "de.factory" to "io.github.factoryfx"

* **utilityFactory:** rename FactoryBase#utilityFactory from "utilityFactory" to "utility"

* **FactoryTreeBuilder:** the root class registration is now passed as constructor, added duplicate check for factories registration:
  example:
  old
  ```java
    FactoryTreeBuilder<Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);
    factoryTreeBuilder..addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
    ...
    });
  ```
  new
  ```java
      FactoryTreeBuilder<Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class, context -> {
          ....
      });
  ```
* **FactoryTreeBuilder#buildSubTree** is replaced with 
  ```java 
  factoryTreeBuilder.branch().select(BranchFactory.class).factory()
  ```

* **attribute** rename, removed reference from name
  e.g.:FactoryReferenceAttribute=>FactoryAttribute
  
  new names:
  * FactoryAttribute
  * FactoryBaseAttribute
  * FactoryListAttribute
  * FactoryPolymorphicAttribute
  * FactoryPolymorphicListAttribute
  * FactoryListBaseAttribute
  * FactoryViewAttribute
  * FactoryViewListAttribute

# 1.9.0


### Bug Fixes

### Features

* **typescript:** improved typescript generator
  * support for adding custom project specific attributes
  * support for most standard attributes
  
* **attribute:** new attributes
  * InstantAttribute
  * BigIntegerAttribute 

### BREAKING CHANGES

* **attribute:** Removed **Base64Attribute** because jackson automatically converts byte arrays to base 64 and therefore no special attribute is required. 
  Replacement is the **ByteArrayAttribute** or FileContentAttribute
* **json:** Changed EnumAttribute json format. 

  **before**
  ```
  ...
  "enumAttribute" : {
      "v" : [ "de.abc.ExampleEnum", "VALUE1" ]
  }
  ...
  ```
  **new**
  ```
  ...
  "enumAttribute" : {
      "v" : "VALUE1"
  }
  ...
  ```
  Migration
  ```java
  Pattern pattern = Pattern.compile("\"v\" : \\[ \".*\", \"(.*)\" \\]");
  String converted = pattern.matcher(old).replaceAll("\"v\" : \"$1\"");    
  ```
  
* ```collectChildFactoriesDeepFromNode``` replaced width ```collectionChildrenDeepFromNonFinalizedTree```
  
