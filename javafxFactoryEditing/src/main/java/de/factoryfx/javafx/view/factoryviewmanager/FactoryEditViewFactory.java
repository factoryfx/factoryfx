package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
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

public class FactoryEditViewFactory<V,R extends FactoryBase<?,V>> extends WidgetFactory<V> {
    public final FactoryReferenceAttribute<FactoryEditManager<V,R>, FactoryEditManagerFactory<V,R>> factoryEditManager = new FactoryReferenceAttribute<FactoryEditManager<V,R>, FactoryEditManagerFactory<V,R>>().setupUnsafe(FactoryEditManagerFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V>> longRunningActionExecutor = new FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V>>().setupUnsafe(LongRunningActionExecutorFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<DataEditor,? super SimpleFactoryBase<DataEditor,V>> dataEditorFactory = new FactoryReferenceAttribute<>(SimpleFactoryBase.class);
    public final FactoryReferenceAttribute<FactoryAwareWidget<R>,FactoryAwareWidgetFactory<V,R>> contentWidgetFactory = new FactoryReferenceAttribute<FactoryAwareWidget<R>,FactoryAwareWidgetFactory<V,R>>().setupUnsafe(FactoryAwareWidgetFactory.class);
    public final FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V>> diffDialogBuilder = new FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V>>().setupUnsafe(DiffDialogBuilderFactory.class);

    @Override
    protected Widget createWidget() {
        return new FactoryEditView<>(longRunningActionExecutor.instance(), factoryEditManager.instance(), contentWidgetFactory.instance(), uniformDesign.instance(), dataEditorFactory.instance(), diffDialogBuilder.instance());
    }
}
