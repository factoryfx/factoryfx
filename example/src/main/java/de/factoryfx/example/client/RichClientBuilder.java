package de.factoryfx.example.client;

import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.data.storage.JacksonSerialisation;
import de.factoryfx.example.client.view.ConfigurationViewFactory;
import de.factoryfx.example.factory.OrderCollector;
import de.factoryfx.example.factory.ShopFactory;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.javafx.editor.DataEditorFactory;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.editor.attribute.EditorBuildersListFactory;
import de.factoryfx.javafx.stage.BorderPaneStage;
import de.factoryfx.javafx.stage.DefaultStageFactory;
import de.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.view.ViewDescriptionFactory;
import de.factoryfx.javafx.view.ViewFactory;
import de.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.view.factoryviewmanager.FactoryEditManagerFactory;
import de.factoryfx.javafx.view.factoryviewmanager.FactoryEditViewFactory;
import de.factoryfx.javafx.view.menu.ViewMenuFactory;
import de.factoryfx.javafx.view.menu.ViewMenuItemFactory;
import de.factoryfx.javafx.widget.diffdialog.DiffDialogBuilderFactory;
import de.factoryfx.server.rest.client.ApplicationServerRestClientFactory;
import de.factoryfx.server.rest.client.RestClientFactory;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;


public class RichClientBuilder {

    private final int adminServerPort;

