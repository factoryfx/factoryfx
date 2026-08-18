package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * the rebuild merge on start uses the current configuration as reference: the FactoryTreeBuilder describes the
 * technical configuration of the tree and only contributes newly introduced factories. existing factories keep their
 * values and wiring, user added factories are preserved, builder default value changes are not propagated.
 */
public class PersistentBuilderRebuildMergeTest {

    public static class ServerRootFactory extends SimpleFactoryBase<Void, ServerRootFactory> {
        public final StringAttribute value = new StringAttribute().nullable();
        public final FactoryAttribute<Void, ServiceFactory> newService = new FactoryAttribute<Void, ServiceFactory>().nullable();
        public final FactoryListAttribute<Void, ServiceFactory> services = new FactoryListAttribute<>();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    public static class ServiceFactory extends SimpleFactoryBase<Void, ServerRootFactory> {
        public final StringAttribute name = new StringAttribute().nullable();

        @Override
        protected Void createImpl() {
            return null;
        }
    }

    @TempDir
    public Path folder;

    private FactoryTreeBuilder<Void, ServerRootFactory> createBuilderV1() {
        FactoryTreeBuilder<Void, ServerRootFactory> builder = new FactoryTreeBuilder<>(ServerRootFactory.class, ctx -> {
            ServerRootFactory root = new ServerRootFactory();
            root.value.set("builderDefault");
            root.services.add(ctx.get(ServiceFactory.class, "A"));
            return root;
        });
        builder.addSingleton(ServiceFactory.class, "A", ctx -> {
            ServiceFactory service = new ServiceFactory();
            service.name.set("A");
            return service;
        });
        return builder;
    }

    private FactoryTreeBuilder<Void, ServerRootFactory> createBuilderV2() {
        FactoryTreeBuilder<Void, ServerRootFactory> builder = new FactoryTreeBuilder<>(ServerRootFactory.class, ctx -> {
            ServerRootFactory root = new ServerRootFactory();
            root.value.set("changedBuilderDefault");
            root.newService.set(ctx.get(ServiceFactory.class, "C"));
            root.services.add(ctx.get(ServiceFactory.class, "A"));
            root.services.add(ctx.get(ServiceFactory.class, "B"));
            return root;
        });
        builder.addSingleton(ServiceFactory.class, "A", ctx -> {
            ServiceFactory service = new ServiceFactory();
            service.name.set("A-changedDefault");
            return service;
        });
        builder.addSingleton(ServiceFactory.class, "B", ctx -> {
            ServiceFactory service = new ServiceFactory();
            service.name.set("B");
            return service;
        });
        builder.addSingleton(ServiceFactory.class, "C", ctx -> {
            ServiceFactory service = new ServiceFactory();
            service.name.set("C");
            return service;
        });
        return builder;
    }

    private void createStoredConfigurationWithUserChanges() {
        Microservice<Void, ServerRootFactory> microservice = createBuilderV1().microservice().withFilesystemStorage(folder).build();
        microservice.start();

        //user edits: change a value and add a factory that is not part of the builder
        DataUpdate<ServerRootFactory> update = microservice.prepareNewFactory();
        update.root.value.set("userValue");
        ServiceFactory userService = new ServiceFactory();
        userService.name.set("userAdded");
        update.root.services.add(userService);
        microservice.updateCurrentFactory(update);
        microservice.stop();
    }

    @Test
    public void test_builderContributesNewFactories_currentConfigurationIsTheReference() {
        createStoredConfigurationWithUserChanges();

        Microservice<Void, ServerRootFactory> microservice = createBuilderV2().microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerRootFactory root = microservice.prepareNewFactory().root;

        //user value wins, changed builder default is not propagated
        Assertions.assertEquals("userValue", root.value.get());
        //new singleton wired into the empty reference is adopted
        Assertions.assertNotNull(root.newService.get());
        Assertions.assertEquals("C", root.newService.get().name.get());
        //list: existing entry keeps current value, user added entry preserved, new builder entry appended
        List<String> serviceNames = root.services.stream().map(service -> service.name.get()).collect(Collectors.toList());
        Assertions.assertEquals(List.of("A", "userAdded", "B"), serviceNames);
        microservice.stop();
    }

    @Test
    public void test_rebuildMerge_isIdempotentAcrossRestarts() {
        createStoredConfigurationWithUserChanges();

        {
            Microservice<Void, ServerRootFactory> microservice = createBuilderV2().microservice().withFilesystemStorage(folder).build();
            microservice.start();
            microservice.stop();
        }

        Microservice<Void, ServerRootFactory> microservice = createBuilderV2().microservice().withFilesystemStorage(folder).build();
        int historySizeBefore = microservice.getHistoryFactoryList(true).size();
        microservice.start();
        Assertions.assertEquals(historySizeBefore, microservice.getHistoryFactoryList(true).size());

        ServerRootFactory root = microservice.prepareNewFactory().root;
        List<String> serviceNames = root.services.stream().map(service -> service.name.get()).collect(Collectors.toList());
        Assertions.assertEquals(List.of("A", "userAdded", "B"), serviceNames);
        microservice.stop();
    }

    @Test
    public void test_factoriesRemovedFromBuilder_arePreservedInCurrentConfiguration() {
        createStoredConfigurationWithUserChanges();

        //builder without the "A" service: nothing is removed from the current configuration
        FactoryTreeBuilder<Void, ServerRootFactory> builder = new FactoryTreeBuilder<>(ServerRootFactory.class, ctx -> {
            ServerRootFactory root = new ServerRootFactory();
            root.value.set("builderDefault");
            return root;
        });
        Microservice<Void, ServerRootFactory> microservice = builder.microservice().withFilesystemStorage(folder).build();
        microservice.start();
        ServerRootFactory root = microservice.prepareNewFactory().root;
        List<String> serviceNames = root.services.stream().map(service -> service.name.get()).collect(Collectors.toList());
        Assertions.assertEquals(List.of("A", "userAdded"), serviceNames);
        microservice.stop();
    }
}
