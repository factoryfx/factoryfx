#Persistentstorage
Update to factories should be permanently stored.

Most other examples use InMemoryFactoryStorage which does not permanently store data changes.
Its usefully for testing or small projects but usually you want to safely store changes.
This example use the PostgresFactoryStorage.