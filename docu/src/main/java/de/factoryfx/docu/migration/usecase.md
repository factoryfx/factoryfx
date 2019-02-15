# Migration
Data migration plays an important role in factoryfx because factoryfx unites data and application structure into one factory structure. Migrations are required not only for data changes but also for application structure changes. 
To simplify the migration process factory provides a dedicated migration API.

There are 3 Layers of Migrations
* **Data storage format**<br> The general format how data and attributes are stored.
* **Data structure**<br> Structure of the Factories e.g.: attributes name, factory names
* **Data populations**<br> Data content changes

## Data storage format migration
```json
{
  "@class" : "de.factoryfx.data.merge.testdata.ExampleDataA",
  "id" : "feee09f0-b6c0-0f93-ee64-1c22364a2630",
  "stringAttribute" : {
    "v" : "adad"
  }
}
```
This is an example for the json format of a factory with a StringAttribute.
The StringAttribute is serialised to nested object and the value is stored in the "v" attribute. 
This unusual structure in an example for the structure format.
(Most of the structure is required for Jackson or workaround for Jackson limitations)
The structure format is mostly stable but may change if, for example Jackson adds a new useful feature in the future.

Data storage format migration can be added to a microservice with MicroserviceBuilder#withGeneralMigration and MicroserviceBuilder#withGeneralStorageMetadata
This should hardly be necessary in practice.

## Data structure migration
The more common case are refactorings in the factory structure. (comparable to refactoring operations in the IDE)
```java
public class ExampleFactory extends SimpleFactoryBase<Void,Void,ExampleFactory> {
    public final StringAttribute oldAttribute= new StringAttribute().labelText("123");
}
public class ExampleFactory extends SimpleFactoryBase<Void,Void,ExampleFactory> {
    public final StringAttribute newAttribute= new StringAttribute().labelText("123");
}
```
In this example the for attribute is renamed from "oldAttribute" to "newAttribute".

```java
FactoryTreeBuilder...
builder.microservice().
withDataMigration(
        (migration)->migration.renameAttribute(ExampleFactory.class,"oldAttribute",(rf)->rf.newAttribute)
).withFilesystemStorage(Files.createTempDirectory("tempfiles")).build();
```
This adds a rename migration. To support multiple renames the new name is provided with a lambada expression and thereby enables IDE refactoring for the migrations. This also prevents rename cycles.

## One time migration
Migration is executed on the fly. advantage faulty migration can't destroy old data

## Data populations
There is no framework support for data populations because it's too project specific.

## Example
[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/migration)