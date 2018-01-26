# Factory update/Server restart
For every factory update the framework creates a new server.
That could be a problem if the creation takes a long time e.g for socket connection, db connections pools, thread pools etc

Therefore the framework offer 2 solutions.
* The framework automatically only recreates the parts that are changed.
* You can pass resource from the old server to the new server and reuse them

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/polymorphism)