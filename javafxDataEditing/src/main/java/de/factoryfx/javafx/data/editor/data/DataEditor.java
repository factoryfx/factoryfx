package de.factoryfx.javafx.data.editor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.data.widget.Widget;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class DataEditor implements Widget {
    DataEditorState dataEditorState;


    /**
     *
     * @param attributeEditorBuilder attributeEditorBuilder
     * @param uniformDesign uniformDesign
     * @param dataVisualisationCustomizer way to customize or extend the editor visualisation for a specific data class, e.g. add a green border(silly example), add special button
     */
    public DataEditor(AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign, BiFunction<Node,Data,Node> dataVisualisationCustomizer) {
        this.dataEditorState = new DataEditorState(null, new ArrayList<>(),attributeEditorBuilder,uniformDesign,this,dataVisualisationCustomizer,true);
        historyNavigationVisible.set(true);
        historyNavigationVisible.addListener(observable -> updateState(dataEditorState.setShowNavigation(historyNavigationVisible.get())));
    }

    public DataEditor(AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign) {
        this(attributeEditorBuilder,uniformDesign,(node, data) -> node);
    }

    SimpleObjectProperty<Data> editData = new SimpleObjectProperty<>();
    public ReadOnlyObjectProperty<Data> editData(){
        return editData;
    }

    /**
     * edit without history reset
     * @param newValue new value
     */
    public void navigate(Data newValue) {
        updateState(dataEditorState.edit(newValue));
    }

    public void edit(Data newValue) {
        updateState(dataEditorState.edit(newValue));
        updateState(dataEditorState.resetHistory());
    }

    private void updateState(DataEditorState dataEditorState){
        editData.set(dataEditorState.getCurrentData());
        this.dataEditorState=dataEditorState;
        if (borderPane!=null){
            borderPane.setCenter(dataEditorState.createVisualisation());
        }
    }

    public void setHistory(List<Data> data){
        updateState(dataEditorState.withHistory(data));
    }

    public void reset(){
        editData.set(null);
        updateState(dataEditorState.reset());
    }

    BorderPane borderPane;
    @Override
    @SuppressWarnings("unchecked")
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

}
