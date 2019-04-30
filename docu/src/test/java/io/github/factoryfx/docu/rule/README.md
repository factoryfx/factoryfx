# Rule
## used for partial integration tests 
FactoryFX supports jUnit rules (jUnit 4) or extensions (jUnit 5) to easily initialize a 
subset of your application.

This example uses the more modern jUnit 5 style with extensions, but the same technique could be used
with jUnit 4 Rules. For lack of a better name, and since the jUnit 5 extension here is supposed to be
used in "jUnit 4 rule style", the extension name uses a "Rule" postfix (`FactoryTreeBuilderRule`).

To demonstrate the techniques, we are implementing a two-tier "hello world" service. A frontend service
offers a `/greetings/greet` service, echoing a greeting to the world. However, we will get the "hello" part
of the greeting from a backend service which, in its regular implementation, is realized by a different
team of developers. Since we do want to test (both, manual developer tests and integration unit tests) our
service before that other team has something to offer, we're implementing a simulator version of the backend
service. We have separated out the client for the backend service in a separate class, `BackendClient`.

In our integration test we will start up the simulator with an ephemeral ("random") port so that running
our test can be done without disturbing other tests (it is not a good idea to use a fixed port - any fixed
port - in a test, especially if it is going to be run on Jenkins or other centralised services). This
random port will then be injected into our `BackendClient` so that it can find the backend service.
(In reality, the rest of the URL, or at least the hostname, will be injected as well.)

We have used an unusual setup of packages, in that there is a separate `tests` subpackage. In real world
scenarios we would instead have separate `src/main/java` and `src/test/java` folders. But this is for demonstration
purposes and tries to keep the relevant files close to each other, with no other docu subpackages obscuring
the view.

The (relevant part of the) live object structure we're looking at is as following:

Simulator:
```java
  org.eclipse.jetty.server.Server
   |
   +--> ServerConnector
   |
   +--> HelloResource
```

Server:
```java
  org.eclipse.jetty.server.Server
   |
   +--> GreetingsResource
         |
         +--> BackendClient
```

Both, simulator and server, have a Builder class for creating the corresponding FactoryTreeBuilder:
`SimulatorBuilder` and `ServerBuilder`. Our `Main` class starts both: the simulator and the server
itself. The simulator uses its standard port, initially configured to 18089, the server uses port 8089.
You could run `Main` and call `http://localhost:8089/greetings/greet` in your browser, getting "hello world"
as the result. 

In our partial integration unit test we want the full simulator operating on an ephemeral (random) port,
we want the BackendClient on the server side to have the actual local port of the simulator data-injected,
and we do not want to instantiate the full server application, instead we're happy with just `GreetingsResource`
and its dependency `BackendClient`. No need for setting up a second jetty Server (which could very well
be done, but we do not *want* one here).

The `FactoryTreeBuilderRule` class capsulates most of what we need for setting up the test. That Rule class
is not tailored for this particular example, it can be reused in other scenarios as-is‌. We're using the
`FactoryTreeBuilderRule` as a 
[jUnit 5 extension with programmatic registration](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-programmatic),
so we add the `@RegisterExtension` annotation.

In our unittest (`GreetingsResourceTest`) we're setting up two `FactoryTreeBuilderRule`s, one for the simulator,
and one for the (partially set up) server. The simulator must be started first (to be able to provide the
actually used port), so we're adding the proper `@Order` annotations as well. (If you're familiar with
jUnit 4 `@Rule` annotations: they do not need `@Order` and always use the order of the fields in the class.)

`FactoryTreeBuilderRule` requires a FactoryTreeBuilder and a `Consumer<FactoryTreeBuilderRule>` 
called `preStart` to configure the setup of the live objects. This `Consumer` is called when jUnit calls
the `BeforeEachCallback` or `BeforeAllCallback`, directly before the "used" branches of the FactoryTree
are started. And exactly this `Consumer` is also the place that defines the actually "used" branches of
our FactoryTree by calling the corresponding `get()` or `getPrototypeInstances()` methods for the
requried factories.

In our case, for the Simulator we're calling `rule.get(HelloJettyServerFactory.class)`, which is the
factory providing the jetty `Server` of the simulator. This is the root object, so we get the full
simulator application for our unit test. In addition, we set the port used by the jetty Server's
first (and only) connector to 0, signalling we're interested in an ephemeral (random) port.

For the server side, since we do not want the full server to be set up, we're just calling 
`rule.get(GreeetingsResourceFactory.class)`, which selects the branch we're interested in:
`GreetingsResource` and `BackendClient`. No jetty `Server` here. If we had a more complicated
application, using only a part (or several parts - we could use separate, completely disjunct
or partially overlapping branches) can save lots of configuration and/or startup time. 

In addition, we fetch the local port of the simulator (i.e. the real port used instead of the port 0 
we configured) and inject it into the `BackendClientFactory`, which in turn injects it into the 
`BackendClient` itself.

It is important to understand the order of things happening here (we're not looking at static 
members annotated with @RegisterExtension here, but the same ideas apply there):

* to execute `@Test testGreeting()`, jUnit creates an instance of `GreetingsResourceTest`.
* during construction of that test class, normal field initialisation happens, so `simulatorCtx`
  and then `serverCtx` are initialized. Note that initializing does not include executing
  the preStart lambdas passed to the constructor of `FactoryTreeBuilderRule`.
