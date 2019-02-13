# Migration
Factoryfx combines data and application structure into one factory structure. The disadvantage is that you have migrations effort not only for data changes but also for structure changes. 
To simplify the migrations factory provides a migration API.

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
Example for a factory with a StringAttribute. 
The string values are stored in nested object in the attribute v. That part of the structure format.
(Most of the structure is required form Jackson or workaround for Jackson limitations)
The structure format is mostly stable but may change if, for example Jackson adds a new useful feature.

## Data structure migration
```java
public class ExampleFactory extends SimpleFactoryBase<Void,Void,ExampleFactory> {
    public final StringAttribute oldAttribute= new StringAttribute().labelText("123");
}
public class ExampleFactory extends SimpleFactoryBase<Void,Void,ExampleFactory> {
    public final StringAttribute newAttribute= new StringAttribute().labelText("123");
}
```
This is an example of renaming an attribute.
Example for a attribute rename. 

```java
FactoryTreeBuilder...
builder.microservice().
withDataMigration(
        (migration)->migration.renameAttribute(ExampleFactory.class,"oldAttribute",(rf)->rf.newAttribute)
).withFilesystemStorage(Files.createTempDirectory("tempfiles")).build();
```
This adds a rename migration. To support multiple renames the new name is provided with a lambada expression and thereby enables IDE refactoring for the migrations.

## Data populations
There is no framework support for data populations because it's too project specific.

## Example
[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/migration)