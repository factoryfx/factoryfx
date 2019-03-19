# Factories

Every dependency injection framework needs a way to describe the object creation.

## Complete Automation is not possible
```java
public class HelloWorld {
    public final Dependency dependency;
    public final String text1;
    public final String text2;
   
    public HelloWorld(String text1, String text2, Dependency dependency) {
        this.text1=text1;
        this.text2=text2;
        this.dependency=dependency;
    }
    
    public HelloWorld(Dependency dependency) {
        this("default1","default2",dependency);
    }
}
```
With Reflection only it's only known that there is a constructor with 2 string parameters and a Dependency parameter.
Unknown is:
* Should this class be created with a dependency injection framework? (e.g it could just be a utility class)
* Parameter order, which String for first and second parameter
* which implementation of Dependency should be passed. There could be multiple subclasses of Dependency.
* The Scope of the Parameter. (Singleton, Prototype)
* Which constructor should be used if multiple constructors are available

Those Information needs to be provided to the framework.
Traditionally there are 2 Methods to pass these information:
* Custom DSL (Spring xml)
* Annotations DSL (CDI)

But why not use the new operator? The java built-in path should be the best way to describe object creation and
that's exactly what factoryfx is doing.

## Factory solution
```java
public class HelloWorldFactory extends SimpleFactoryBase<HelloWorld,Void,HelloWorldFactory> {
    public final FactoryReferenceAttribute<Dependency,DependencyFactory> dependency =new FactoryReferenceAttribute<>(DependencyFactory.class);
    public final StringAttribute text1 = new StringAttribute();
    public final StringAttribute text2 = new StringAttribute();
    @Override
    protected HelloWorld create() {
        return new HelloWorld(text1.get(),text2.get(),dependency.instance());
    }
}
```
The Scope ist define with the FactoryTreeBuilder
```java
    FactoryTreeBuilder<HelloWorld,HelloWorldFactory,Void> builder = new FactoryTreeBuilder<>(HelloWorldFactory.class);
    builder.addFactory(HelloWorldFactory.class, Scope.SINGLETON);
    builder.addFactory(DependencyFactory.class, Scope.PROTOTYPE);
```

## Immutable objects vs changes at runtime
The factories allow you to have immutable runtime objects, but they can also be changed at runtime.
You can change the attribute values of factories but not from the corresponding live objects.

## GUI metadata
Factories also provide a GUI metadata API to add Label and validation to factories.
```java
public final StringAttribute text1 = new StringAttribute().en("text1").de("täxt1");
```
This saves mapping effort between multiple files. (template <=> java data class <=> property file)

## Trade-of
For each object a corresponding factory must be created. But this boilerplate code is still better than a new dsl.
HelloWorldFactory is essentially a structure convention which can be misused unintentionally. To validate the structure you can use the FactoryStyleValidator in a unit test. 
[Test example](./../../../../../../../../example/src/test/java/io/github/factoryfx/example/FactoryTest.java)
 