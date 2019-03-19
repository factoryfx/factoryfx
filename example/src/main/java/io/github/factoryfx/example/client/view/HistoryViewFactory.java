package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.data.widget.Widget;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import io.github.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.factory.widget.factory.history.HistoryWidget;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

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
