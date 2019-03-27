# Persistent storage
Update to factories should be persisted.

Most other examples use InMemoryFactoryStorage which does not store data changes across restarts.
It is usefully for testing or small projects but usually you want to safely store changes that survive an application restart.
This example uses the PostgresFactoryStorage.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/io/github/factoryfx/docu/persistentstorage)