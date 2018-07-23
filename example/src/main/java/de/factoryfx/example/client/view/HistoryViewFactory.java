package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryEditManager;
import de.factoryfx.javafx.factory.view.factoryviewmanager.FactoryEditManagerFactory;
import de.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import de.factoryfx.javafx.factory.widget.factory.history.HistoryWidget;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

public class HistoryViewFactory extends WidgetFactory {

    public final FactoryReferenceAttribute<FactoryEditManager, FactoryEditManagerFactory<OrderCollector, ServerRootFactory, String>> factoryEditManager =
        new FactoryReferenceAttribute<FactoryEditManager, FactoryEditManagerFactory<OrderCollector, ServerRootFactory, String>>().setupUnsafe(FactoryEditManagerFactory.class)
            .de("uniformDesign")
            .labelText("uniformDesign");
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor = new FactoryReferenceAttribute<>(LongRunningActionExecutorFactory.class)
            .de("items")
            .labelText("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class)
            .de("uniformDesign")
            .labelText("uniformDesign");
    public final FactoryReferenceAttribute<MicroserviceRestClient<OrderCollector, ServerRootFactory, String>, MicroserviceRestClientFactory<Void, RichClientRoot, OrderCollector, ServerRootFactory, String>> restClient =
        new FactoryReferenceAttribute<MicroserviceRestClient<OrderCollector, ServerRootFactory, String>, MicroserviceRestClientFactory<Void, RichClientRoot, OrderCollector, ServerRootFactory, String>>().setupUnsafe(MicroserviceRestClientFactory.class)
            .de("restClient")
            .labelText("restClient");
    public final FactoryReferenceAttribute<AttributeEditorBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder = new FactoryReferenceAttribute<>(AttributeEditorBuilderFactory.class)
            .de("attribute editor")
            .labelText("attribute editor");

    @Override
    protected Widget createWidget() {
        return new HistoryWidget<>(uniformDesign.instance(),
                                   longRunningActionExecutor.instance(),
                                   restClient.instance(),
                                   new DiffDialogBuilder(uniformDesign.instance(), attributeEditorBuilder.instance()));
    }
}
