# 5.0.0

## Breaking changes
* **Microservice / FactoryTreeBuilder** (rebuild merge on start)
  * the current configuration replaces the initial configuration as the reference for the treeBuilder rebuild merge. The FactoryTreeBuilder describes the technical configuration of the tree: on start it only contributes newly introduced factories (templates without an instance in the current configuration) including their wiring into empty references. Existing factories keep their current values and wiring, user added factories are preserved and factories removed from the builder are not removed from the current configuration. Builder default value changes are no longer propagated to existing configurations, use the new configuration patches for data changes
* **DataStorage**
  * the current configuration is now stored as compact json (previously pretty printed): FileSystemDataStorage currentFactory.json, PostgresDataStorage currentconfiguration, OracledbDataStorage FACTORY_CURRENT. The format change is cosmetic, all readers are whitespace-agnostic and previously stored pretty files remain readable
  * updateCurrentData contract clarified: implementations must not retain a reference to update.root after the method returns (the caller may pass the live factory tree). Custom DataStorage implementations that store the object itself must copy it
  * DataStoragePatcher is removed, patchAll/patchCurrentData now take the same ConfigurationPatch interface used for the patches registered on the MicroserviceBuilder. The patch callback receives the factory json wrapped as DataJsonNode (generic factory api, previously a raw ObjectNode) and the metadata as typed StoredDataMetadata (previously a raw JsonNode). Changes to StoredDataMetadata.configurationSchemaVersion and to the dataStorageMetadataDictionary are persisted, the rest of the metadata is read-only
  * DataJsonNode.getJsonNode() now returns ObjectNode (previously JsonNode), removing the cast every raw patch needed
  * DataStorage.patchCurrentData is removed and patchAll is demoted to the persistence primitive behind MicroserviceDeployment.persistConfigurationPatches: patching is declared on the MicroserviceBuilder (withPatch, applied in-memory on every load) and persisted explicitly via persistConfigurationPatches, the storage is no longer patched directly
  * new method updateCurrentDataRaw(RawFactoryDataAndMetadata): store a raw configuration (json + complete metadata) unchanged as the new current configuration, counterpart of getCurrentDataRaw. implemented for filesystem/postgres/oracle, in-memory throws UnsupportedOperationException, custom DataStorage implementations must implement it

## Improved performance on saving configurations
* **Microservice / FactoryManager** (configuration update)
  * updateCurrentFactory/simulateUpdateCurrentFactory: when the update is based on the current configuration (the usual case, baseVersionId is the current version) the common version for the merge is an in-memory copy of the running factory tree instead of being read and deserialized from the storage
  * the merge no longer creates an additional deep copy of the running factory tree for the diff previous state when the update is based on the current configuration (the common version is reused), same for the treeBuilder rebuild merge on start. New DataMerger.createMergeResult(permissionChecker, commonVersionEqualsCurrent) overload
  * FactoryManager.update via merge no longer re-finalises the factory tree and re-runs the loop detection and lifecycle-order collection a second time after the merge (the merge already does this), only the direct-mutation update(FactoryUpdate) still does
* **DataStorage** (FileSystemDataStorage, PostgresDataStorage, OracledbDataStorage)
  * root and metadata are serialized once per save and reused for the current and the history record (previously serialized twice)
* **FileSystemDataStorage**
  * getCurrentDataId no longer deserializes the whole configuration, the id is cached and read from the metadata file only
* **Microservice**
  * updateCurrentFactory no longer creates a defensive deep copy of the factory tree before storing it
* **InMemoryDataStorage**
  * fix: updateCurrentData stored a live reference to the factory tree, later in-place updates could corrupt stored history (now copies)

## Server validation
* **Attribute / FactoryBase**
  * new validation category executed ONLY on the server, never in editor clients: Attribute.serverValidation(validation) and config().addServerValidation(validation, dependencies) (factory-level, at least one dependency attribute required). For validations that need server resources or are too expensive to run while typing. Server validations run against a finalised but not-started factory tree, so they must only inspect attribute values. The required/nullable check stays a client validation
  * existing validation(...) rules are unchanged: editor clients + preflight check + treeBuilder build-time validation, they still do NOT run on configuration updates
