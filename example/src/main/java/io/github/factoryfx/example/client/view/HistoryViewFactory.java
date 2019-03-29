package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.Widget;
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

    public final FactoryReferenceAttribute<RichClientRoot,LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor =
            new FactoryReferenceAttribute<RichClientRoot,LongRunningActionExecutor, LongRunningActionExecutorFactory>().de("items").en("items");
    public final FactoryReferenceAttribute<RichClientRoot,UniformDesign, UniformDesignFactory> uniformDesign =
            new FactoryReferenceAttribute<RichClientRoot,UniformDesign, UniformDesignFactory>().de("uniformDesign").en("uniformDesign");

    public final FactoryReferenceAttribute<RichClientRoot,MicroserviceRestClient<ServerRootFactory, String>, MicroserviceRestClientFactory<RichClientRoot, ServerRootFactory, String>> restClient =
            new FactoryReferenceAttribute<RichClientRoot,MicroserviceRestClient<ServerRootFactory, String>, MicroserviceRestClientFactory<RichClientRoot, ServerRootFactory, String>>().de("restClient").en("restClient");
    public final FactoryReferenceAttribute<RichClientRoot,AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder =
            new FactoryReferenceAttribute<RichClientRoot,AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory>().de("attribute editor").en("attribute editor");

    @Override
    protected Widget createWidget() {
        return new HistoryWidget<>(uniformDesign.instance(),
                                   longRunningActionExecutor.instance(),
                                   restClient.instance(),
                                   new DiffDialogBuilder(uniformDesign.instance(), attributeEditorBuilder.instance()));
    }
}
