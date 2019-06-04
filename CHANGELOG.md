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

* **initializr:** One time java code generator for initial project setup to simplify the first steps with factoryfx.

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

  before
  ```
  ...
  "enumAttribute" : {
      "v" : [ "de.abc.ExampleEnum", "VALUE1" ]
  }
  ...
  ```
  new
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
  
