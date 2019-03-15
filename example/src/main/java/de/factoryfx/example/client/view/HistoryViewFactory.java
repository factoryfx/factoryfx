package de.factoryfx.example.client.view;

import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import de.factoryfx.javafx.factory.widget.factory.history.HistoryWidget;
import de.factoryfx.microservice.rest.client.MicroserviceRestClient;
import de.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

public class HistoryViewFactory extends WidgetFactory {

    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor =
            new FactoryReferenceAttribute<>(LongRunningActionExecutorFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign =
            new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<MicroserviceRestClient<ServerRootFactory, String>, MicroserviceRestClientFactory<RichClientRoot, ServerRootFactory, String>> restClient =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(MicroserviceRestClientFactory.class).de("restClient").en("restClient"));
    public final FactoryReferenceAttribute<AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder =
            new FactoryReferenceAttribute<>(AttributeEditorBuilderFactory.class).de("attribute editor").en("attribute editor");

    @Override
    protected Widget createWidget() {
        return new HistoryWidget<>(uniformDesign.instance(),
                                   longRunningActionExecutor.instance(),
                                   restClient.instance(),
                                   new DiffDialogBuilder(uniformDesign.instance(), attributeEditorBuilder.instance()));
    }
}
