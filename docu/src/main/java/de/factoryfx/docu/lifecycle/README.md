# Live object lifecycle
Factoryfx supports changing the Factory structure at runtime.
To Customise this process a lifecycle API is provided.

As a build in optimisation the server only execute delta update for the changed live objects.
The lifecycle API can be used to configure reuse for expensive resources like sockets or database pools.

All changes are tracked in a changelog and Custom validation can be added to factories.

Live object need dedicated start and destroy action. <br/>
Examples:<br/>
* http server that open /close sockets.
* database access with connection pool.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/lifecycle)