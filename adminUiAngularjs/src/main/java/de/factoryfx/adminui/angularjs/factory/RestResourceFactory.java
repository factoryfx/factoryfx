package de.factoryfx.adminui.angularjs.factory;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.adminui.angularjs.model.table.WebGuiTable;
import de.factoryfx.adminui.angularjs.model.view.GuiView;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.UserManagement;

public class RestResourceFactory<V> extends FactoryBase<RestResource> {
    public final FactoryReferenceAttribute<Layout,LayoutFactory> layout=new FactoryReferenceAttribute<>(LayoutFactory.class,new AttributeMetadata().labelText("Layout"));
    public final ObjectValueAttribute<ApplicationServer<V,?>> applicationServer=new ObjectValueAttribute<>(new AttributeMetadata().labelText("applicationServer"));
    public final ObjectValueAttribute<List<Class<? extends FactoryBase>>> appFactoryClasses = new ObjectValueAttribute<>(new AttributeMetadata().labelText("appFactoryClasses"));
    public final ObjectValueAttribute<List<Locale>> locales = new ObjectValueAttribute<>(new AttributeMetadata().labelText("locales"));
    public final ObjectValueAttribute<UserManagement> userManagement=new ObjectValueAttribute<>(new AttributeMetadata().labelText("userManagement"));
    public final ObjectValueAttribute<Supplier<V>> emptyVisitorCreator=new ObjectValueAttribute<>(new AttributeMetadata().labelText("emptyVisitorCreator"));
    public final ObjectValueAttribute<Function<V,List<WebGuiTable>>> dashboardTablesProvider=new ObjectValueAttribute<>(new AttributeMetadata().labelText("dashboardTablesProvider"));
    public final ObjectValueAttribute<List<GuiView<?>>> guiViews=new ObjectValueAttribute<>(new AttributeMetadata().labelText("guiViews"));
    public final FactoryReferenceAttribute<SessionStorage,SessionStorageFactory> sessionStorage= new FactoryReferenceAttribute<>(SessionStorageFactory.class, new AttributeMetadata().labelText("emptyVisitorCreator"));

    @Override
    protected RestResource createImp(Optional<RestResource> previousLiveObject) {
        return new RestResource<>(layout.get().instance(),applicationServer.get(),appFactoryClasses.get(),locales.get(),userManagement.get(),emptyVisitorCreator.get(),dashboardTablesProvider.get(),guiViews.get(), sessionStorage.instance());
    }
}
