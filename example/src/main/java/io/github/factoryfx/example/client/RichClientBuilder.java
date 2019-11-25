package io.github.factoryfx.example.client;

import io.github.factoryfx.example.client.view.*;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.storage.migration.MigrationManager;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.DataEditorFactory;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactoryBuilder;
import io.github.factoryfx.javafx.factoryviewmanager.FactoryAwareWidgetFactory;
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
import io.github.factoryfx.javafx.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.widget.factory.tree.DataTreeWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.masterdetail.DataViewWidgetFactory;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilderFactory;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.eclipse.jetty.server.Server;

import java.util.Locale;


public class RichClientBuilder {


    public static FactoryTreeBuilder<Stage,RichClientRoot> createFactoryBuilder(int adminServerPort, Stage primaryStage, String user, String passwordHash, Locale locale, FactoryTreeBuilder<Server, JettyServerRootFactory> serverRootFactoryFactoryTreeBuilder, MigrationManager<JettyServerRootFactory> serverMigrationManager) {
        FactoryTreeBuilder<Stage,RichClientRoot> factoryBuilder = new FactoryTreeBuilder<>(RichClientRoot.class);

        factoryBuilder.addFactory(LongRunningActionExecutorFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactoryUnsafe(FactoryEditManagerFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(AttributeEditorBuilderFactory.class, Scope.SINGLETON, ctx -> new AttributeEditorBuilderFactoryBuilder().build(ctx.get(UniformDesignFactory.class)));
        factoryBuilder.addFactory(DataEditorFactory.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(ViewsDisplayWidgetFactory.class, Scope.SINGLETON);
        factoryBuilder.addFactory(DiffDialogBuilderFactory.class, Scope.PROTOTYPE);
        factoryBuilder.addFactory(DataTreeWidgetFactory.class, Scope.PROTOTYPE);

        factoryBuilder.addFactoryUnsafe(FactorySerialisationManagerFactory.class, Scope.SINGLETON, (context)->{
            return new RichClientFactorySerialisationManagerFactory(serverMigrationManager);
        });

        factoryBuilder.addSingleton(UniformDesignFactory.class, voidSimpleFactoryContext -> {
            UniformDesignFactory uniformDesignFactory = new UniformDesignFactory();
            uniformDesignFactory.locale.set(locale);
            uniformDesignFactory.askBeforeDelete.set(false);
            return uniformDesignFactory;
        });

        factoryBuilder.addSingleton(ViewMenuFactory.class, "file", context -> {
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

        factoryBuilder.addSingleton(StageFactory.class, context -> {
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

        factoryBuilder.addFactoryUnsafe(MicroserviceRestClientFactory.class, Scope.SINGLETON, context -> {
            MicroserviceRestClientFactory<RichClientRoot, JettyServerRootFactory> restClient = new MicroserviceRestClientFactory<>();
            restClient.host.set("localhost");
            restClient.port.set(adminServerPort);
            restClient.path.set(null);
            restClient.user.set(user);
            restClient.passwordHash.set(passwordHash);
            restClient.factoryTreeBuilderBasedAttributeSetup.set(new FactoryTreeBuilderBasedAttributeSetup<>(serverRootFactoryFactoryTreeBuilder));
            return restClient;
        });

        addViewFactoryAware(factoryBuilder, ConfigurationViewFactory.class, "configuration",FontAwesome.Glyph.COG,"Configuration","Konfiguration");
        addView(factoryBuilder, DashboardViewFactory.class, "dashboard",FontAwesome.Glyph.COG,"Dashboard","Dashboard");
        addView(factoryBuilder, HistoryViewFactory.class, "history",FontAwesome.Glyph.COG,"History","Historie");
        addViewFactoryAware(factoryBuilder, ProductsViewFactory.class, "products",FontAwesome.Glyph.COG,"Products","Produkte");

        factoryBuilder.addFactoryUnsafe(FactoryEditViewFactory.class,Scope.PROTOTYPE, context ->{
            FactoryEditViewFactory<JettyServerRootFactory> factoryEditViewFactory = new FactoryEditViewFactory<>();
            factoryEditViewFactory.factoryEditManager.set(context.getUnsafe(FactoryEditManagerFactory.class));
            factoryEditViewFactory.longRunningActionExecutor.set(context.get(LongRunningActionExecutorFactory.class));
            factoryEditViewFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            DataEditorFactory dataEditorFactory = context.get(DataEditorFactory.class);
            factoryEditViewFactory.dataEditorFactory.set(dataEditorFactory);
            factoryEditViewFactory.diffDialogBuilder.set(context.get(DiffDialogBuilderFactory.class));
            return factoryEditViewFactory;
        });

        factoryBuilder.addFactoryUnsafe(DataViewWidgetFactory.class,Scope.PROTOTYPE);
        factoryBuilder.addFactory(SeparatorMenuItemFactory.class,Scope.PROTOTYPE);

        factoryBuilder.markAsNonPersistentFactoryBuilder();
        return factoryBuilder;

    }

    private static void addViewFactoryAware(FactoryTreeBuilder<Stage, RichClientRoot> factoryBuilder, Class<? extends FactoryAwareWidgetFactory<JettyServerRootFactory>> viewFactoryClass, String builderName, FontAwesome.Glyph icon, String textEn, String textDe) {
        factoryBuilder.addSingleton(ViewDescriptionFactory.class, builderName+"DescriptionFactory", context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en(textEn).de(textDe);
            viewDescriptionFactory.icon.set(icon);
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            return viewDescriptionFactory;
        });

        factoryBuilder.addSingleton(ViewFactory.class, builderName+"View", context -> {
            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(context.get(ViewDescriptionFactory.class,builderName+"DescriptionFactory"));
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));

            FactoryEditViewFactory<JettyServerRootFactory> factoryEditViewFactory = context.getUnsafe(FactoryEditViewFactory.class);
            factoryEditViewFactory.contentWidgetFactory.set(context.get(viewFactoryClass));
            viewFactory.widget.set(factoryEditViewFactory);
            return viewFactory;
        });

        factoryBuilder.addSingleton(ViewMenuItemFactory.class, builderName, context -> {
            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(context.get(ViewDescriptionFactory.class,builderName+"DescriptionFactory"));
            viewMenuItemFactory.view.set(context.get(ViewFactory.class,builderName+"View"));
            return viewMenuItemFactory;
        });

        factoryBuilder.addPrototype(viewFactoryClass);
    }

    private static void addView(FactoryTreeBuilder<Stage, RichClientRoot> factoryBuilder, Class<? extends WidgetFactory> viewFactoryClass, String builderName, FontAwesome.Glyph icon, String textEn, String textDe) {
        factoryBuilder.addSingleton(ViewDescriptionFactory.class, builderName+"DescriptionFactory", context -> {
            ViewDescriptionFactory viewDescriptionFactory = new ViewDescriptionFactory();
            viewDescriptionFactory.text.en(textEn).de(textDe);
            viewDescriptionFactory.icon.set(icon);
            viewDescriptionFactory.uniformDesign.set(context.get(UniformDesignFactory.class));
            return viewDescriptionFactory;
        });

        factoryBuilder.addSingleton(ViewFactory.class, builderName+"View", context -> {
            ViewFactory viewFactory = new ViewFactory();
            viewFactory.viewDescription.set(context.get(ViewDescriptionFactory.class,builderName+"DescriptionFactory"));
            viewFactory.viewsDisplayWidget.set(context.get(ViewsDisplayWidgetFactory.class));
            viewFactory.widget.set(context.get(viewFactoryClass));
            return viewFactory;
        });

        factoryBuilder.addSingleton(ViewMenuItemFactory.class, builderName, context -> {
            ViewMenuItemFactory viewMenuItemFactory = new ViewMenuItemFactory();
            viewMenuItemFactory.viewDescription.set(context.get(ViewDescriptionFactory.class,builderName+"DescriptionFactory"));
            viewMenuItemFactory.view.set(context.get(ViewFactory.class,builderName+"View"));
            return viewMenuItemFactory;
        });

        factoryBuilder.addPrototype(viewFactoryClass);
    }




    private static class RichClientFactorySerialisationManagerFactory extends FactorySerialisationManagerFactory<JettyServerRootFactory> {
        private final MigrationManager<JettyServerRootFactory> serverMigrationManager;

        static {
            FactoryMetadataManager.getMetadata(RichClientFactorySerialisationManagerFactory.class).setNewCopyInstanceSupplier(
                    old -> new RichClientFactorySerialisationManagerFactory(old.serverMigrationManager)
            );
        }

        private RichClientFactorySerialisationManagerFactory(MigrationManager<JettyServerRootFactory> serverMigrationManager) {
            this.serverMigrationManager = serverMigrationManager;
        }

        @Override
        protected MigrationManager<JettyServerRootFactory> createImpl() {
            return serverMigrationManager;
        }
    }
}
