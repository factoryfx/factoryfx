package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleFactoryB;
import io.github.factoryfx.factory.testfactories.ExampleFactoryC;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link FactoryTreeBuilder#rebuildTreeForExistingConfiguration(FactoryBase)} against configurations
 * predating the tree builder identity: an existing same-class factory without identity occupies its
 * template, the rebuild must not introduce a duplicate next to it (a duplicated jetty connector on the
 * same port made the server block its own start with "Address already in use").
 */
public class RebuildTreeForExistingConfigurationTest {

    private FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder(boolean wireSingleReference) {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.add(context.get(ExampleFactoryB.class));
            if (wireSingleReference) {
                factory.referenceAttribute.set(context.get(ExampleFactoryB.class));
            }
            return factory;
        });
        builder.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> new ExampleFactoryB());
        return builder;
    }

    private void stripIdentity(FactoryBase<?, ?> factory) {
        factory.internal().setTreeBuilderName(null);
        factory.internal().setTreeBuilderClassUsed(false);
    }

    /** replicates the persistent-builder block of Microservice#start */
    private MergeDiffInfo<ExampleFactoryA> startupMerge(ExampleFactoryA current, ExampleFactoryA rebuild) {
        DataMerger<ExampleFactoryA> merger = new DataMerger<>(current, current.utility().copy(), rebuild);
        return merger.createMergeResult((permission) -> true, true).executeMerge();
    }

    @Test
    public void legacyListEntry_notDuplicated_identityStamped() {
        ExampleFactoryA current = builder(false).buildTree();
        ExampleFactoryB legacy = current.referenceListAttribute.get(0);
        stripIdentity(legacy);

        ExampleFactoryA rebuild = builder(false).rebuildTreeForExistingConfiguration(current);

        Assertions.assertEquals(1, rebuild.referenceListAttribute.size(), "rebuild duplicated the legacy list entry");
        Assertions.assertEquals(legacy.getId(), rebuild.referenceListAttribute.get(0).getId());
        Assertions.assertTrue(rebuild.referenceListAttribute.get(0).internal().isTreeBuilderClassUsed(),
                              "template identity not stamped onto the occupying legacy factory");
        Assertions.assertTrue(legacy.internal().isTreeBuilderClassUsed(),
                              "identity must also be stamped onto the passed current root so the next persisted configuration carries it");
        Assertions.assertTrue(startupMerge(current, rebuild).mergeInfos.isEmpty(),
                              "legacy adoption must not produce a spurious FactoryTreeBuilder update");
    }

    @Test
    public void legacyListEntry_secondRebuildAfterStamp_noChanges() {
        ExampleFactoryA current = builder(false).buildTree();
        ExampleFactoryB legacy = current.referenceListAttribute.get(0);
        stripIdentity(legacy);
        builder(false).rebuildTreeForExistingConfiguration(current); //stamps the identity onto current

        ExampleFactoryA rebuild = builder(false).rebuildTreeForExistingConfiguration(current);

        Assertions.assertEquals(1, rebuild.referenceListAttribute.size());
        Assertions.assertEquals(legacy.getId(), rebuild.referenceListAttribute.get(0).getId());
        Assertions.assertTrue(startupMerge(current, rebuild).mergeInfos.isEmpty());
    }

    @Test
    public void ambiguousLegacyCandidates_nothingContributed_nothingStamped() {
        ExampleFactoryA current = builder(false).buildTree();
        stripIdentity(current.referenceListAttribute.get(0));
        current.referenceListAttribute.add(new ExampleFactoryB()); //second identity-less same-class factory: occupant ambiguous

        ExampleFactoryA rebuild = builder(false).rebuildTreeForExistingConfiguration(current);

        Assertions.assertEquals(2, rebuild.referenceListAttribute.size(), "ambiguous legacy candidates: the rebuild must contribute nothing");
        for (ExampleFactoryB factory : rebuild.referenceListAttribute) {
            Assertions.assertFalse(factory.internal().isCreatedWithBuilderTemplate(),
                                   "ambiguous legacy candidates: no identity may be stamped");
        }
    }

    @Test
    public void emptySingleReference_notFilledWhenLegacyFactoryOccupiesTemplate() {
        ExampleFactoryA current = builder(false).buildTree(); //v1: single reference not wired
        ExampleFactoryB legacy = current.referenceListAttribute.get(0);
        stripIdentity(legacy);

        ExampleFactoryA rebuild = builder(true).rebuildTreeForExistingConfiguration(current); //v2 wires the single reference

        Assertions.assertNull(rebuild.referenceAttribute.get(),
                              "the legacy factory occupies the template: the rebuild contributes nothing for it");
        Assertions.assertEquals(1, rebuild.referenceListAttribute.size());
        Assertions.assertTrue(legacy.internal().isTreeBuilderClassUsed());
    }

    @Test
    public void userAddedEntry_doesNotBlockNewTemplates_whenClassHasIdentityInstances() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> v2 = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.add(context.get(ExampleFactoryB.class, "first"));
            factory.referenceListAttribute.add(context.get(ExampleFactoryB.class, "second"));
            return factory;
        });
        v2.addSingleton(ExampleFactoryB.class, "first", context -> new ExampleFactoryB());
        v2.addSingleton(ExampleFactoryB.class, "second", context -> new ExampleFactoryB());

        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> v1 = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.add(context.get(ExampleFactoryB.class, "first"));
            return factory;
        });
        v1.addSingleton(ExampleFactoryB.class, "first", context -> new ExampleFactoryB());

        ExampleFactoryA current = v1.buildTree();
        ExampleFactoryB userAdded = new ExampleFactoryB(); //user-added same-class factory: no identity, but NOT a legacy template occupant
        current.referenceListAttribute.add(userAdded);

        ExampleFactoryA rebuild = v2.rebuildTreeForExistingConfiguration(current);

        Assertions.assertEquals(3, rebuild.referenceListAttribute.size(),
                                "the class has identity-carrying instances: the user-added entry must not block the new template");
        Assertions.assertFalse(userAdded.internal().isCreatedWithBuilderTemplate(), "no identity may be stamped onto user-added data");
    }

    @Test
    public void newTemplate_stillContributedWhenIdentitiesPresent() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> v2 = new FactoryTreeBuilder<>(ExampleFactoryA.class, context -> {
            ExampleFactoryA factory = new ExampleFactoryA();
            factory.referenceListAttribute.add(context.get(ExampleFactoryB.class));
            return factory;
        });
        v2.addFactory(ExampleFactoryB.class, Scope.SINGLETON, context -> {
            ExampleFactoryB factory = new ExampleFactoryB();
            factory.referenceAttributeC.set(context.get(ExampleFactoryC.class));
            return factory;
        });
        v2.addFactory(ExampleFactoryC.class, Scope.SINGLETON, context -> new ExampleFactoryC());

        ExampleFactoryA current = builder(false).buildTree(); //identities intact: guard must not block genuinely new templates
        ExampleFactoryA rebuild = v2.rebuildTreeForExistingConfiguration(current);

        Assertions.assertEquals(1, rebuild.referenceListAttribute.size());
        Assertions.assertNotNull(rebuild.referenceListAttribute.get(0).referenceAttributeC.get(),
                                 "newly introduced template must still be contributed");
        Assertions.assertTrue(rebuild.referenceListAttribute.get(0).referenceAttributeC.get().internal().isTreeBuilderClassUsed());
    }
}