    public RichClientBuilder(int adminServerPort) {
        this.adminServerPort = adminServerPort;
    }


    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder<Void, BorderPaneStage, DefaultStageFactory> createFactoryBuilder(Stage primaryStage, String user, String passwordHash, Locale locale) {
        FactoryTreeBuilder<Void, BorderPaneStage, DefaultStageFactory> factoryBuilder = new FactoryTreeBuilder<>(DefaultStageFactory.class);

        factoryBuilder.addFactory(LongRunningActionExecutorFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(UniformDesignFactory.class, Scope.SINGLETON, voidSimpleFactoryContext -> {
            UniformDesignFactory<Void> uniformDesignFactory = new UniformDesignFactory<>();
            uniformDesignFactory.locale.set(locale);
            uniformDesignFactory.askBeforeDelete.set(false);
            return uniformDesignFactory;
        });
        factoryBuilder.addFactory(FactoryEditManagerFactory.class, Scope.SINGLETON, context -> {
            FactoryEditManagerFactory factoryEditManager = new FactoryEditManagerFactory();
            factoryEditManager.restClient.set(context.get(ApplicationServerRestClientFactory.class));
            DataSerialisationManager<ShopFactory,Void> serialisationManager = new DataSerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(ShopFactory.class,1),new ArrayList<>(),1);

            factoryEditManager.factorySerialisationManager.set(serialisationManager);
            return factoryEditManager;
        });

        factoryBuilder.addFactory(AttributeEditorBuilderFactory.class, Scope.SINGLETON, context -> {
            final AttributeEditorBuilderFactory<Void> attributeEditorBuilderFactory = new AttributeEditorBuilderFactory<>();
            EditorBuildersListFactory<Void> editorBuildersListFactory = new EditorBuildersListFactory<>();
            editorBuildersListFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            attributeEditorBuilderFactory.editorBuildersList.set(editorBuildersListFactory);
            return attributeEditorBuilderFactory;
        });

        factoryBuilder.addFactory(DataEditorFactory.class, Scope.SINGLETON, context -> {
            DataEditorFactory dataEditorFactory = new DataEditorFactory();
            dataEditorFactory.attributeEditorBuilder.set(context.get(AttributeEditorBuilderFactory.class));
            dataEditorFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            return dataEditorFactory;
        });

        factoryBuilder.addFactory(ViewMenuFactory.class, "file", Scope.SINGLETON, context -> {
            ViewMenuFactory<Void> fileMenu = new ViewMenuFactory<>();
            fileMenu.uniformDesign.set(context.get(UniformDesignFactory.class));
            fileMenu.text.en("File").de("Data");
            fileMenu.items.add(context.get(ViewMenuItemFactory.class, "configuration"));
//            fileMenu.items.add(context.get(ViewMenuItemFactory.class, "history"));
            return fileMenu;
        });

        factoryBuilder.addFactory(ViewsDisplayWidgetFactory.class, Scope.SINGLETON);

        factoryBuilder.addFactory(DefaultStageFactory.class, Scope.SINGLETON, context -> {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            DefaultStageFactory stageFactory = new DefaultStageFactory();
            stageFactory.stage.set(primaryStage);
            stageFactory.items.add(context.get(ViewMenuFactory.class, "file"));
            stageFactory.width.set((int) (primaryScreenBounds.getWidth() * 0.9));
            stageFactory.height.set((int) (primaryScreenBounds.getHeight() * 0.8));
            stageFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));
            stageFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
            return stageFactory;
        });

        factoryBuilder.addFactory(ApplicationServerRestClientFactory.class, Scope.SINGLETON, context -> {
            ApplicationServerRestClientFactory<Void, DefaultStageFactory, OrderCollector, ShopFactory> restClient = new ApplicationServerRestClientFactory<>();
            restClient.factoryRootClass.set(ShopFactory.class);
            restClient.restClient.set(new RestClientFactory<>());
            restClient.restClient.get().host.set("localhost");
            restClient.restClient.get().port.set(adminServerPort);
            restClient.restClient.get().path.set("applicationServer");
            restClient.user.set(user);
            restClient.passwordHash.set(passwordHash);
            return restClient;
        });

        factoryBuilder.addFactory(DiffDialogBuilderFactory.class, Scope.PROTOTYPE, context -> {
            DiffDialogBuilderFactory<Void> diffDialogBuilderFactory = new DiffDialogBuilderFactory<>();
            diffDialogBuilderFactory.attributeEditorBuilder.set(context.get(AttributeEditorBuilderFactory.class));
            diffDialogBuilderFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            return diffDialogBuilderFactory;
        });

        factoryBuilder.addFactory(ViewMenuItemFactory.class, "configuration", Scope.SINGLETON, context -> {
            ViewDescriptionFactory<Void> viewDescriptionFactory = new ViewDescriptionFactory<>();
            viewDescriptionFactory.text.en("Configuration").de("Konfiguration");
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));

            ViewFactory<Void> viewFactory = new ViewFactory<>();
            viewFactory.viewDescription.set(viewDescriptionFactory);
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            ConfigurationViewFactory configurationViewFactory = new ConfigurationViewFactory();
            configurationViewFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            configurationViewFactory.dataEditorFactory.set(context.get(DataEditorFactory.class));

            FactoryEditViewFactory<Void, DefaultStageFactory, OrderCollector, ShopFactory, String> factoryEditViewFactory = new FactoryEditViewFactory<>();
            factoryEditViewFactory.factoryEditManager.set(context.get(FactoryEditManagerFactory.class));
            factoryEditViewFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
            factoryEditViewFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            factoryEditViewFactory.dataEditorFactory.set((DataEditorFactory) context.get(DataEditorFactory.class));
            factoryEditViewFactory.contentWidgetFactory.set(configurationViewFactory);
            factoryEditViewFactory.diffDialogBuilder.set(context.get(DiffDialogBuilderFactory.class));
            viewFactory.widget.set(factoryEditViewFactory);

            ViewMenuItemFactory<Void> viewMenuItemFactory = new ViewMenuItemFactory<>();
            viewMenuItemFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
            viewMenuItemFactory.view.set(viewFactory);
            return viewMenuItemFactory;
        });

//        factoryBuilder.addFactory(ViewMenuItemFactory.class, "history", Scope.SINGLETON, context -> {
//            ViewDescriptionFactory<Void> viewDescriptionFactory = new ViewDescriptionFactory<>();
//            viewDescriptionFactory.text.en("History").de("Historie");
//            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
//
//            ViewFactory<Void> viewFactory = new ViewFactory<>();
//            viewFactory.viewDescription.set(viewDescriptionFactory);
//            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));
//
//            HistoryViewFactory historyViewFactory = new HistoryViewFactory();
//            historyViewFactory.factoryEditManager.set(context.get(FactoryEditManagerFactory.class));
//            historyViewFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
//            historyViewFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
//            historyViewFactory.restClient.set(context.get(ApplicationServerRestClientFactory.class));
//
//            viewFactory.widget.set(historyViewFactory);
//
//            ViewMenuItemFactory<Void> viewMenuItemFactory = new ViewMenuItemFactory<>();
//            viewMenuItemFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
//            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
//            viewMenuItemFactory.view.set(viewFactory);
//            return viewMenuItemFactory;
//        });
        return factoryBuilder;

    }

}
