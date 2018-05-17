package de.factoryfx.example.client;

import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.JacksonDeSerialisation;
import de.factoryfx.data.storage.JacksonSerialisation;
import de.factoryfx.example.client.view.ConfigurationViewFactory;
import de.factoryfx.example.client.view.DashboardViewFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.editor.DataEditorFactory;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.factory.editor.attribute.DefaultEditorBuildersListFactory;
import de.factoryfx.javafx.factory.stage.StageFactory;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.factory.view.ViewDescriptionFactory;
import de.factoryfx.javafx.factory.view.ViewFactory;
import de.factoryfx.javafx.factory.view.container.ViewsDisplayWidgetFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryEditManagerFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryEditViewFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactorySerialisationManagerFactory;
import de.factoryfx.javafx.factory.view.menu.ViewMenuFactory;
import de.factoryfx.javafx.factory.view.menu.ViewMenuItemFactory;
import de.factoryfx.javafx.factory.widget.factory.datatree.DataTreeWidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilderFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.ArrayList;
import java.util.Locale;


public class RichClientBuilder {

    private final int adminServerPort;

    public RichClientBuilder(int adminServerPort) {
        this.adminServerPort = adminServerPort;
    }


    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder<RichClientRoot> createFactoryBuilder(Stage primaryStage, String user, String passwordHash, Locale locale) {
        FactoryTreeBuilder<RichClientRoot> factoryBuilder = new FactoryTreeBuilder<>(RichClientRoot.class);

        factoryBuilder.addFactory(LongRunningActionExecutorFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(RichClientRoot.class, Scope.SINGLETON);
        factoryBuilder.addFactory(FactoryEditManagerFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(AttributeEditorBuilderFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DefaultEditorBuildersListFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DataEditorFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(ViewsDisplayWidgetFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DiffDialogBuilderFactory.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(DashboardViewFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DataTreeWidgetFactory.class, Scope.PROTOTYPE);

        factoryBuilder.addFactory(FactorySerialisationManagerFactory.class, Scope.SINGLETON, (context)->{
            return new RichClientFactorySerialisationManagerFactory();
        });

        factoryBuilder.addFactory(UniformDesignFactory.class, Scope.SINGLETON, voidSimpleFactoryContext -> {
            UniformDesignFactory uniformDesignFactory = new UniformDesignFactory();
            uniformDesignFactory.locale.set(locale);
            uniformDesignFactory.askBeforeDelete.set(false);
            return uniformDesignFactory;
        });

        factoryBuilder.addFactory(ViewMenuFactory.class, "file", Scope.SINGLETON, context -> {
            ViewMenuFactory fileMenu = new ViewMenuFactory();
            fileMenu.uniformDesign.set(context.get(UniformDesignFactory.class));
            fileMenu.text.en("File").de("Data");
            fileMenu.items.add(context.get(ViewMenuItemFactory.class, "configuration"));
            fileMenu.items.add(context.get(ViewMenuItemFactory.class, "dashboard"));
            return fileMenu;
        });

        factoryBuilder.addFactory(StageFactory.class, Scope.SINGLETON, context -> {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            StageFactory stageFactory = new StageFactory();
            stageFactory.stage.set(primaryStage);
            stageFactory.items.add(context.get(ViewMenuFactory.class, "file"));
            stageFactory.width.set((int) (primaryScreenBounds.getWidth() * 0.9));
            stageFactory.height.set((int) (primaryScreenBounds.getHeight() * 0.8));
            stageFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));
            stageFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
            return stageFactory;
        });

        factoryBuilder.addFactory(MicroserviceRestClientFactory.class, Scope.SINGLETON, context -> {
            MicroserviceRestClientFactory<Void,RichClientRoot, OrderCollector, ServerRootFactory,Void> restClient = new MicroserviceRestClientFactory<>();
            restClient.factoryRootClass.set(ServerRootFactory.class);
            restClient.host.set("localhost");
            restClient.port.set(adminServerPort);
            restClient.path.set(null);
            restClient.user.set(user);
            restClient.passwordHash.set(passwordHash);
            return restClient;
        });

        factoryBuilder.addFactory(ViewMenuItemFactory.class, "configuration", Scope.SINGLETON, context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en("Configuration").de("Konfiguration");
            viewDescriptionFactory.icon.setEnum(FontAwesome.Glyph.COG);
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));

            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(viewDescriptionFactory);
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            ConfigurationViewFactory configurationViewFactory = new ConfigurationViewFactory();
            configurationViewFactory.dataTreeWidget.set(context.get(DataTreeWidgetFactory.class));

            FactoryEditViewFactory<OrderCollector, ServerRootFactory, Void> factoryEditViewFactory = new FactoryEditViewFactory<>();
            factoryEditViewFactory.factoryEditManager.set(context.get(FactoryEditManagerFactory.class));
            factoryEditViewFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
            factoryEditViewFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            DataEditorFactory dataEditorFactory = context.get(DataEditorFactory.class);
            factoryEditViewFactory.dataEditorFactory.set(dataEditorFactory);
            factoryEditViewFactory.contentWidgetFactory.set(configurationViewFactory);
            factoryEditViewFactory.diffDialogBuilder.set(context.get(DiffDialogBuilderFactory.class));
            viewFactory.widget.set(factoryEditViewFactory);

            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
            viewMenuItemFactory.view.set(viewFactory);
            return viewMenuItemFactory;
        });

        factoryBuilder.addFactory(ViewMenuItemFactory.class, "dashboard", Scope.SINGLETON, context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en("Dashboard").de("Dashboard");
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));

            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(viewDescriptionFactory);
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            viewFactory.widget.set(context.get(DashboardViewFactory.class));

            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
            viewMenuItemFactory.view.set(viewFactory);
            return viewMenuItemFactory;
        });

        return factoryBuilder;

    }

    private static class RichClientFactorySerialisationManagerFactory extends FactorySerialisationManagerFactory<ServerRootFactory,Void,Void,RichClientRoot> {
        @Override
        public DataSerialisationManager<ServerRootFactory, Void> createImpl() {
            return new DataSerialisationManager<>(new JacksonSerialisation<>(1),new JacksonDeSerialisation<>(ServerRootFactory.class,1),new ArrayList<>(),1);
        }
    }
}
