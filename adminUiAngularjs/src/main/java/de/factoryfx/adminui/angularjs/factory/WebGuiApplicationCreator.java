package de.factoryfx.adminui.angularjs.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.adminui.angularjs.model.table.WebGuiTable;
import de.factoryfx.adminui.angularjs.model.view.GuiView;
import de.factoryfx.adminui.angularjs.server.resourcehandler.ClasspathMinifingFileContentProvider;
import de.factoryfx.adminui.angularjs.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.DefaultApplicationServer;
import de.factoryfx.user.UserManagement;

public class WebGuiApplicationCreator<V, T extends FactoryBase<? extends LiveObject<V>, T>> {
    private final ApplicationServer<V, T> applicationServer;
    private final List<Class<? extends FactoryBase>> appFactoryClasses;
    private final UserManagement userManagement;
    private final Supplier<V> emptyVisitorCreator;
    private final Function<V, List<WebGuiTable>> dashboardTablesProvider;
    private final List<GuiView<T>> guiViews;

    public WebGuiApplicationCreator(ApplicationServer<V, T> applicationServer, List<Class<? extends FactoryBase>> appFactoryClasses, UserManagement userManagement, Supplier<V> emptyVisitorCreator, Function<V, List<WebGuiTable>> dashboardTablesProvider, List<GuiView<T>> guiViews) {
        this.applicationServer = applicationServer;
        this.appFactoryClasses = appFactoryClasses;
        this.userManagement = userManagement;
        this.emptyVisitorCreator = emptyVisitorCreator;
        this.dashboardTablesProvider = dashboardTablesProvider;
        this.guiViews = guiViews;
    }

    public <V> ApplicationServer<V,WebGuiServerFactory<V>> createApplication(
            WebGuiServerFactory<V> webGuiServerFactory,
            FactoryStorage<WebGuiServerFactory<V>>  factoryStorage
            ){
        return new DefaultApplicationServer<>(new FactoryManager<>(), factoryStorage);
    }

    public WebGuiServerFactory<V> createDefaultFactory() {
        WebGuiServerFactory<V> webGuiServerFactory =new WebGuiServerFactory<>();
        webGuiServerFactory.port.set(8089);
        webGuiServerFactory.host.set("localhost");
        webGuiServerFactory.sessionTimeoutS.set(60*30);

        WebGuiResourceFactory<V> webGuiResourceFactory = new WebGuiResourceFactory<>();
        WebGuiLayoutFactory webGuiLayoutFactory = new WebGuiLayoutFactory();
        webGuiLayoutFactory.title.en("Test example");

        webGuiLayoutFactory.userManagement.set(userManagement);
        webGuiResourceFactory.layout.set(webGuiLayoutFactory);
        webGuiResourceFactory.applicationServer.set(applicationServer);
        webGuiResourceFactory.appFactoryClasses.set(appFactoryClasses);
        webGuiResourceFactory.locales.set(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
        webGuiResourceFactory.userManagement.set(userManagement);
        webGuiResourceFactory.emptyVisitorCreator.set(emptyVisitorCreator);
        webGuiResourceFactory.dashboardTablesProvider.set(dashboardTablesProvider);
        webGuiResourceFactory.sessionStorage.set(new SessionStorageFactory());

        ArrayList<GuiView<?>> list = new ArrayList<>();
        guiViews.forEach(list::add);
        webGuiResourceFactory.guiViews.set(list);

        webGuiServerFactory.webGuiResource.set(webGuiResourceFactory);
        webGuiServerFactory.resourceHandler.set(new ConfigurableResourceHandler(new ClasspathMinifingFileContentProvider(), () -> UUID.randomUUID().toString()));
        return webGuiServerFactory;
    }

}
