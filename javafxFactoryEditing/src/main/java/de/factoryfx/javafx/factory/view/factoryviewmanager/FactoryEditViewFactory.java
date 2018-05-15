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
 * @param <V> client visitor
 * @param <R> client root
 * @param <VS> server visitor
 * @param <RS> server root
 */
public class FactoryEditViewFactory<V,R extends FactoryBase<?,V,R>, VS,RS extends FactoryBase<?,VS,RS>,S> extends WidgetFactory<V,R> {
    public final FactoryReferenceAttribute<FactoryEditManager<VS,RS,S>, FactoryEditManagerFactory<V,R,VS,RS,S>> factoryEditManager = new FactoryReferenceAttribute<FactoryEditManager<VS,RS,S>, FactoryEditManagerFactory<V,R,VS,RS,S>>().setupUnsafe(FactoryEditManagerFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V,R>> longRunningActionExecutor = new FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V,R>>().setupUnsafe(LongRunningActionExecutorFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V,R>> uniformDesign = new FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V,R>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryPolymorphicReferenceAttribute<DataEditor> dataEditorFactory = new FactoryPolymorphicReferenceAttribute<>();
    public final FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<VS,RS>> contentWidgetFactory = new FactoryReferenceAttribute<FactoryAwareWidget<RS>,FactoryAwareWidgetFactory<VS,RS>>().setupUnsafe(FactoryAwareWidgetFactory.class);
    public final FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V,R>> diffDialogBuilder = new FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V,R>>().setupUnsafe(DiffDialogBuilderFactory.class);

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}
