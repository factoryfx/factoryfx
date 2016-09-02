package de.factoryfx.adminui.angularjs.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
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

public class WebGuiApplication<V,T extends FactoryBase<? extends LiveObject<V>, T>> {

    private final DefaultApplicationServer<V, WebGuiServerFactory<V>> webGuiApplicationServer;

    public WebGuiApplication(
            ApplicationServer<V,T> applicationServer,
            List<Class<? extends FactoryBase>> appFactoryClasses,
            Function<WebGuiServerFactory<V>,FactoryStorage<WebGuiServerFactory<V>>>  factoryStorageProvider,
            UserManagement userManagement,
            Consumer<WebGuiServerFactory<V>> configurationCustomiser,
            Supplier<V> emptyVisitorCreator,
            Function<V,List<WebGuiTable>> dashboardTablesProvider,
            List<GuiView<T>> guiViews){
        WebGuiServerFactory<V> webGuiServerFactory =new WebGuiServerFactory<>();
        webGuiServerFactory.port.set(8089);
        webGuiServerFactory.host.set("localhost");

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

        configurationCustomiser.accept(webGuiServerFactory);

        webGuiApplicationServer = new DefaultApplicationServer<>(new FactoryManager<>(), factoryStorageProvider.apply(webGuiServerFactory));
    }

    public WebGuiApplication(
            ApplicationServer<V,T> applicationServer,
            List<Class<? extends FactoryBase>> appFactoryClasses,
            Function<WebGuiServerFactory<V>,FactoryStorage<WebGuiServerFactory<V>>>  factoryStorageProvider,
            UserManagement userManagement,
            Supplier<V> emptyVisitorCreator,
            Function<V,List<WebGuiTable>> dashboardTablesProvider,
            List<GuiView<T>> guiViews){
        this(applicationServer,appFactoryClasses,factoryStorageProvider,userManagement,(config)->{},emptyVisitorCreator,dashboardTablesProvider,guiViews);
    }

    public void start(){
        webGuiApplicationServer.start();
    }

    public DefaultApplicationServer<V, WebGuiServerFactory<V>> getServer(){
        return webGuiApplicationServer;
    }



}
