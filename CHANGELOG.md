# 2.0.0


### Bug Fixes

### Features

* **migration:** new migration system 
  *  automatic support for removing attributes
  *  FactoryTreeBuilder is used for new attributes
  *  Restoring old data from deleted attributes
  *  easy rename of attributes and factories

* **jetty server:** rework the jetty configuration. The complete jetty structure in represent in factory structure. A new builder can be used for creation.
  
* **builder:** new builder API for microservice setup


### BREAKING CHANGES

* **factory:** removed observer, the observer was not used and complicated the generics declaration. Fix is in most cases to remove Void from generic declaration e.g.: FactoryTreeBuilder<**~~Void~~**,Root,RootFactory,Void>


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
  ````
  
