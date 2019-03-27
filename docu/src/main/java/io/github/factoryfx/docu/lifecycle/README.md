# Live object lifecycle
Factoryfx supports changing the factory structure at runtime.
To customize this process a lifecycle API is provided.

As a built-in optimization the server only executes delta update for the changed live objects.
The lifecycle API can be used to configure reuse for expensive resources like sockets or database pools.

All changes are tracked in a changelog and custom validations can be added to factories.

Some live objects need dedicated start and destroy action. <br/>
Examples:<br/>
* http server that open /close sockets.
* database access with connection pool.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/lifecycle)