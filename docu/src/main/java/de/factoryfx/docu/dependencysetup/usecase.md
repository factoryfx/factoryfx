# Gradle example dependency setups

##### minimal server
```groovy
compile(group: 'io.github.factoryfx', name: 'factory', version: factoryfxVersion)
```

##### postgres storage with javafx gui
###### server
```groovy
compile(group: 'io.github.factoryfx', name: 'factory', version: factoryfxVersion)
compile(group: 'io.github.factoryfx', name: 'javafxDistributionServer', version: factoryfxVersion)
compile(group: 'io.github.factoryfx', name: 'microserviceRestServer', version: factoryfxVersion)
compile(group: 'io.github.factoryfx', name: 'postgresqlStorage', version: factoryfxVersion)
```
###### client
```groovy
compile(group: 'io.github.factoryfx', name: 'factory', version: factoryfxVersion)
compile(group: 'io.github.factoryfx', name: 'javafxFactoryEditing', version: factoryfxVersion)
compile(group: 'io.github.factoryfx', name: 'microserviceRestClient', version: factoryfxVersion)
```

