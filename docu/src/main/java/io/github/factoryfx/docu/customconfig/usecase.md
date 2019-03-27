# Custom configuration interface

You may like to provide a simplified REST interface for configuration because the standard [**MicroserviceResource**](https://github.com/factoryfx/factoryfx/blob/master/microserviceRestServer/src/main/java/io/github/factoryfx/microservice/rest/MicroserviceResource.java) interface is too complicated for the intended use. 
Or the existing configuration interface exposes too many facilities that might be unwanted.  
In this example we create a REST API that restricts configuration changes to change only one tcp port.
[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/customconfig)