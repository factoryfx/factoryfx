package io.github.factoryfx.example.client;

import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.example.client.view.ConfigurationViewFactory;
import io.github.factoryfx.example.client.view.DashboardViewFactory;
import io.github.factoryfx.example.client.view.HistoryViewFactory;
import io.github.factoryfx.example.client.view.ProductsViewFactory;
import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.DataEditorFactory;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactoryBuilder;
import io.github.factoryfx.javafx.stage.StageFactory;
import io.github.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import io.github.factoryfx.javafx.view.ViewDescriptionFactory;
import io.github.factoryfx.javafx.view.ViewFactory;
import io.github.factoryfx.javafx.view.container.ViewsDisplayWidgetFactory;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryEditManagerFactory;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryEditViewFactory;
import io.github.factoryfx.javafx.factoryviewmanager.FactorySerialisationManagerFactory;
import io.github.factoryfx.javafx.view.menu.SeparatorMenuItemFactory;
import io.github.factoryfx.javafx.view.menu.ViewMenuFactory;
import io.github.factoryfx.javafx.view.menu.ViewMenuItemFactory;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilderFactory;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.eclipse.jetty.server.Server;

import java.util.Locale;


public class RichClientBuilder {

    @SuppressWarnings("unchecked")
    public static FactoryTreeBuilder<Stage,RichClientRoot> createFactoryBuilder(int adminServerPort, Stage primaryStage, String user, String passwordHash, Locale locale, FactoryTreeBuilder<Server, ServerRootFactory> serverRootFactoryFactoryTreeBuilder, MigrationManager<ServerRootFactory> serverMigrationManager) {
        FactoryTreeBuilder<Stage,RichClientRoot> factoryBuilder = new FactoryTreeBuilder<>(RichClientRoot.class);

        factoryBuilder.addFactory(LongRunningActionExecutorFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(FactoryEditManagerFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(AttributeEditorBuilderFactory.class, Scope.SINGLETON, ctx -> new AttributeEditorBuilderFactoryBuilder().build(ctx.get(UniformDesignFactory.class)));
        factoryBuilder.addFactory(DataEditorFactory.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(ViewsDisplayWidgetFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DiffDialogBuilderFactory.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(DashboardViewFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DataTreeWidgetFactory.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(HistoryViewFactory.class, Scope.PROTOTYPE);

        factoryBuilder.addFactory(FactorySerialisationManagerFactory.class, Scope.SINGLETON, (context)->{
            return new RichClientFactorySerialisationManagerFactory(serverMigrationManager);
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
            fileMenu.items.add(context.get(ViewMenuItemFactory.class, "history"));
            fileMenu.items.add(context.get(SeparatorMenuItemFactory.class));
            fileMenu.items.add(context.get(ViewMenuItemFactory.class, "products"));
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
            MicroserviceRestClientFactory<RichClientRoot, ServerRootFactory> restClient = new MicroserviceRestClientFactory<>();
            restClient.host.set("localhost");
            restClient.port.set(adminServerPort);
            restClient.path.set(null);
            restClient.user.set(user);
            restClient.passwordHash.set(passwordHash);
            restClient.factoryTreeBuilderBasedAttributeSetup.set(new FactoryTreeBuilderBasedAttributeSetup<>(serverRootFactoryFactoryTreeBuilder));
            return restClient;
        });

        factoryBuilder.addFactory(ViewMenuItemFactory.class, "configuration", Scope.SINGLETON, context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en("Configuration").de("Konfiguration");
            viewDescriptionFactory.icon.set(FontAwesome.Glyph.COG);
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));

            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(viewDescriptionFactory);
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            ConfigurationViewFactory configurationViewFactory = context.get(ConfigurationViewFactory.class);

            FactoryEditViewFactory<ServerRootFactory> factoryEditViewFactory = (FactoryEditViewFactory<ServerRootFactory>)context.get(FactoryEditViewFactory.class);
            factoryEditViewFactory.contentWidgetFactory.set(configurationViewFactory);
            viewFactory.widget.set(factoryEditViewFactory);

            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
            viewMenuItemFactory.view.set(viewFactory);
            return viewMenuItemFactory;
        });

        factoryBuilder.addFactory(ConfigurationViewFactory.class, Scope.SINGLETON);

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

        factoryBuilder.addFactory(ViewMenuItemFactory.class, "history", Scope.SINGLETON, context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en("History").de("Historie");
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));

            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(viewDescriptionFactory);
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            viewFactory.widget.set(context.get(HistoryViewFactory.class));

            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
            viewMenuItemFactory.view.set(viewFactory);
            return viewMenuItemFactory;
        });

        factoryBuilder.addFactory(ViewMenuItemFactory.class, "products", Scope.SINGLETON, context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en("Products").de("Produkte");
            viewDescriptionFactory.icon.set(FontAwesome.Glyph.LIST);
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));

            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(viewDescriptionFactory);
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            ProductsViewFactory configurationViewFactory = context.get(ProductsViewFactory.class);

            FactoryEditViewFactory<ServerRootFactory> factoryEditViewFactory = (FactoryEditViewFactory<ServerRootFactory>)context.get(FactoryEditViewFactory.class);
            factoryEditViewFactory.contentWidgetFactory.set(configurationViewFactory);
            viewFactory.widget.set(factoryEditViewFactory);

            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(viewDescriptionFactory);
            viewMenuItemFactory.view.set(viewFactory);
            return viewMenuItemFactory;
        });

        factoryBuilder.addFactory(ProductsViewFactory.class,Scope.SINGLETON);

        factoryBuilder.addFactory(FactoryEditViewFactory.class,Scope.PROTOTYPE, context ->{
            FactoryEditViewFactory<ServerRootFactory> factoryEditViewFactory = new FactoryEditViewFactory<>();
            factoryEditViewFactory.factoryEditManager.set(context.get(FactoryEditManagerFactory.class));
            factoryEditViewFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
            factoryEditViewFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            DataEditorFactory dataEditorFactory = context.get(DataEditorFactory.class);
            factoryEditViewFactory.dataEditorFactory.set(dataEditorFactory);
            factoryEditViewFactory.diffDialogBuilder.set(context.get(DiffDialogBuilderFactory.class));
            return factoryEditViewFactory;
        });

        factoryBuilder.addFactory(DataViewWidgetFactory.class,Scope.PROTOTYPE);
        factoryBuilder.addFactory(SeparatorMenuItemFactory.class,Scope.PROTOTYPE);


        return factoryBuilder;

    }

    private static class RichClientFactorySerialisationManagerFactory extends FactorySerialisationManagerFactory<ServerRootFactory> {
        private final MigrationManager<ServerRootFactory> serverMigrationManager;

        static {
            FactoryMetadataManager.getMetadata(RichClientFactorySerialisationManagerFactory.class).setNewCopyInstanceSupplier(
                    old -> new RichClientFactorySerialisationManagerFactory(old.serverMigrationManager)
            );
        }

        private RichClientFactorySerialisationManagerFactory(MigrationManager<ServerRootFactory> serverMigrationManager) {
            this.serverMigrationManager = serverMigrationManager;
        }

        @Override
        protected MigrationManager<ServerRootFactory> createImpl() {
            return serverMigrationManager;
        }
    }
}
