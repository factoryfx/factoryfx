# Custom configuration interface

You may like to provide a simplified REST interface for configuration because the standard [**MicroserviceResource**](https://github.com/factoryfx/factoryfx/blob/master/microserviceRestServer/src/main/java/io/github/factoryfx/microservice/rest/MicroserviceResource.java) interface is too complicated for the intended use. 
Or the existing configuration interface exposes too many unwanted settings.  

In this example, a REST API is created that provides a simplified configuration.
The simplified configuration only allows changing the tcp port.

## Maven dependency
jettyFactory

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/customconfig)