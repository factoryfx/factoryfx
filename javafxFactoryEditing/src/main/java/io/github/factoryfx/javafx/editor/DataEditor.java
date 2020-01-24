package io.github.factoryfx.javafx.editor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class DataEditor implements Widget {
    DataEditorState dataEditorState;


    /**
     *
     * @param attributeVisualisationMappingBuilder attributeEditorBuilder
     * @param uniformDesign uniformDesign
     * @param dataVisualisationCustomizer way to customize or extend the editor visualisation for a specific data class, e.g. add a green border(silly example), add special button
     */
    public DataEditor(AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign, BiFunction<Node, FactoryBase<?,?>,Node> dataVisualisationCustomizer) {
        this.dataEditorState = new DataEditorState(null, new ArrayDeque<>(),new ArrayDeque<>(), attributeVisualisationMappingBuilder,uniformDesign,this,dataVisualisationCustomizer,true);
        historyNavigationVisible.set(true);
        historyNavigationVisible.addListener(observable -> updateState(dataEditorState.setShowNavigation(historyNavigationVisible.get())));
    }

    public DataEditor(AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign) {
        this(attributeVisualisationMappingBuilder,uniformDesign,(node, data) -> node);
    }

    SimpleObjectProperty<FactoryBase<?,?>> editData = new SimpleObjectProperty<>();
    public ReadOnlyObjectProperty<FactoryBase<?,?>> editData(){
        return editData;
    }

    /**
     * edit without history reset
     * @param newValue new value
     */
    public void navigate(FactoryBase<?,?> newValue) {
        updateState(dataEditorState.edit(newValue));
    }

    public void navigateBack(FactoryBase<?,?> newValue) {
        updateState(dataEditorState.navigateBack(newValue));
    }

    public void edit(FactoryBase<?,?> newValue) {
        updateState(dataEditorState.edit(newValue));
        updateState(dataEditorState.resetHistory());
    }

    private void updateState(DataEditorState dataEditorState){
        this.dataEditorState.destroy();
        editData.set(dataEditorState.getCurrentData());
        this.dataEditorState=dataEditorState;
        if (borderPane!=null){
            borderPane.setCenter(dataEditorState.createVisualisation());
        }
    }

    public void setHistory(List<FactoryBase<?,?>> data){
        updateState(dataEditorState.withHistory(data));
    }

    public void reset(){
        editData.set(null);
        updateState(dataEditorState.reset());
    }

    BorderPane borderPane;
    @Override
    public Node createContent() {
        borderPane = new BorderPane();
        borderPane.setCenter(dataEditorState.createVisualisation());
        return borderPane;
    }

    void back(){
        updateState(dataEditorState.back());
    }

    void next(){
        updateState(dataEditorState.next());
    }

    private final SimpleBooleanProperty historyNavigationVisible = new SimpleBooleanProperty(true);
    public SimpleBooleanProperty historyNavigationVisibleProperty(){
        return historyNavigationVisible;
    }

    @Override
    public void destroy() {
        dataEditorState.destroy();
    }
}
