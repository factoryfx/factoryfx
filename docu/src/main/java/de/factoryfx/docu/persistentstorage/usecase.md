# Persistentstorage
Update to factories should be permanently stored.

Most other examples use InMemoryFactoryStorage which does not permanently store data changes.
Its usefully for testing or small projects but usually you want to safely store changes.
This example use the PostgresFactoryStorage.

[code](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/persistentstorage)