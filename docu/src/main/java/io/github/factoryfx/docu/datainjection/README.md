# Data injection
In many java applications most data is stored inside a database. 
The data design is determined by the database (relational model, bottom-up approach).
With that architecture it is hard to implement data encapsulation which is one of the main feature of oop.
Often you will end up with data only classes and other classes that implement business logic based on the data classes. 
That will result in a procedural design that does not convey the original motivation begin object oriented programming. 

Factoryfx can directly inject required data into objects.

## Suitable data for data injection?
### Database
Since the framework loads all data into the memory the limit is the available RAM.
Typically a database contains few large tables (e.g more than 100000 rows) and many small tables (e.g less than 10000 rows).
The small tables are good candidates for data injection.
* basic data e.g. Products in a simple shop
* mass data e.g. Orders shop 

### Configuration Data
Typical configuration data are ports, hostname, ssl certificates. Data which are typical stored in property files.


A real world example:<br/>
FTP server need port and host to start the server. The data in this case port and host are inject with the factory.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/datainjection)