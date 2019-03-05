# Factory update/Server restart
With each factory update, all changed live objects or those affected by changes are recreated.
Any existing runtime status is lost. (A simple example for runtime is a request counter.)
To keep the status you can define a reCreator and pass the status from the previous liveobject.


[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/runtimestatus)