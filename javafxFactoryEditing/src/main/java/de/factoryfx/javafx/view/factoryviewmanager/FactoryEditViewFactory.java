package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.LongRunningActionExecutor;
import de.factoryfx.javafx.util.LongRunningActionExecutorFactory;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.WidgetFactory;
import de.factoryfx.javafx.widget.diffdialog.DiffDialogBuilder;
import de.factoryfx.javafx.widget.diffdialog.DiffDialogBuilderFactory;

/**
 *
 * @param <V> client visitor
 * @param <R> client root
 * @param <VS> server visitor
 * @param <RS> server root
 */
public class FactoryEditViewFactory<V,R extends FactoryBase<?,V>, VS,RS extends FactoryBase<?,VS>,S> extends WidgetFactory<V> {
    public final FactoryReferenceAttribute<FactoryEditManager<VS,RS,S>, FactoryEditManagerFactory<V,R,VS,RS,S>> factoryEditManager = new FactoryReferenceAttribute<FactoryEditManager<VS,RS,S>, FactoryEditManagerFactory<V,R,VS,RS,S>>().setupUnsafe(FactoryEditManagerFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V>> longRunningActionExecutor = new FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V>>().setupUnsafe(LongRunningActionExecutorFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryPolymorphicReferenceAttribute<DataEditor> dataEditorFactory = new FactoryPolymorphicReferenceAttribute<>();
    public final FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<VS,RS>> contentWidgetFactory = new FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<VS,RS>>().setupUnsafe(FactoryAwareWidgetFactory.class);
    public final FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V>> diffDialogBuilder = new FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V>>().setupUnsafe(DiffDialogBuilderFactory.class);

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}