* before executing the test, the registered extensions are called in order of the `@Order` annotation,
  so the `beforeEach` method of `simulatorCtx` is called.
  * that method executes the `preStart` `Consumer`, which sets up the jetty server to use an
    ephemeral port and then "uses" the full server (`get(HelloJettyServerFactory.class)`).
  * next, the used branches of the simulator rule (i.e. `HelloJettyServerFactory` and all its 
    dependencies) are started (by the FactoryFX lifecycle management). This actually starts the 
    Jetty server of the simulator, figuring out the local port actually used. 
* next, the `beforeEach` method of `serverCtx` is called.
  * that method again calls the `preStart` `Consumer`, which first fetches the local port of
    the simulator and injects it into the `BackendClientFactory`. Then it "uses" `GreetingsResourceFactory`
    and thus also its dependency, `BackendClientFactory`.
  * next, the used branches of the server rule (i.e. `GreetingsResourceFactory` and its
    dependency) are started, which actually is a no-op. We have not started a second Jetty. 
* next, the test method itself, `testGreeting()`, is executed. It just uses the `GreetingsResource`
  instance that we got (and stored) when we "used" its factory by calling `get(GreetingsResourceFactory.class)`.
  It calls the `greet()` method of our `GreetingsResource` (i.e. the frontend implementation normally
  used as a jetty service resource), which in term calls the `hello()` method of `BackendClient`.
  That `hello()` method then calls the simulator using the local port injected into it, using
  a real HTTP connection, and the simulator returns "hello", which is finally prepended to " world"
  by `greet()`, and the result is then asserted in the test.
* next, the `afterEach` method of `serverCtx` is called. This stops the used live objects, which
  again is a no-op in our case.
* then the `afterEach` method of `simulatorCtx` is called, which stops the jetty server.
* finally, the second test, `testGreetingOnceMore`, is executed, performing the same steps
  again. Note that a new ephemeral port will be chosen by the Jetty started during that test.
  
Now this unit test looks more complicated than it should, doesn't it?

Not really:

* Our live example is already more complicated than a hello world would normally justify.
* Real world applications will be far more complicated than our server side, featuring many more
  live objects. Persistence layers, business logic, several backend clients, etc.
* Simulators are very similar to mocks. Why not use a simulator as a mock, saving work
  in creating a (potentially complicated) mock?
* Setting up the dependencies of such live objects is complicated work already done in our
  `...Builder` classes creating the `FactoryTreeBuilder`s. We do not want to
  recreate even part of all that setup code for a unit test. Remember that we have many of those.
  Using the techniques described here we will reuse as much as possible from our `...Builder` classes,
  also eliminating possible coding errors when recreating part of our live object setup.
* Yes, when testing `GreetingsResource`, we could just mock our whole `BackendClient`. This would
  eliminate the simulator from our unit test. This would also be a *pure* unit test then. But
  integration tests are useful as well, and mocks can be complicated to set up on complicated
  live objects. If you ever used H2 or a [testcontainers](https://www.testcontainers.org/modules/databases/)
  database to test your DAO objects you understood that mocking an SQL server isn't really an
  option, at least not an option you want to code yourself. The same thing can be true for 
  complicated parts of your application: why, if doing so doesn't cost significant time 
  (implementation and runtime), shouldn't you use "the real thing"? This also eliminates errors
  done when mocking objects or when relying on certain behavior of your mocks.
  
  Using the `preStart` `Consumer`s of `FactoryTreeBuilderRule` you can select one or more
  branches to be used in your unittest (`get(... .class)`), you could mock parts of your
  application that you really want to replace with a mock (`mock(... .class, ...)`, and you only have to look at
  those parts of your dependency graph that you're interested in. No need to look at the
  content of your FactoryTreeBuilder each time you write a unit test, no need to refactor
  all of your unit tests when there's a change in your dependency graph, even a change
  in those parts that are used in your test.
  
  You could even create subclasses of `FactoryTreeBuilderRule` defining common additions or
  mock replacements for your unit tests, e.g. for your H2 or testcontainers database setup
  or for simulators as in this example. The `preStart` `Consumer`s can be daisy-chained 
  by `.andThen()`, allowing further local specialisation of your unit test.

A *partial integration test* will at least have to take care (by using mocks or simulators)
of all outside dependencies (backend services, databases, ...), but about everything else,
i.e. everything that is completely realized inside the application, could be used directly.
This is even true for services offered to the outside, however you'll have to configure
these appropriately (e.g. using ephemeral ports). Note that *could* is not *must*. You're
always free to use standard mocks for all or part of your dependencies.

Note that a *partial integration test* as described here is not a replacement for a unit
test of each of your live object classes. In reality, you should add another unit test
for our `BackendClient` class - just testing it alongside `GreetingsResource` is not
sufficient, although it helps tremendously.


[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/test/java/io/github/factoryfx/docu/rule)

More examples (possibly a bit closer to reality) using these techniques can be found in the Shop example:

[**code**](https://github.com/factoryfx/factoryfx/tree/master/example/src/test/java/io/github/factoryfx/example)