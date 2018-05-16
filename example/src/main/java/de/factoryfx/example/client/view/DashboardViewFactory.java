package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

public class DashboardViewFactory extends WidgetFactory {
    public final FactoryReferenceAttribute<MicroserviceRestClient<OrderCollector,ServerRootFactory,Void>,MicroserviceRestClientFactory<Void,RichClientRoot,OrderCollector,ServerRootFactory,Void>> restClient = new FactoryReferenceAttribute<MicroserviceRestClient<OrderCollector,ServerRootFactory,Void>,MicroserviceRestClientFactory<Void,RichClientRoot,OrderCollector,ServerRootFactory,Void>>().setupUnsafe(MicroserviceRestClientFactory.class);

    @Override
    protected Widget createWidget() {
        return new DashboardView(restClient.instance());
    }
}
