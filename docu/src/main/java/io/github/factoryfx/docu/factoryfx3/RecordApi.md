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
    public record Dependency (String text, Dependency<TextPrinterFacility,TextPrinterFacility.Dependency> textFacility) implements Dependencies<Printer> {}

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
  * New dependencies must only be declared once instead of 2 times
  * No translation between Attribute types. String <=> StringAttribute
  * Immutable by default, no extra association in the constructor for each dependency
  * Adding new Dependencies is much easier. (just one change in the record)

* Performance
  * Equals should be a lot faster with records instead of the old solution that used reflection

* Memory
  * Old system had high memory consumption because for every attribute an additional attributeobject was created.


## Risks and Assumptions

* Record is a java preview feature and can change (unlikely)
* Compatibility, possible to supports both APIs?
* If the old api is preserved this can lead to confusion between old and new api

## Record
Advantages of records 
* Records are immutable
    * Java classes can also be immutable, but they require additional boilerplate. Using Records reduce the necessary boilerplate code.
* Records implements equals as default
    * Improved performance compared to an implementation using reflection.

Disadvantages of records 
* Records are immutable
  * This is problematic for data editing in the gui. It's no longer possible to support 2 way binding in the richtclient. That means if you set a value programmatically, the GUI does no update. This is not too bad because in most cases the user edits the data.


