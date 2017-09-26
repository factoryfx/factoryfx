package de.factoryfx.javafx.editor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.ReadOnlyObjectProperty;
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
        this.dataEditorState = new DataEditorState(null, new ArrayList<>(),attributeEditorBuilder,uniformDesign,this,dataVisualisationCustomizer);
    }

    public DataEditor(AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign) {
        this(attributeEditorBuilder,uniformDesign,(node, data) -> node);
    }

    SimpleObjectProperty<Data> editData = new SimpleObjectProperty<>();
    public ReadOnlyObjectProperty<Data> editData(){
        return editData;
    }

    public void edit(Data newValue) {
        updateState(dataEditorState.edit(newValue));
    }

    private void updateState(DataEditorState dataEditorState){
        editData.set(dataEditorState.getCurrentData());
        this.dataEditorState=dataEditorState;
        if (borderPane!=null){
            borderPane.setCenter(dataEditorState.createVisualisation());
        }
    }

    public void resetHistory(){
        updateState(dataEditorState.resetHistory());
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

}
