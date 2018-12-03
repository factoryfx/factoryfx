# Custom configuration interface

For some applications it is useful to provide a simplified REST interface for configuration because the standard [**MicroserviceResource**](https://github.com/factoryfx/factoryfx/blob/master/microserviceRestServer/src/main/java/de/factoryfx/microservice/rest/MicroserviceResource.java) interface is too complicated.
In this example we create a REST API that can only change one port.
[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/customconfig)