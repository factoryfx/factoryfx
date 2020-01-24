package io.github.factoryfx.javafx.editor;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import javafx.scene.Node;

import java.util.*;
import java.util.function.BiFunction;

//immutable editor state
public class DataEditorState {
    public static final int HISTORY_LIMIT = 20;
    final ArrayDeque<FactoryBase<?,?>> displayedEntities;
    final ArrayDeque<FactoryBase<?,?>> nextEntities;
    private final FactoryBase<?,?> currentData;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final BiFunction<Node,FactoryBase<?,?>,Node> visCustomizer;
    private final boolean showNavigation;

    public DataEditorState(FactoryBase<?,?> currentData, ArrayDeque<FactoryBase<?,?>> displayedEntities, ArrayDeque<FactoryBase<?,?>> nextEntities, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node,FactoryBase<?,?>,Node> visCustomizer, boolean showNavigation) {
        this.displayedEntities = displayedEntities;
        this.nextEntities = nextEntities;
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
        return new DataEditorState(currentData,new ArrayDeque<>(),new ArrayDeque<>(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState withHistory(List<FactoryBase<?,?>> data) {
        return new DataEditorState(currentData,new ArrayDeque<>(data),new ArrayDeque<>(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    private Optional<FactoryBase<?,?>> previousData(){
        return Optional.ofNullable(displayedEntities.peek());
    }

    private Optional<FactoryBase<?,?>> nextData(){
        if (!nextEntities.isEmpty()){
            return Optional.ofNullable(nextEntities.peek());
        }
        return Optional.empty();
    }


    public DataEditorState back(){
        FactoryBase<?, ?> newCurrent = currentData;
        if (!displayedEntities.isEmpty()){
            newCurrent = displayedEntities.poll();
            nextEntities.push(currentData);
        }
        return new DataEditorState(newCurrent,displayedEntities,nextEntities, attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState next(){
        FactoryBase<?,?> newData=currentData;
        if(!nextEntities.isEmpty()){
            newData=nextEntities.poll();
            displayedEntities.push(currentData);
        }
        return new DataEditorState(newData,displayedEntities,nextEntities, attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState navigateBack(FactoryBase<?,?> newValue){
        while (true){
            FactoryBase<?, ?> poll = displayedEntities.poll();
            if (poll ==newValue || poll==null) {
                break;
            }
            nextEntities.push(poll);
        }
        return new DataEditorState(newValue,displayedEntities,nextEntities, attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }


    public DataEditorState edit(FactoryBase<?,?> newValue) {
        if (this.currentData!=null){
            displayedEntities.push(this.currentData);
        }
        if (displayedEntities.size()>HISTORY_LIMIT){
            displayedEntities.pollLast();
        }

        return new DataEditorState(newValue,displayedEntities,nextEntities, attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public FactoryBase<?,?> getCurrentData() {
        return currentData;
    }


    public DataEditorState reset() {
        return new DataEditorState(null,new ArrayDeque<>(),new ArrayDeque<>(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,showNavigation);
    }

    public DataEditorState setShowNavigation(boolean newShowNavigation) {
        return new DataEditorState(null,new ArrayDeque<>(),new ArrayDeque<>(), attributeVisualisationMappingBuilder,uniformDesign,dataEditor, visCustomizer,newShowNavigation);
    }

    public void destroy() {
        if (editorStateVisualisation!=null){
            editorStateVisualisation.destroy();
        }
    }

}
