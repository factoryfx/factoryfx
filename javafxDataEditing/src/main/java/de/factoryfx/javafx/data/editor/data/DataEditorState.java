package de.factoryfx.javafx.data.editor.data;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.scene.Node;

import java.util.*;
import java.util.function.BiFunction;

//immutable editor state
public class DataEditorState {
    public static final int HISTORY_LIMIT = 20;
    final List<Data> displayedEntities;
    private final Data currentData;
    private final AttributeEditorBuilder attributeEditorBuilder;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final BiFunction<Node,Data,Node> visCustomizer;

    public DataEditorState(Data currentData, List<Data> displayedEntities, AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node,Data,Node> visCustomizer) {
        this.displayedEntities = displayedEntities;
        this.currentData = currentData;
        this.attributeEditorBuilder = attributeEditorBuilder;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.visCustomizer = visCustomizer;
    }

    public Node createVisualisation(){
        return new DataEditorStateVisualisation(currentData,displayedEntities,previousData(),nextData(),attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }

    public DataEditorState resetHistory() {
        return new DataEditorState(currentData,new ArrayList<>(Collections.singletonList(currentData)),attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }

    public DataEditorState withHistory(List<Data> data) {
        return new DataEditorState(currentData,data,attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }

    private Optional<Data> previousData(){
        int index = displayedEntities.indexOf(currentData)-1;
        if (index>=0){
            return Optional.ofNullable(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    private Optional<Data> nextData(){
        int index = displayedEntities.indexOf(currentData)+1;
        if (index<displayedEntities.size()){
            return Optional.ofNullable(displayedEntities.get(index));
        }
        return Optional.empty();
    }


    public DataEditorState back(){
        Data newData=currentData;
        Optional<Data> data = previousData();
        if(data.isPresent()){
            newData=data.get();
        }
        return new DataEditorState(newData,new ArrayList<>(displayedEntities),attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }

    public DataEditorState next(){
        Data newData=currentData;
        Optional<Data> data = nextData();
        if(data.isPresent()){
            newData=data.get();
        }
        return new DataEditorState(newData,new ArrayList<>(displayedEntities),attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }


    public DataEditorState edit(Data newValue) {
        if (!displayedEntities.contains(newValue)){
            removeUpToCurrent(currentData);
            displayedEntities.add(newValue);
        } else {
            int indexOfCurrent = displayedEntities.indexOf(currentData);
            int indexOfNewValue = displayedEntities.indexOf(newValue);
            if (indexOfNewValue > indexOfCurrent) {
                removeUpToCurrent(currentData);
                displayedEntities.add(newValue);
            }
        }
        if (displayedEntities.size()>HISTORY_LIMIT){
            displayedEntities.remove(0);
        }

        return new DataEditorState(newValue,new ArrayList<>(displayedEntities),attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }

    public Data getCurrentData() {
        return currentData;
    }

    private void removeUpToCurrent(Data newValue) {
        if (newValue == null)
            return;
        int idx = displayedEntities.indexOf(newValue);
        if (idx >= 0) {
            for (int i = displayedEntities.size()-1; i >= idx+1; i--) {
                displayedEntities.remove(i);
            }

        }
    }

    public DataEditorState reset() {
        return new DataEditorState(null,new ArrayList<>(),attributeEditorBuilder,uniformDesign,dataEditor, visCustomizer);
    }
}