* **Microservice**
  * updateCurrentFactory: runs the server validations on the submitted factory tree BEFORE the merge and REJECTS the update on failure: no liveObject change, no persistence, the returned FactoryUpdateLog carries the errors (failedValidation()). Covers revertTo and snapshot restore while started as well. Previously configuration updates ran no validation at all
  * simulateUpdateCurrentFactory: reports the server validation errors in MergeDiffInfo.validationErrors without affecting the merge result
  * update(FactoryUpdate) (in-JVM programmatic self-update) is intentionally NOT validated: it is the application's own trusted code, server validation guards externally submitted configurations
* **MicroserviceDeployment**
  * preflightCheck reports server validation errors of the stored configuration in addition to the normal validation errors ("server validation error" problems)
  * snapshot restore while NOT started stays unvalidated by design (rollback safety net must not be blockable, the preflight check is the pre-start verification)
* **FactoryUpdateLog / MergeDiffInfo**
  * new field validationErrors (List of error descriptions, additive and empty for old payloads) and FactoryUpdateLog.failedValidation() / MergeDiffInfo.hasValidationErrors(). MergeDiffInfo.successfullyMerged() is unaffected by validation errors
  * note: the javafx FactoryUpdateLogWidget does not display validationErrors yet, FactoryUpdateLog.dumpError includes them

