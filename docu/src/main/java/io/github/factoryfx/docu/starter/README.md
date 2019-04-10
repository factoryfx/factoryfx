# Initial project setup
the starter project provides a code generator that generate the initial project setup.
The generator is a one time java code generator that simplifies the generics declarations.

### Execution
* **InitialProjectSetupGenerator**  
    executes the generator
### Utility classes    
* **ServerBaseFactory**  
  Base factory for all factories in the project
* **ServerRootFactory**  
  Root factory of the project
* **ServerBuilder**  
  Utility class to construct the factory tree
* **FactoryAttribute**  
  adds ServerRootFactory als generic type
* **FactoryListAttribute**  
  adds ServerRootFactory als generic type
* **ServerMain**  
  Application start

### REST server 
* **ExampleResource**  
  Example jersey REST resource
* **ExampleResourceFactory**  
  Factory for the example resource



 

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/starter)