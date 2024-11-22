package io.github.factoryfx.javafx.editor;

import java.util.ArrayDeque;
import java.util.List;
import java.util.function.BiFunction;

import javafx.scene.Node;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;

//immutable editor state
public class DataEditorState {
    public static final int HISTORY_LIMIT = 20;
    final ArrayDeque<FactoryBase<?, ?>> displayedEntities;
    final ArrayDeque<FactoryBase<?, ?>> nextEntities;
    private final FactoryBase<?, ?> currentData;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;
    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final BiFunction<Node, FactoryBase<?, ?>, Node> visCustomizer;
    private final boolean showNavigation;
    private final boolean showUsages;

    public DataEditorState(FactoryBase<?, ?> currentData, ArrayDeque<FactoryBase<?, ?>> displayedEntities, ArrayDeque<FactoryBase<?, ?>> nextEntities, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node, FactoryBase<?, ?>, Node> visCustomizer, boolean showNavigation, boolean showUsages) {
        this.displayedEntities = displayedEntities;
        this.nextEntities = nextEntities;
        this.currentData = currentData;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.visCustomizer = visCustomizer;
        this.showNavigation = showNavigation;
        this.showUsages = showUsages;
    }

    private DataEditorStateVisualisation editorStateVisualisation;

    public Node createVisualisation() {
        editorStateVisualisation = new DataEditorStateVisualisation(currentData,
                                                                    displayedEntities,
                                                                    displayedEntities.isEmpty(),
                                                                    nextEntities.isEmpty(),
                                                                    attributeVisualisationMappingBuilder,
                                                                    uniformDesign,
                                                                    dataEditor,
                                                                    visCustomizer,
                                                                    showNavigation,
                                                                    showUsages);
        return editorStateVisualisation;
    }

    public DataEditorState resetHistory() {
        return new DataEditorState(currentData, new ArrayDeque<>(), new ArrayDeque<>(), attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public DataEditorState withHistory(List<FactoryBase<?, ?>> data) {
        return new DataEditorState(currentData, new ArrayDeque<>(data), new ArrayDeque<>(), attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public DataEditorState back() {
        FactoryBase<?, ?> newCurrent = currentData;
        if (!displayedEntities.isEmpty()) {
            newCurrent = displayedEntities.poll();
            nextEntities.push(currentData);
        }
        return new DataEditorState(newCurrent, displayedEntities, nextEntities, attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public DataEditorState next() {
        FactoryBase<?, ?> newData = currentData;
        if (!nextEntities.isEmpty()) {
            newData = nextEntities.poll();
            displayedEntities.push(currentData);
        }
        return new DataEditorState(newData, displayedEntities, nextEntities, attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public DataEditorState navigateBack(FactoryBase<?, ?> newValue) {
        while (true) {
            FactoryBase<?, ?> poll = displayedEntities.poll();
            if (poll == newValue || poll == null) {
                break;
            }
            nextEntities.push(poll);
        }
        return new DataEditorState(newValue, displayedEntities, nextEntities, attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public DataEditorState edit(FactoryBase<?, ?> newValue) {
        if (this.currentData != null) {
            displayedEntities.push(this.currentData);
        }
        if (displayedEntities.size() > HISTORY_LIMIT) {
            displayedEntities.pollLast();
        }

        return new DataEditorState(newValue, displayedEntities, nextEntities, attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public FactoryBase<?, ?> getCurrentData() {
        return currentData;
    }

    public DataEditorState reset() {
        return new DataEditorState(null, new ArrayDeque<>(), new ArrayDeque<>(), attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, showUsages);
    }

    public DataEditorState setShowNavigation(boolean newShowNavigation) {
        return new DataEditorState(currentData, displayedEntities, nextEntities, attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, newShowNavigation, showUsages);
    }

    public DataEditorState setShowUsages(boolean newShowUsages) {
        return new DataEditorState(currentData, displayedEntities, nextEntities, attributeVisualisationMappingBuilder, uniformDesign, dataEditor, visCustomizer, showNavigation, newShowUsages);
    }

    public void destroy() {
        if (editorStateVisualisation != null) {
            editorStateVisualisation.destroy();
        }
    }

}
