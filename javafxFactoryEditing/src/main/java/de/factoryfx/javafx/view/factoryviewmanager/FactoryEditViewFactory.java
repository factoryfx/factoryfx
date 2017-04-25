package de.factoryfx.javafx.view.factoryviewmanager;

import de.factoryfx.data.attribute.AttributeMetadata;
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
    public final FactoryReferenceAttribute<FactoryEditManager, FactoryEditManagerFactory<V,R>> factoryEditManager = new FactoryReferenceAttribute<>((new AttributeMetadata()).de("uniformDesign").en("uniformDesign"),FactoryEditManagerFactory.class);
    public final FactoryReferenceAttribute<LongRunningActionExecutor, LongRunningActionExecutorFactory<V>> longRunningActionExecutor = new FactoryReferenceAttribute<>((new AttributeMetadata()).de("items").en("items"), LongRunningActionExecutorFactory.class);
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>((new AttributeMetadata()).de("uniformDesign").en("uniformDesign"), UniformDesignFactory.class);
    public final FactoryReferenceAttribute<DataEditor,? super SimpleFactoryBase<DataEditor,V>> dataEditorFactory = new FactoryReferenceAttribute<>(new AttributeMetadata(),SimpleFactoryBase.class);
    public final FactoryReferenceAttribute<FactoryAwareWidget,FactoryAwareWidgetFactory> contentWidgetFactory = new FactoryReferenceAttribute<>(FactoryAwareWidgetFactory.class,new AttributeMetadata());
    public final FactoryReferenceAttribute<DiffDialogBuilder,DiffDialogBuilderFactory<V>> diffDialogBuilder = new FactoryReferenceAttribute<>(new AttributeMetadata(),DiffDialogBuilderFactory.class);

    @Override
    protected Widget createWidget() {
        return new FactoryEditView(longRunningActionExecutor.instance(),factoryEditManager.instance(), contentWidgetFactory.instance() ,uniformDesign.instance(),dataEditorFactory.instance(),diffDialogBuilder.instance());
    }
}
