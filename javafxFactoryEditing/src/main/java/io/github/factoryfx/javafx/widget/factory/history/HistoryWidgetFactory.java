package io.github.factoryfx.javafx.widget.factory.history;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.util.LongRunningActionExecutor;
import io.github.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import io.github.factoryfx.javafx.widget.Widget;
import io.github.factoryfx.javafx.widget.factory.WidgetFactory;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilder;
import io.github.factoryfx.javafx.widget.factory.diffdialog.DiffDialogBuilderFactory;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClient;
import io.github.factoryfx.microservice.rest.client.MicroserviceRestClientFactory;

public class HistoryWidgetFactory<RS extends FactoryBase<?, RS>> extends WidgetFactory {
    public final FactoryAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor =
        new FactoryAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory>().de("items").en("items");
    public final FactoryAttribute<UniformDesign, UniformDesignFactory> uniformDesign =
        new FactoryAttribute<UniformDesign, UniformDesignFactory>().de("uniformDesign").en("uniformDesign");
    public final FactoryAttribute<MicroserviceRestClient<RS>, MicroserviceRestClientFactory<RichClientRoot, RS>> restClient =
        new FactoryAttribute<MicroserviceRestClient<RS>, MicroserviceRestClientFactory<RichClientRoot, RS>>().de("restClient").en("restClient");
    public final FactoryAttribute<DiffDialogBuilder<RS>, DiffDialogBuilderFactory<RS>> diffDialogBuilder =
        new FactoryAttribute<DiffDialogBuilder<RS>, DiffDialogBuilderFactory<RS>>().de("restClient").en("restClient");

    @Override
    protected Widget createWidget() {
        return new HistoryWidget<>(uniformDesign.instance(), longRunningActionExecutor.instance(), restClient.instance(), diffDialogBuilder.instance());
    }
}
