package de.factoryfx.javafx.data.editor.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeGroup;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import de.factoryfx.javafx.data.util.DataObservableDisplayText;
import de.factoryfx.javafx.data.util.UniformDesign;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DataEditorStateVisualisation extends BorderPane {
    private final UniformDesign uniformDesign;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;
    private final DataEditor dataEditor;
    private final BiFunction<Node,Data,Node> visCustomizer;

    public DataEditorStateVisualisation(Data currentData, List<Data> displayedEntities, Optional<Data> previousData, Optional<Data> nextData, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign, DataEditor dataEditor, BiFunction<Node,Data,Node> visCustomizer, boolean showNavigation){
        this.uniformDesign=uniformDesign;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
        this.dataEditor = dataEditor;
        this.visCustomizer = visCustomizer;

        Data previousValue=null;
        if (displayedEntities.size()>1){
            previousValue=displayedEntities.get(displayedEntities.size()-1);
        }
        setCenter(createEditor(currentData,previousValue));

        if (showNavigation){
            setTop(createNavigation(displayedEntities,currentData,previousData,nextData));
        }
    }

    private Node customizeVis(Node defaultVis, Data data){
        return visCustomizer.apply(defaultVis,data);
    }

    @SuppressWarnings("unchecked")
    private Node createEditor(Data newValue, Data previousValue){
        if (newValue==null) {
            return new Label("empty");
        } else {

            if (newValue.internal().attributeListGrouped().size()==1){
                final Node attributeGroupVisual = createAttributeGroupVisual(newValue.internal().attributeListGrouped().get(0).group,previousValue, () -> newValue.internal().validateFlat());
                return customizeVis(attributeGroupVisual,newValue);
            } else {
                TabPane tabPane = new TabPane();
                for (AttributeGroup attributeGroup: newValue.internal().attributeListGrouped()) {

                    Tab tab=new Tab(uniformDesign.getText(attributeGroup.title));
                    tab.setClosable(false);
                    tab.setContent(createAttributeGroupVisual(attributeGroup.group,previousValue, () -> newValue.internal().validateFlat()));
                    tabPane.getTabs().add(tab);
                }
                return customizeVis(tabPane,newValue);
            }

        }
    }

    private Node createNavigation(List<Data> displayedEntities, Data currentData, Optional<Data> previousData, Optional<Data> nextData){
        BreadCrumbBarWidthFixed<Data> breadCrumbBar = new BreadCrumbBarWidthFixed<>();


        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane();
        scrollPaneBreadCrumbBar.setContent(breadCrumbBar);
        scrollPaneBreadCrumbBar.setHvalue(1.0);
        scrollPaneBreadCrumbBar.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.getStyleClass().add("transparent-scroll-pane");

        //force hvalue to 1 cause its buggy
        scrollPaneBreadCrumbBar.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null && newValue.doubleValue()<1.0){
                scrollPaneBreadCrumbBar.setHvalue(1);
            }
        });

        breadCrumbBar.setCrumbFactory(param -> {
            BreadCrumbBarSkin.BreadCrumbButton breadCrumbButton = new BreadCrumbBarSkin.BreadCrumbButton("");
            if (param.getValue()!=null){
                breadCrumbButton.textProperty().bind(new DataObservableDisplayText(param.getValue()).get());
            }
            if (currentData==param.getValue()){
                breadCrumbButton.setStyle("-fx-font-weight: bold;");
            }
            return breadCrumbButton;
        });
        breadCrumbBar.setOnCrumbAction(event -> {
            dataEditor.navigate(event.getSelectedCrumb().getValue());
        });


//        List<Data> newhistory = new ArrayList<>();
//        for (Data data: displayedEntities){
//            newhistory.add(data);
//            if (data==currentData){
//                break;
//            }
//        }

        breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(displayedEntities.toArray(new Data[0])));
        breadCrumbBar.layout();



        HBox navigation = new HBox(3);
        navigation.getStyleClass().add("navigationhbox");
        navigation.setAlignment(Pos.TOP_LEFT);
        Button back = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_LEFT));
        back.prefHeightProperty().bind(breadCrumbBar.heightProperty().add(1));
        back.setOnAction(event -> dataEditor.back());
        back.setDisable(!previousData.isPresent());
        Button next = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_RIGHT));
        next.prefHeightProperty().bind(breadCrumbBar.heightProperty().add(1));
        next.setDisable(!nextData.isPresent());
        next.setOnAction(event -> dataEditor.next());

        navigation.getChildren().add(back);
        navigation.getChildren().add(next);


        navigation.getChildren().add(scrollPaneBreadCrumbBar);

        navigation.setPadding(new Insets(3,3,0,3));//workaround scrollpane fittoheight and  .setAlignment(Pos.TOP_LEFT); don't work in this cas

        HBox.setHgrow(scrollPaneBreadCrumbBar, Priority.ALWAYS);
        return navigation;
    }

    List<AttributeGroupEditor> createdAttributeGroupEditor=new ArrayList<>();//prevent gc for weak listeners
    private Node createAttributeGroupVisual(List<Attribute<?,?>> attributeGroup, Data oldValue, Supplier<List<ValidationError>> additionalValidation) {
        AttributeGroupEditor attributeGroupEditor = new AttributeGroupEditor(attributeGroup, oldValue, attributeVisualisationMappingBuilder, dataEditor, uniformDesign, additionalValidation);
        createdAttributeGroupEditor.add(attributeGroupEditor);
        return attributeGroupEditor.createContent();
    }

    public void destroy() {
        for (AttributeGroupEditor attributeGroupEditor : createdAttributeGroupEditor) {
            attributeGroupEditor.destroy();
        }
    }
}