## Preflight checks and configuration snapshots
* **Microservice / MicroserviceDeployment**
  * new class MicroserviceDeployment, accessed via Microservice.deployment(): home of the deployment tooling below plus persistConfigurationPatches. intended for deployment scripts/tooling, the Microservice class itself stays the runtime API (start/stop/update/history)
  * preflightCheck(): verify WITHOUT starting the application that this software version can work with the stored configuration: the current configuration deserializes through the regular load path (patches, migrations, json binding), the factory tree has no validation errors and the treeBuilder rebuild succeeds. Returns a report with all found problems instead of failing on the first one, intended for deployment tooling before switching to a new software version
  * preflightCheck(PreflightCheckOptions): additional checks are selected via the options object. PreflightCheckOptions.includeHistory() additionally verifies that every history configuration can be loaded. PreflightCheckOptions.createLiveObjects() additionally creates all liveObjects (the factories' create phase, on the tree the start would use including the treeBuilder rebuild merge) WITHOUT starting them; the created objects are discarded (no start, no destroy — per the lifecycle contract external resources are claimed in start)
  * saveConfigurationSnapshot(Path): save the current configuration as stored (raw json, no patches/migrations applied) to a file. Since the stored form is unchanged the snapshot can also be restored by an older software version, making it a rollback safety net before an upgrade
  * loadConfigurationSnapshot(Path): restore a snapshot through the regular load path (patches and migrations apply), stored as a new configuration version with the existing history preserved. Updates the running application when started
  * loadConfigurationSnapshotRaw(Path): restore a snapshot WITHOUT baking the registered patches and migrations into the stored form: the snapshot json and its configuration schema version are stored unchanged as a new configuration version, so patches keep applying on load as usual and the stored configuration remains readable by an older software version. Updates the running application when started (loaded through the regular load path, patches apply in-memory)
* **DataStorage**
  * new method getCurrentDataRaw(): the current configuration as stored (raw json + metadata), implemented for filesystem/postgres/oracle (in-memory throws UnsupportedOperationException)

## More reliable configuration migration
* **Configuration patches** (applied dynamically on load, run-once or every-time)
  * new builder api MicroserviceBuilder.withPatch: registered patches are applied in-memory whenever a configuration (current or history) is loaded, the stored files are not modified
  * patches run after the declarative migrations (rename/retype), the removed/retyped attribute handling and the definition relocation: they always see the stored configuration lifted to the current model shape, so patches that roundtrip through the current factory classes are safe by construction
  * withPatch(fromVersion, toVersion, patch): version-gated patch with run-once semantics based on the new configuration schema version stored in the metadata (StoredDataMetadata.configurationSchemaVersion, previously stored configurations without a version are treated as version 0). Patches chain in version order, the chain is validated in MicroserviceBuilder.build
  * withPatch(patch): patch applied on every load regardless of version
  * withConfigurationSchemaVersion(version): declare the schema version written on save (optional, defaults to the highest patch toVersion)
  * Microservice.deployment().persistConfigurationPatches(): explicitly applies the declarative migrations and the registered patches to all stored configurations (same order as on load) and writes the results including the updated metadata dictionary and the bumped schema version back to the storage, afterwards version-gated patches self-skip
  * loading a configuration with a newer schema version than the application declares logs a warning and skips version-gated patches (every-time patches still run)
* **Retype migrations** (changing an attribute's type no longer drops the stored data)
  * new builder api MicroserviceBuilder.withRetypeAttributeMigration / withRetypeListAttributeMigration: convert the previously stored value to the new attribute type with a converter function
  * automatic conversions without a registered migration for the built-in scalar attribute types: single value to list, list with one element to single value, number/boolean to string, string to number/boolean when parseable. Lossy or unknown conversions clear the value as before, with a warning naming the factory class, attribute and the migration api to register
  * automatic conversions also cover reference attributes when the reference class is unchanged: reference list to single reference (the first element is used, further elements are dropped with a warning), single reference to list, and pass-through for a changed attribute class with the same shape (e.g. a custom list attribute replaced with FactoryListAttribute)
  * withRenameAttributeMigration no longer clobbers the target attribute when old and new attribute coexisted during a transition: an existing value of the target attribute wins, the stored value of the old attribute is dropped with a warning. Combined with the reference conversions this migrates a "deprecated list attribute replaced by a differently named single reference" without a hand-written patch
* **Removed attributes/classes no longer lose referenced factories**
  * references are serialized first-occurrence-defines (JsonIdentityInfo): when the first occurrence (the full definition) sat inside a removed attribute or a factory of a removed class, the definition was silently dropped and the remaining id references dangled (previously only partially rescued by an exception-driven fallback, and not at all inside patches that roundtrip through the current factory classes)
  * on load such definitions are now relocated to their first remaining reference before the registered configuration patches run; definitions not referenced anywhere else are dropped with a warning
* **Attribute class rename migrations** (renaming/moving a custom attribute class no longer drops the stored data)
  * new builder api MicroserviceBuilder.withRenameAttributeClassMigration: for a renamed or moved custom attribute class whose serialized form is unchanged (e.g. an attribute class moved to a shared library). Rewrites the attribute class name recorded in the stored metadata dictionary so the attribute is not detected as retyped (which would clear the stored values)
  * new api DataStorageMetadataDictionary.retypeAttributeClass(previousName, newName): the underlying primitive, usable from a ConfigurationPatch for storage-level one-time migrations

# 4.1.10
* **FactoryTreeBuilder**
    * fix: builders added via addBuilder() were only applied inside buildTreeUnvalidated(), so buildNewSubTree/buildSubTree/getScope failed with "builder missing Factory" when called before the tree was ever built. This broke server startup with existing stored data after 4.1.8 added the errorHandler attribute to JettyServerFactory: MigrationManager.read -> fillNewAttributes -> buildNewSubTree(root) hit the unapplied nested root creator of jetty-based builders. Nested builders are now applied lazily and exactly once from all context lookups

# 4.1.9
* **FactoryEditView**
    * fix listener accumulation: the FactoryRootChangeListener was registered on every view open, so after n opens a single save fired the widget's edit() n times; the listener is now registered once
    * toolbar and root pane are built once instead of on every view open
    * edit() deduplication: content.edit(root) is now called at most once per root instance (initial load and each save/update); re-opening a view with an unchanged factory no longer calls edit(). createContent() is still called on every open and remains the per-open signal for widgets to refresh non-factory data. Note: widgets that relied on the per-open edit() call to rebuild their UI now keep the previously built UI (including its state) across re-opens
* **LifecycleFactoryAwareWidget** (new)
    * opt-in base class for FactoryAwareWidget that splits the lifecycle into initContent() (once), refreshData() (every open and after every new factory version, started in parallel with the initial factory download) and applyFactory() (once per new root instance), so views can display and load data before the factory download completes
* **FactoryAwareWidget**
    * document the lifecycle contract

# 4.1.8
* **JettyServerFactory**
    * add support for setting a jetty error handler (Server.setErrorHandler), configurable via JettyServerBuilder.withErrorHandler

# 4.1.7
* **Update dependencies**
* **Remove copper bridge**
* **ValueListAttributeVisualisation**
    * minor improvements

# 4.1.6
* **ValueListAttributeVisualisation**
    * fix readOnly not being applied to value list attribute visualization

# 4.1.5
* **DataStorage**
  * add lightweight metadata for factory history (changeSummary == null && dataStorageMetadataDictionary == null)
* **MicroserviceResource**
  * getHistoryFactoryList returns factories with lightweight metadata by default

# 4.1.4
* **Microservice**
  * modify config revert message to show timestamp and comment instead of id
* **SimpleObjectMapperTest**
  * add Windows OS compatibility to output style test

# 4.1.3
* **SelectFactoryDialog**
    * add filter text field

# 4.1.2
* **OracledbDataStorage**
    * optimize memory usage for storing and reading factory data
    * make FACTORY_CURRENT update atomic

# 4.1.1
* **SimpleObjectMapper**
    * cleanup
* **DataStorage**
    * store history as compact json

# 4.1.0

* **FactoryListAttributeEditWidget**
    * select the newly added item
* **Maintenance**
    * update dependency versions

# 4.0.16

* **OracledbDataStorage**
    * add an option to compress the history, the compression/decompression is also applied to existing records
* **SimpleObjectMapper#writeValueAsString**
    * introduce PRETTY/COMPACT/DEFAULT output style
* **Save history**
    * always save history using the COMPACT output style

# 4.0.15

* **JettyServerBuilder**
    * add the option to register "first handler" with FactoryTemplateId

# 4.0.14

* **TableControlWidget**
    * fix sorting in table
* **Validators**
    * enhance Regex validator, add ValidationByException


# 4.0.11

## Breaking changes
* **ResourceBuilder** (and **JettyServerBuilder** by delegation)
  * Removed _withResource(ResourceFactory)_ and _withJaxrsComponent(JaxrsComponentFactory)_
  * Replaced with _withResource(FactoryTemplateId)_ and _withJaxrsComponent(FactoryTemplateId)_. The reason is to make sure that when the factory is created by the FactoryTreeBuilder, the FactoryContext is fully initialized (including persisted factories)
  * Added _withResourceLiveObjectClass(Class)_ and _withJaxrsComponentLiveObjectClass(Class)_ to support Resource or Jaxrs Component without a factory


### Migration

-> FactoryTemplateId

**old**
```java
    JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
        jetty.withHost("localhost").withPort(8015).withResource(ctx.get(SomeResourceFactory.class))
                    .withJaxrsComponent(ctx.get(SomeJaxrsComponentFactory.class));
    });
```

**new**
```java
    JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
        jetty.withHost("localhost").withPort(8015).withResource(new FactoryTemplateId<>(SomeResourceFactory.class))
                    .withJaxrsComponent(new FactoryTemplateId<>(SomeJaxrsComponentFactory.class));
    });
```


-> AttributelessFactory

**old**
```java
    JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
        jetty.withHost("localhost").withPort(8015).withResource(AttributelessFactory.create(SomeResource.class))
                    .withJaxrsComponent(AttributelessFactory.create(SomeJaxrsComponent.class));
    });
```

**new**
```java
    JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->{
        jetty.withHost("localhost").withPort(8015).withResourceLiveObjectClass(SomeResource.class)
                    .withJaxrsComponentLiveObjectClass(SomeJaxrsComponent.class);
    });
```

See [JettyServerBuilderWithFactoryTemplateIdTest.java](jettyFactory/src/test/java/io/github/factoryfx/jetty/JettyServerBuilderWithFactoryTemplateIdTest.java) for examples

# 4.0.10

* **ResourceBuilder**
    * Added ResourceBuilder#withResource(FactoryTemplateId)

# 4.0.9

* **UserInterfaceDistributionClient**
    * Split classes into logic and visualization
* **SimpleObjectMapper**
    * Add more delegate methods for convenience

# 4.0.8

* **AttributeVisualisationMappingBuilder**
    * Fix StringListAttribute having ChoiceAttribute visualisation

# 4.0.7

* **FactorybaseAttribute** and **FactorybaseListAttribute**
    * add convenience method 'internal_deleteFactoryDeep'

# 4.0.6

* **DataJsonNode**
    * fix NPE in certain scenarios

# 4.0.5

* **MicroserviceRestClient**
    * fix user not showing up in configuration history
 
# 4.0.4

* **TableControlWidget**
    * fix NPE when list for table is already sortedList, simplify code

* **DataJsonNode**
    * fix ClassCastException for renamed ListAttributes

# 4.0.3

* **UserAwareRequest**
    * remove Json serialization hint that breaks serializing/deserializing the object

* **FactoryBase**
    * Fix specific scenario where finalizeChildren() fails

# 4.0.2

* **DataEditor**
    * Make display of usages optional

# 4.0.1

* **UserInterfaceDistributionClientController**
    * Make finding of executable more flexible

# 4.0.0

* **Update Java version**
    * jetty12 requires jdk 17 or higher
* **Update dependencies**
    * Mainly updated to jetty12, but also other libraries
* **PostgresDataStorage**
    * implement patchAll, performance improvements

* # 3.2.3

* **PostgresDataStorage**
    * Remove redundant storage of metadata. After the update you can delete the tables futureconfigurationmetadata and configurationmetadata

* **FactoryBase**
    * fixed typo 'serFactoryTreeBuilderBasedAttributeSetupForRoot' to 'setFactoryTreeBuilderBasedAttributeSetupForRoot'


# 3.2.2

* **AttributeEditorBuilderFactoryBuilder**
    * Fix order of AttributeEditorBuilders


# 3.2.1

* **MicroserviceRestClient**
    * Transport permissions to client

* **ChoiceAttribute**
    * Add attribute that selects a String from a List of Strings

* **LocalDateTimeAttributeVisualisation**
    * Fix Localdate updating

# 3.2.0

* **ReferenceBaseAttribute**
    * Change visibility of root attribute

* **UniqueListBy**
    * Add convenience constructors 


# 3.1.6

* **Slf4LoggingFeature add Verbosity parameter**
  * Makes it possible to log for example [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807)'s application/problem+json, by choosing Verbosity.PAYLOAD_ANY

# 3.1.5

### Features

* **UniqueNestedListBy validation**
  * New validation to check uniqueness in nested lists

# 3.0.16

### Bug Fixes

* **DataStorage#patchCurrentData**
  * DataStorage#patchCurrentData now also patches the related history entry

# 3.0.15

### Features
* **Ini file SslContextFactoryFactory**
  * Added support for configuration of SslContextFactoryFactory by ini file (ClientIniFileSslContextFactoryFactory and ServerIniFileSslContextFactoryFactory)

### BREAKING CHANGES

* **HttpServerConnectorFactory**
  * HttpServerConnectorFactory#ssl attribute will not be loaded from a stored configuration.

It can be fixed by patching the factoryMetadata: change referenceClass of the ssl attribute for className "io.github.factoryfx.jetty.HttpServerConnectorFactory" to "io.github.factoryfx.factory.FactoryBase".

The following DataStorage#patchAll does it:
```java
dataStorage.patchAll((root, metaData, objectMapper) -> {
    ArrayNode dataListJsonNode = (ArrayNode) metaData.get("dataStorageMetadataDictionary").get("dataList");
    for (JsonNode childNode : dataListJsonNode) {
        String className = childNode.get("className").asText();
        if ("io.github.factoryfx.jetty.HttpServerConnectorFactory".equals(className)) {
            ArrayNode attributes = (ArrayNode) childNode.get("attributes");
            for (JsonNode attributeMetadata : attributes) {
                final String variableName = Optional.ofNullable(attributeMetadata.get("variableName")).map(JsonNode::asText).orElse(null);
                if("ssl".equals(variableName)) {
                    ((ObjectNode) attributeMetadata).set("referenceClass", new TextNode(FactoryBase.class.getName()));
                }
            }
        }
    }
});
```

# 3.0.4

### BREAKING CHANGES
* **Default Creator**
    * The DefaultCreator (used commonly for example in FactoryTreeBuilder.addSingleton(Class>) ) now no longer tries to fill FactoryListAttribute and FactoryPolymorphicListAttribute. 
    * It also has convenience methods to allow manual filling of attributes, before it tries to fill the FactoryAttributes from the context.


# 3.0.0

### Features
* **improved factory runtime**
  * improved performance for factory update in the same process

* **http2 jetty server builder:**
  * http2 configuration added

* **clean up javax.rs modules:**
  * replace module java.ws.rs with jakarta.ws.rs, in our project and update used libraries

### BREAKING CHANGES
* java 17 or higher is required

# 2.2.24
* Factories can be marked as catalog item to support semantic copy.

```java
public static class FactoryCatalogItem extends SimpleFactoryBase  {
    public FactoryCatalogItem {
        this.config().markAsCatalogItem();
    }
}
```

# 2.2.16

### BREAKING CHANGES
* **json format**
    * json format now includes null values, v=null (JsonInclude.Include.NON_NULL)
    (normally this should be compatible with the old format) 

# 2.2.10

### Features
* **javafxFactoryEditing**
  * Select copy support
  * show factory usage in order to clarify the implications of data changes
  
### BREAKING CHANGES
* **semantic copy rework**
    semantic now respect the scope from the FactoryTreeBuilder. Nested singleton factories are not copied. 
    The semantic copy configuration on the attributes is removed. This also affect Attributes marked as catalog.
    For catalog attributes you have to make sure that they are registered as singleton in the builder.
    

# 2.2.9

### BREAKING CHANGES
* **ParametrizedObjectCreatorAttribute**
    removed ParametrizedObjectCreatorAttribute because it was not thread safe.

# 2.2.8

### BREAKING CHANGES
* **PolymorphicFactoryAttribute**  
    simplified FactoryPolymorphicAttribute constructor  
    **old**
    ```java
    public final FactoryPolymorphicAttribute<Printer> reference = new FactoryPolymorphicAttribute<Printer>().setup(Printer.class,ErrorPrinterFactory.class,OutPrinterFactory.class);
    ```
    **new**
    the possible factories are now determined via the FactoryTreeBuilder and therefore need no longer be specified
    ```java
    public final FactoryPolymorphicAttribute<Printer> reference = new FactoryPolymorphicAttribute<>();
    ```
* **PolymorphicFactoryBase**  
    The class is removed and can be replaced with FactoryBase/SimpleFactoryBase 

# 2.2.1

### BREAKING CHANGES
* **EnumAttribute**  
    simplified EnumAttribute constructor 
    **old**
    ```java
    public final EnumListAttribute<TestEnum> enumAttribute= new EnumListAttribute<>(TestEnum.class);
    ```
    **new**
    ```java
    public final EnumListAttribute<TestEnum> enumAttribute= new EnumListAttribute<>();
    ```
        
# 2.2.0

### Features
* **FactoryTreeBuilder migration improvement:**  
  Data changes in the FactoryTreeBuilder are now automatically applied.
  For example if you add a new jersey resource in the builder the new resource is also added to existing configuration after start.
  This simplifies structural application changes.
  
* **migration improvements:**  
  * restore data with path to ListAttribute
  * support for attribute type change

### BREAKING CHANGES

* **redesigned  jetty server builder:**  
  improved builder integration  into the FactoryTreeBuilder

  * factory JettyServerFactory.class registration  
    **old**
    ```java
    addFactory(JettyServerFactory.class, Scope.SINGLETON, context -> new JettyServerBuilder<ServerFactory>()
                .withHostWildcard()
                .withResource(ctx.get(ResourceFactory.class))
                .build());
    ```
    **new**
    ```java          
    builder.addBuilder(ctx->new SimpleJettyServerBuilder<RootFactory>()
            .withHostWildcard()
            .withResource(ctx.get(ResourceFactory.class))      
    ```
    The server can be used in other factory build templates with  ```ctx.get(JettyServerFactory.class)```   
      
  * derived factory from JettyServerFactory.class registration  
    **old**
    ```java
    addFactory(DerivedJettyServerFactory.class, Scope.SINGLETON, context -> new JettyServerBuilder<ServerFactory>()
                .withHostWildcard()
                .withResource(ctx.get(ResourceFactory.class))
                .build());
    ```
    **new**
    ```java          
    builder.addBuilder(ctx->new JettyServerBuilder<Root,RootFactory>(new FactoryTemplateId<>(null, DerivedJettyServerFactory.class), DerivedJettyServerFactory::new)
            .withHostWildcard()
            .withResource(ctx.get(ResourceFactory.class))        
    ```  
  * root factory is JettyServerFactory  
    **new**
    ```java          
        JettyFactoryTreeBuilder builder = new JettyFactoryTreeBuilder((jetty, ctx)->jetty
                    .withHost("localhost").withPort(8005)
                    .withResource(ctx.get(ResourceFactory.class))
                );

        builder.addFactory(ResourceFactory.class, Scope.SINGLETON);  
    ```  
    This is the recommended solution if the application is primarily used as jetty/REST server.
   
* **gzip handler used as wrapper**   
  GzipHandler is removed from the HandlerCollection and is now used as a wrapper.
  This affect factory model navigation to resources.
   

# 2.1.0

### Features

* **configuration web application:** new configuration via web application
  [Quickstart](docu/src/main/java/io/github/factoryfx/docu/configurationwebapp)  

### BREAKING CHANGES

* **removed summary generic parameter:** 
  This was intended as simple index for the factory storage but never used.  
  
  example:  
  **old**
  ```java
  FactoryTreeBuilder<Server, ServerRootFactory,Void> serverBuilder = ...
  ```
  **new**
  ```java
  FactoryTreeBuilder<Server, ServerRootFactory> serverBuilder = ...
  ```
  
* **removed root generic parameter in FactoryAttribute:**  
  Root Parameter was used in ```ReferenceBaseAttribute#possibleValueProvider```. Which now requires a cast.
  This affects: FactoryAttribute/FactoryListAttribute/FactoryPolymorphicAttribute/FactoryPolymorphicListAttribute
  
  example:  
  **old** 
  ```java
  FactoryAttribute<ServerRoot,Test,TestFactory> attribute = ...
  ```
  **new**
  ```java
  FactoryAttribute<Test,TestFactory> attribute = ...
  ```

* **renamed subproject**  
  From microserviceRestServer to microserviceRest**Resource**

# 2.0.6

### BREAKING CHANGES
* **JettyServerBuilder:**
removed generic parameter JettyServerBuilder and constructor parameter. To create a derived JettyServerFactory a new buildTo method is added.

    **old**
    ```java
    new JettyServerBuilder<RootFactory,JettyServerDerivedFactory>(new JettyServerDerivedFactory()).withHost("localhost").withPort(8015).build()
    ```
    **new**
    ```java
    new JettyServerBuilder<RootFactory>().withHost("localhost").withPort(8015).buildTo(new JettyServerDerivedFactory())
    ```

# 2.0.5

### Features

* **jetty server:** the builder now supports ObjectMapper configuration.

* **jetty server:** support for thread pool configuration

### BREAKING CHANGES

* **factory:** SimpleFactoryBase method createImpl(); is no protected to avoid accidental misuse.  
 (wrong ```attribute.get().createImpl()``` instead of the correct ``` attribute.instance()``` )

  

# 2.0.0


### Bug Fixes

### Features

* **migration:** new migration system 
  *  automatic support for removing attributes
  *  FactoryTreeBuilder is used for new attributes
  *  Restoring old data from deleted attributes
  *  easy renaming of attributes and factories

* **jetty server:** rework the jetty configuration. The complete jetty structure is now represented in a factory structure. A new builder can be used for creation.
  
* **builder:** new builder API for microservice setup

* **initializr:** One time java code generator for initial project setup to simplify the first steps with FactoryFX.

* **testing:** Added the ability to set a mock liveobject for a factory.

* **data:** Removed the data editing layer (Factory editing still works the same). In the past, factories were a layer above a general data editing layer. The general data editing layer is removed to focus on the factory dependency injection.


### BREAKING CHANGES

* **factory:** removed observer, the observer was not used and overcomplicated the generics declaration. In most cases it is sufficient to remove Void from generic declaration e.g.: FactoryTreeBuilder<**~~Void~~**,Root,RootFactory,Void>

* **module:** rename module and packages from "de.factory" to "io.github.factoryfx"

* **utilityFactory:** rename FactoryBase#utilityFactory from "utilityFactory" to "utility"

* **FactoryTreeBuilder:** the root class registration is now passed as constructor, added duplicate check for factories registration:
  example:
  old
  ```java
    FactoryTreeBuilder<Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class);
    factoryTreeBuilder..addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
    ...
    });
  ```
  new
  ```java
      FactoryTreeBuilder<Void,FactoryTestA,Void> factoryTreeBuilder = new FactoryTreeBuilder<>(FactoryTestA.class, context -> {
          ....
      });
  ```
* **FactoryTreeBuilder#buildSubTree** is replaced with 
  ```java 
  factoryTreeBuilder.branch().select(BranchFactory.class).factory()
  ```

* **attribute** rename, removed reference from name
  e.g.:FactoryReferenceAttribute=>FactoryAttribute
  
  new names:
  * FactoryAttribute
  * FactoryBaseAttribute
  * FactoryListAttribute
  * FactoryPolymorphicAttribute
  * FactoryPolymorphicListAttribute
  * FactoryListBaseAttribute
  * FactoryViewAttribute
  * FactoryViewListAttribute

# 1.9.0


### Bug Fixes

### Features

* **typescript:** improved typescript generator
  * support for adding custom project specific attributes
  * support for most standard attributes
  
* **attribute:** new attributes
  * InstantAttribute
  * BigIntegerAttribute 

### BREAKING CHANGES

* **attribute:** Removed **Base64Attribute** because jackson automatically converts byte arrays to base 64 and therefore no special attribute is required. 
  Replacement is the **ByteArrayAttribute** or FileContentAttribute
* **json:** Changed EnumAttribute json format. 

  **before**
  ```
  ...
  "enumAttribute" : {
      "v" : [ "de.abc.ExampleEnum", "VALUE1" ]
  }
  ...
  ```
  **new**
  ```
  ...
  "enumAttribute" : {
      "v" : "VALUE1"
  }
  ...
  ```
  Migration
  ```java
  Pattern pattern = Pattern.compile("\"v\" : \\[ \".*\", \"(.*)\" \\]");
  String converted = pattern.matcher(old).replaceAll("\"v\" : \"$1\"");    
  ```
  
* ```collectChildFactoriesDeepFromNode``` replaced width ```collectionChildrenDeepFromNonFinalizedTree```
  
