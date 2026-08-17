package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryTreeBuilderNestedBuilderTest {

    /** mirrors application builders (e.g. jetty-based servers) whose root creator is registered by a nested builder */
    private static class NestedRootBuilder extends FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> {
        NestedRootBuilder() {
            this(new FactoryTemplateId<>(ExampleFactoryA.class, "root"));
        }

        private NestedRootBuilder(FactoryTemplateId<ExampleFactoryA> rootTemplateId) {
            super(rootTemplateId, (FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder) -> {
                builder.addBuilder(ctx -> (NestedBuilder<ExampleFactoryA>) nested ->
                    nested.addFactory(rootTemplateId, Scope.SINGLETON, creatorCtx -> {
                        ExampleFactoryA root = new ExampleFactoryA();
                        root.referenceAttribute.set(creatorCtx.get(ExampleFactoryB.class));
                        return root;
                    }));
            });
            addFactory(ExampleFactoryB.class, Scope.SINGLETON, ctx -> {
                ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
                exampleFactoryB.stringAttribute.set("111");
                return exampleFactoryB;
            });
        }
    }

    @Test
    public void test_buildNewSubTree_root_from_nested_builder_without_buildTree() {
        NestedRootBuilder builder = new NestedRootBuilder();

        //no buildTree() call before: nested builders must be applied lazily
        ExampleFactoryA root = builder.buildNewSubTree(ExampleFactoryA.class);

        Assertions.assertNotNull(root);
        Assertions.assertEquals("111", root.referenceAttribute.get().stringAttribute.get());
    }

    /** reproduces a server start reading stored data that misses a newly added attribute
     * (MigrationManager.read -&gt; fillNewAttributes -&gt; buildNewSubTree) before the tree was ever built */
    @Test
    public void test_fillNewAttributes_root_from_nested_builder_without_buildTree() {
        NestedRootBuilder builder = new NestedRootBuilder();
        FactoryTreeBuilderAttributeFiller<ExampleLiveObjectA, ExampleFactoryA> filler = new FactoryTreeBuilderAttributeFiller<>(builder);

        ExampleFactoryA metadataSource = new ExampleFactoryA();
        metadataSource.internal().finalise();
        DataStorageMetadataDictionary oldDictionary = metadataSource.internal().createDataStorageMetadataDictionaryFromRoot();
        oldDictionary.getDataStorageMetadata(ExampleFactoryA.class.getName()).removeAttribute("referenceAttribute");

        ExampleFactoryA storedRoot = new ExampleFactoryA();
        storedRoot.internal().finalise();
        filler.fillNewAttributes(storedRoot, oldDictionary);

        Assertions.assertEquals("111", storedRoot.referenceAttribute.get().stringAttribute.get());
    }
}
