package de.factoryfx.javafx.editor.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

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
