package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutor;
import de.factoryfx.javafx.factory.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.data.widget.Widget;
import de.factoryfx.javafx.factory.widget.factory.WidgetFactory;
import de.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilder;
import de.factoryfx.javafx.factory.widget.factory.diffdialog.DiffDialogBuilderFactory;

/**
 *
 * @param <VS> server visitor
 * @param <RS> server root
 */
public class FactoryEditViewFactory<VS,RS extends FactoryBase<?,VS,RS>,S> extends WidgetFactory {
    public final FactoryReferenceAttribute<FactoryEditManager<VS,RS>, FactoryEditManagerFactory<VS,RS,S>> factoryEditManager = new FactoryReferenceAttribute<FactoryEditManager<VS,RS>, FactoryEditManagerFactory<VS,RS,S>>().setupUnsafe(FactoryEditManagerFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory> longRunningActionExecutor = new FactoryReferenceAttribute<>(LongRunningActionExecutorFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryPolymorphicReferenceAttribute<DataEditor> dataEditorFactory = new FactoryPolymorphicReferenceAttribute<>();
    public final FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<RS>> contentWidgetFactory = new FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<RS>>().setupUnsafe(FactoryAwareWidgetFactory.class);
    public final FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory> diffDialogBuilder = new FactoryReferenceAttribute<>(DiffDialogBuilderFactory.class);

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}
