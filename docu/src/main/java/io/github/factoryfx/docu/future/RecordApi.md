# New API based on Records

## Summary

Provide a new Factory API based on Record Feature (JEP 359) to reduce the boilerplate and improve performance.


## Motivation

The current API contains a boilerplate code. It is optimized for the GUI configuration. 
So it is easy to maintain labels and validations. The average server applications do not require such a sophisticated GUI configuration, so the API seems too complicated for the common case.

## Description
Current API
```java
public class Printer {
    public static class PrinterFactory extends SimpleFactoryBase<Printer, RootFactory> {
        public final StringAttribute text = new StringAttribute<>();
        public final FactoryAttribute<TextPrinterFacility, TextPrinterFacilityFactory> textFacility = new FactoryAttribute<>();

        @Override
        protected Printer createImpl() {
            return new Printer(text.get(),text.textFacility.instance());
        }
    }
    
    private final String text;
    private final TextPrinterFacility textPrinterFacility;
    public Printer(String text, TextPrinterFacility textPrinterFacility){
        this.text = text;
        this.textPrinterFacility=textPrinterFacility;
    }

    public void print(){
        this.textPrinterFacility.show(dep.text);
    }
}
```

New API
```java
public class Printer {
    public record Dependency (String text, Factory<TextPrinterFacility.Dependency, TextPrinterFacility> textFacility) implements Dependencies<Printer> {}

    private final Dependency dep;
    public Printer(Dependency dep){
        this.dep = dep;
    }

    public void print(){
        this.dep.textFacility.instance().show(dep.text);
    }
}
```
* API Advantages
  * new Dependencies must only be declared once instead of 2 times
  * no Translation between Attribute types. String vs StringAttribute
  * immutable by default, no extra association in the constructor for each dependency
  * Adding new Dependencies is much easier. (just one change in the record)

* Performance
  * equals should be a lot faster with records instead of the old solution that used reflection

* Memory
  * Old System had high memory consumption because for every attribute an additional attributeobject was created.


## Risks and Assumptions

* Record is a java preview feature and can change (unlikely)
* compatibility, possible to supports both APIs?
* confusion between different API

## Dependencies



