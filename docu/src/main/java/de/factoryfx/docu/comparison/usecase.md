# Comparison to other dependency injection frameworks
Dependency injection requires 2 types of objects.
* business logic
* factory code which instantiate the business logic objects

Most existing dependency injection frameworks try to automate the factory code.
They scan the classpath and create business object tree.

There are a few drawback with that approach:
* A complete automation is not possible. You need additional information provided with annotations e.g to exclude classes/exclude from instantiation or polymorphism. Finally, you program in an annotation dsl instead of java.
* Annotations are not part of the type system and lack tooling
* Classpath scanning and reflection cause confusing stack traces
* Slow startup

## Factoryfx
Instead of annotations factoryfx use a functional java api with factories. 
The user creates factories following a simple structure convention. The factories also provides the dependencies and lifecycle control.

The programmatic API has the following advantages:
### Advantages:
* no reflection magic means easy to debug and no surprises at runtime
* validation at compile-time
* easy lifecycle control
