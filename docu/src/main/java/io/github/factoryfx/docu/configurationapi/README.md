# Runtime configuration change
There are several ways to change  the configuration at runtime.

## REST
FactoryFX offers a general REST api, which can be used to change the configuration at runtime.
## GUI
Based on the REST-API there are different GUI options available for configuration editing
### Rich client
Javafx data editing components.
##### Advantage
Best integration between server and client because both are implemented in java
### Webapp
[Configuration via web application](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/configurationwebapp)  
##### Advantage
Easy to integrate into the project
##### Disadvantage
Limited customizability
### Typescript
FactoryFX provides a typescript generator that can be used as the basis for a custom Web GUI.
##### Advantage
Flexibility in terms of framework and layout choice.