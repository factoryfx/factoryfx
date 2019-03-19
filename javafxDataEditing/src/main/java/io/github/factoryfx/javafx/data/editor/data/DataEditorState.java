package io.github.factoryfx.javafx.data.editor.data;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import javafx.scene.Node;

import java.util.*;
import java.util.function.BiFunction;

//immutable editor state
public class DataEditorState {
    public static final int HISTORY_LIMIT = 20;
    final List<Data> displayedEntities;
    private final Data currentData;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final BiFunction<Node,Data,Node> visCustomizer;
    private final boolean showNavigation;

    public DataEditorState(Data currentData, List<Data> displayedEntities, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node,Data,Node> visCustomizer, boolean showNavigation) {
        this.displayedEntities = displayedEntities;
        this.currentData = currentData;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.visCustomizer = visCustomizer;
        this.showNavigation = showNavigation;
    }

    private DataEditorStateVisualisation editorStateVisualisation;
    public Node createVisualisation(){
        editorStateVisualisation=new DataEditorStateVisualisation(currentData,displayedEntities,previousData(),nextData(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
        return editorStateVisualisation;
    }

    public DataEditorState resetHistory() {
        return new DataEditorState(currentData,new ArrayList<>(Collections.singletonList(currentData)), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState withHistory(List<Data> data) {
        return new DataEditorState(currentData,data, attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
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
        return new DataEditorState(newData,new ArrayList<>(displayedEntities), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState next(){
        Data newData=currentData;
        Optional<Data> data = nextData();
        if(data.isPresent()){
            newData=data.get();
        }
        return new DataEditorState(newData,new ArrayList<>(displayedEntities), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
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

        return new DataEditorState(newValue,new ArrayList<>(displayedEntities), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
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
        return new DataEditorState(null,new ArrayList<>(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState setShowNavigation(boolean newShowNavigation) {
        return new DataEditorState(null,new ArrayList<>(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,newShowNavigation);
    }

    public void destroy() {
        if (editorStateVisualisation!=null){
            editorStateVisualisation.destroy();
        }
    }

}