# Coordinated configuration for multiple microservices (system tree)

factoryfx keeps one stored configuration per Microservice. Splitting a system into several services
therefore fragments the configuration: no coordinated view, no cross-service update, and typed factory
references end at the process boundary.

This example shows how to get both back without any new framework mechanism, using only the existing
admin REST channel (microserviceRestResource/microserviceRestClient):

* **Control plane**: plain code that authors the system configuration in one place and deploys it to
  every service with the pipeline *fetch → transform → simulate → apply → compensate*.
  * *fetch, never rebuild*: `prepareNewFactory()` returns the service's current tree with stable
    factory ids, so the service-side three-way merge sees only the real delta.
  * *simulate everywhere first*: an invalid configuration is rejected before anything is applied anywhere.
  * *compensate on failure*: if a service rejects the apply (e.g. because an administrator edited it
    concurrently), the already-updated services are reverted to their recorded pre-deploy version.
    A deploy across processes is never atomic — this is preflight plus compensation, not a distributed
    transaction.
* **Typed cross-service reference**: `GreetingApiClientFactory` is injectable like any factory, but its
  live object is a jersey proxy of the shared `GreetingApi` interface (the same pattern the admin channel
  itself uses with `MicroserviceResourceApi`). The control plane writes host/port when it deploys, so the
  wiring between the services is part of the deployed configuration instead of hand-maintained strings.
* **Contract check**: provider and consumer both compute a reflection hash over the shared api interface;
  the control plane compares them before deploying, catching api-jar version skew.

Note the different failure contract of a remote reference: the jersey proxy is created lazily, so factory
updates succeed while the remote service is down, but every invocation can throw
(`jakarta.ws.rs.ProcessingException`). A local factory reference can never fail this way.

## Maven dependency
microserviceRestServer, microserviceRestClient, jettyFactory

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/systemtree)
