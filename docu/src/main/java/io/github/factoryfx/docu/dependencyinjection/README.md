# Dependency injection
Objects often need other objects to provide the service they are offering.
The required object dependencies are injected with the framework.

A real world example:<br/>
FTP server needs a port and host to start the server. The data in this case port and host are inject with the factory.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/dependencyinjection)