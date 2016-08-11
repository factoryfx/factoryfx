package de.factoryfx.development.factory;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.development.angularjs.model.table.WebGuiTable;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.ReferenceAttribute;
import de.factoryfx.factory.attribute.util.ObjectValueAttribute;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.UserManagement;

public class WebGuiResourceFactory<V> extends FactoryBase<WebGuiResource,WebGuiResourceFactory<V>> {
    public final ReferenceAttribute<WebGuiLayoutFactory> layout=new ReferenceAttribute<>(WebGuiLayoutFactory.class,new AttributeMetadata().labelText("Layout"));
    public final ObjectValueAttribute<ApplicationServer<V,?>> applicationServer=new ObjectValueAttribute<>(new AttributeMetadata().labelText("applicationServer"));
    public final ObjectValueAttribute<List<Class<? extends FactoryBase>>> appFactoryClasses = new ObjectValueAttribute<>(new AttributeMetadata().labelText("appFactoryClasses"));
    public final ObjectValueAttribute<List<Locale>> locales = new ObjectValueAttribute<>(new AttributeMetadata().labelText("locales"));
    public final ObjectValueAttribute<UserManagement> userManagement=new ObjectValueAttribute<>(new AttributeMetadata().labelText("userManagement"));
    public final ObjectValueAttribute<Supplier<V>> emptyVisitorCreator=new ObjectValueAttribute<>(new AttributeMetadata().labelText("emptyVisitorCreator"));
    public final ObjectValueAttribute<Function<V,List<WebGuiTable>>> dashboardTablesProvider=new ObjectValueAttribute<>(new AttributeMetadata().labelText("dashboardTablesProvider"));

    @Override
    protected WebGuiResource createImp(Optional<WebGuiResource> previousLiveObject) {
        return new WebGuiResource<>(layout.get().create(),applicationServer.get(),appFactoryClasses.get(),locales.get(),userManagement.get(),emptyVisitorCreator.get(),dashboardTablesProvider.get());
    }
}
