#  Configuration via web application

The FactoryFX web application provides a configuration GUI for changing the configuration attributes.
Design goals are little boilerplate and easy setup to be helpful for beginners. 

dependency:
domFactoryEditing

adding the resource
```java 
.withResource(ctx.get(MicroserviceDomResourceFactory.class)
...
builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON);
```
 
url:
http://{host}:{port}/microservice/index.html


[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/restserver)