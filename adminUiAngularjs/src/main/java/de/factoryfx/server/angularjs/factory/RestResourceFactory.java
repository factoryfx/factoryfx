package de.factoryfx.server.angularjs.factory;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.angularjs.model.table.WebGuiTable;
import de.factoryfx.server.angularjs.model.view.GuiView;
import de.factoryfx.user.UserManagement;

public class RestResourceFactory<V,L,T extends FactoryBase<L,V>> extends SimpleFactoryBase<RestResource,Void> {
    public final FactoryReferenceAttribute<Layout,LayoutFactory> layout=new FactoryReferenceAttribute<>(LayoutFactory.class).labelText("Layout");
    public final ObjectValueAttribute<ApplicationServer<V,L,T>> applicationServer=new ObjectValueAttribute<ApplicationServer<V,L,T>>().labelText("applicationServer");
    public final ObjectValueAttribute<List<Class<? extends FactoryBase>>> appFactoryClasses = new ObjectValueAttribute<List<Class<? extends FactoryBase>>>().labelText("appFactoryClasses");
    public final ObjectValueAttribute<List<Locale>> locales = new ObjectValueAttribute<List<Locale>>().labelText("locales");
    public final ObjectValueAttribute<UserManagement> userManagement=new ObjectValueAttribute<UserManagement>().labelText("userManagement");
    public final ObjectValueAttribute<Supplier<V>> emptyVisitorCreator=new ObjectValueAttribute<Supplier<V>>().labelText("emptyVisitorCreator");
    public final ObjectValueAttribute<Function<V,List<WebGuiTable>>> dashboardTablesProvider=new ObjectValueAttribute<Function<V,List<WebGuiTable>>>().labelText("dashboardTablesProvider");
    public final ObjectValueAttribute<List<GuiView<?>>> guiViews=new ObjectValueAttribute<List<GuiView<?>>>().labelText("guiViews");
    public final FactoryReferenceAttribute<SessionStorage,SessionStorageFactory> sessionStorage= new FactoryReferenceAttribute<>(SessionStorageFactory.class).labelText("emptyVisitorCreator");

    //TODO why ObjectValueAttribute?
    @Override
    public RestResource createImpl() {
        return new RestResource<>(layout.instance(),applicationServer.get(),appFactoryClasses.get(),locales.get(),userManagement.get(),emptyVisitorCreator.get(),dashboardTablesProvider.get(),guiViews.get(), sessionStorage.instance());
    }

}
