package de.factoryfx.adminui.angularjs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.adminui.angularjs.factory.server.HttpServerFactory;
import de.factoryfx.adminui.angularjs.factory.LayoutFactory;
import de.factoryfx.adminui.angularjs.factory.RestResourceFactory;
import de.factoryfx.adminui.angularjs.factory.SessionStorageFactory;
import de.factoryfx.adminui.angularjs.model.table.WebGuiTable;
import de.factoryfx.adminui.angularjs.model.view.GuiView;
import de.factoryfx.adminui.angularjs.factory.server.resourcehandler.ClasspathMinifingFileContentProvider;
import de.factoryfx.adminui.angularjs.factory.server.resourcehandler.ConfigurableResourceHandler;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.datastorage.FactoryStorage;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.DefaultApplicationServer;
import de.factoryfx.user.UserManagement;

public class WebGuiApplicationCreator<V, T extends FactoryBase<? extends LiveObject<V>>> {
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

    public ApplicationServer<Void,HttpServerFactory<V>> createApplication(FactoryStorage<HttpServerFactory<V>>  factoryStorage){
        return new DefaultApplicationServer<>(new FactoryManager<>(), factoryStorage);
    }

    public HttpServerFactory<V> createDefaultFactory() {
        HttpServerFactory<V> httpServerFactory =new HttpServerFactory<>();
        httpServerFactory.port.set(8089);
        httpServerFactory.host.set("localhost");
        httpServerFactory.sessionTimeoutS.set(60*30);

        RestResourceFactory<V> restResourceFactory = new RestResourceFactory<>();
        LayoutFactory layoutFactory = new LayoutFactory();
        layoutFactory.title.en("Admin UI");

        layoutFactory.userManagement.set(userManagement);
        restResourceFactory.layout.set(layoutFactory);
        restResourceFactory.applicationServer.set(applicationServer);
        restResourceFactory.appFactoryClasses.set(appFactoryClasses);
        restResourceFactory.locales.set(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
        restResourceFactory.userManagement.set(userManagement);
        restResourceFactory.emptyVisitorCreator.set(emptyVisitorCreator);
        restResourceFactory.dashboardTablesProvider.set(dashboardTablesProvider);
        restResourceFactory.sessionStorage.set(new SessionStorageFactory());

        ArrayList<GuiView<?>> list = new ArrayList<>();
        guiViews.forEach(list::add);
        restResourceFactory.guiViews.set(list);

        httpServerFactory.webGuiResource.set(restResourceFactory);
        httpServerFactory.resourceHandler.set(new ConfigurableResourceHandler(new ClasspathMinifingFileContentProvider(), () -> UUID.randomUUID().toString()));
        return httpServerFactory;
    }

}
