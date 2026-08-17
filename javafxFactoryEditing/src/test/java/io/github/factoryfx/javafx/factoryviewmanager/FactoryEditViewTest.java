package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;
import io.github.factoryfx.factory.testfactories.ExampleLiveObjectA;
import io.github.factoryfx.javafx.UniformDesignBuilder;
import io.github.factoryfx.javafx.editor.DataEditor;
import io.github.factoryfx.javafx.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.server.Microservice;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class FactoryEditViewTest {

    private static class CountingWidget implements FactoryAwareWidget<ExampleFactoryA> {
        int createContentCalls;
        int editCalls;
        ExampleFactoryA lastEditedRoot;

        @Override
        public Node createContent() {
            createContentCalls++;
            return new Region();
        }

        @Override
        public void edit(ExampleFactoryA rootFactory) {
            editCalls++;
            lastEditedRoot = rootFactory;
        }
    }

    @SuppressWarnings("unchecked")
    private FactoryEditManager<ExampleFactoryA> createFactoryEditManager() {
        FactoryTreeBuilder<ExampleLiveObjectA, ExampleFactoryA> builder = new FactoryTreeBuilder<>(ExampleFactoryA.class, ctx -> new ExampleFactoryA());
        Microservice<ExampleLiveObjectA, ExampleFactoryA> microservice = builder.microservice().build();
        microservice.start();

        MicroserviceRestClient<ExampleFactoryA> client = Mockito.mock(MicroserviceRestClient.class);
        Mockito.when(client.prepareNewFactory()).then(invocation -> microservice.prepareNewFactory());
        Mockito.when(client.updateCurrentFactory(Mockito.any(DataUpdate.class), Mockito.anyString())).then(invocation -> microservice.updateCurrentFactory(invocation.getArgument(0)));

        FactoryEditManager<ExampleFactoryA> factoryEditManager = new FactoryEditManager<>(client, new MigrationManager<>(ExampleFactoryA.class, ObjectMapperBuilder.build(), (root, d) -> { }));
        factoryEditManager.runLaterExecuter = Runnable::run;
        return factoryEditManager;
    }

    @SuppressWarnings("unchecked")
    private FactoryEditView<ExampleFactoryA> createFactoryEditView(FactoryEditManager<ExampleFactoryA> factoryEditManager, CountingWidget widget, LongRunningActionExecutor longRunningActionExecutor) {
        return new FactoryEditView<>(
            longRunningActionExecutor,
            factoryEditManager,
            widget,
            UniformDesignBuilder.build(),
            Mockito.mock(DataEditor.class),
            Mockito.mock(DiffDialogBuilder.class)) {
            @Override
            protected ToolBar createToolBar() {
                return null; //JavaFX controls require the toolkit, which is unavailable in headless tests
            }
        };
    }

    @Test
    public void test_edit_called_once_per_root_instance() {
        FactoryEditManager<ExampleFactoryA> factoryEditManager = createFactoryEditManager();
        factoryEditManager.load();

        CountingWidget widget = new CountingWidget();
        FactoryEditView<ExampleFactoryA> view = createFactoryEditView(factoryEditManager, widget, Mockito.mock(LongRunningActionExecutor.class));

        view.createContent();
        view.createContent(); //re-open with unchanged factory
        view.createContent(); //re-open with unchanged factory

        Assertions.assertEquals(3, widget.createContentCalls, "createContent is the per-open signal and must fire on every open");
        Assertions.assertEquals(1, widget.editCalls, "edit must not be repeated for the same root instance");
        Assertions.assertSame(factoryEditManager.getLoadedFactory().get(), widget.lastEditedRoot);

        factoryEditManager.load(); //save/update: a new root instance is delivered via the listener
        Assertions.assertEquals(2, widget.editCalls, "a new root instance must be delivered");
        Assertions.assertSame(factoryEditManager.getLoadedFactory().get(), widget.lastEditedRoot);
    }

    @Test
    public void test_listener_registered_once() {
        FactoryEditManager<ExampleFactoryA> factoryEditManager = createFactoryEditManager();
        factoryEditManager.load();

        CountingWidget widget = new CountingWidget();
        FactoryEditView<ExampleFactoryA> view = createFactoryEditView(factoryEditManager, widget, Mockito.mock(LongRunningActionExecutor.class));

        view.createContent();
        view.createContent();
        view.createContent();

        //if createContent registered the listener repeatedly, one registration would survive this remove
        factoryEditManager.removeListener(view);
        factoryEditManager.load();

        Assertions.assertEquals(1, widget.editCalls, "duplicate listener registrations would still deliver updates after removeListener");
    }

    @Test
    public void test_load_triggered_and_edit_delivered_when_factory_not_yet_loaded() {
        FactoryEditManager<ExampleFactoryA> factoryEditManager = createFactoryEditManager();

        CountingWidget widget = new CountingWidget();
        LongRunningActionExecutor longRunningActionExecutor = Mockito.mock(LongRunningActionExecutor.class);
        FactoryEditView<ExampleFactoryA> view = createFactoryEditView(factoryEditManager, widget, longRunningActionExecutor);

        view.createContent();

        Assertions.assertEquals(1, widget.createContentCalls, "content must be embedded before the factory is available");
        Assertions.assertEquals(0, widget.editCalls);

        ArgumentCaptor<Runnable> loadTask = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(longRunningActionExecutor).execute(loadTask.capture());
        loadTask.getValue().run(); //background factory download finishes

        Assertions.assertEquals(1, widget.editCalls, "edit must be delivered once the factory arrives");
        Assertions.assertSame(factoryEditManager.getLoadedFactory().get(), widget.lastEditedRoot);
    }
}
