package io.github.factoryfx.example.client.view;

import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import io.github.factoryfx.javafx.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import io.github.factoryfx.javafx.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.widget.factory.history.HistoryWidget;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

public class HistoryViewFactory extends WidgetFactory {

    public final FactoryAttribute<RichClientRoot,LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor =
            new FactoryAttribute<RichClientRoot,LongRunningActionExecutor, LongRunningActionExecutorFactory>().de("items").en("items");
    public final FactoryAttribute<RichClientRoot,UniformDesign, UniformDesignFactory> uniformDesign =
            new FactoryAttribute<RichClientRoot,UniformDesign, UniformDesignFactory>().de("uniformDesign").en("uniformDesign");

    public final FactoryAttribute<RichClientRoot,MicroserviceRestClient<ServerRootFactory, String>, MicroserviceRestClientFactory<RichClientRoot, ServerRootFactory, String>> restClient =
            new FactoryAttribute<RichClientRoot,MicroserviceRestClient<ServerRootFactory, String>, MicroserviceRestClientFactory<RichClientRoot, ServerRootFactory, String>>().de("restClient").en("restClient");
    public final FactoryAttribute<RichClientRoot,AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory> attributeEditorBuilder =
            new FactoryAttribute<RichClientRoot,AttributeVisualisationMappingBuilder, AttributeEditorBuilderFactory>().de("attribute editor").en("attribute editor");

    @Override
    protected Widget createWidget() {
        return new HistoryWidget<>(uniformDesign.instance(),
                                   longRunningActionExecutor.instance(),
                                   restClient.instance(),
                                   new DiffDialogBuilder(uniformDesign.instance(), attributeEditorBuilder.instance()));
    }
}
