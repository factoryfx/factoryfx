# Data injection

Factorfx handles data in the same way as dependencies.

```java
public class HelloWorld{
    public constructor(String text, Printer printer){
...
    }

    public void print(){
        printer.print(text);
    }
}
```
In this example Printer is a dependency and text is data. Both are injected from the framework.

## Background
In many Java applications store most data in a database. 
The data design is determined by the database (relational model, bottom-up approach).
With that architecture it is hard to implement data encapsulation which is one of the main feature of oop.
Often you will end up with data only classes and other classes that implement business logic based on the data classes. 
That will result in a procedural design that does not convey the original motivation begin object oriented programming. 

```java
public class HelloWorld{
    public constructor(TextDatabase textDatabase, Printer printer){
...
    }

    public void print(){
        printer.print(textDatabase.getText());
    }
}
```
This example shows the same functionality without data injection
FactoryFX can directly inject required data into objects without the indirection in this example.

## Suitable data for data injection
Basically there are two categories of data that are suitable for data injection.
### Database
FactoryFX can not replace databases but the goal is to prefer java based solution as much as possible
Since the framework loads all data into the memory the limit is the available RAM.
Typically a database contains few large tables (e.g more than 100000 rows) and many small tables (e.g less than 10000 rows).
The small tables are good candidates to be replaced with data injection.
#### Examples
* basic data e.g. Products in a simple shop
* mass data e.g. Orders shop 

### Configuration Data
Typical configuration data are ports, hostname, ssl certificates. Data which are typical stored in property files.
#### Examples
* FTP server need port and host to start the server. The data in this case port and host are inject with the factory.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/datainjection)