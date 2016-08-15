package de.factoryfx.adminui.javafx.framework.editor;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.factory.attribute.Attribute;
import de.factoryfx.adminui.javafx.framework.widget.Widget;
import de.factoryfx.adminui.javafx.framework.editor.editors.IntegerEditor;
import de.factoryfx.adminui.javafx.framework.editor.editors.StringEditor;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MasterEditor implements Widget{

    private final List<AttributeEditor<?,?>> attributeEditors= new ArrayList<>();
    private BorderPane borderPane=new BorderPane();

    public MasterEditor(){
        attributeEditors.add(new StringEditor());
        attributeEditors.add(new IntegerEditor());
        //TODO
    }

    public void bind(Attribute<?,?> attribute){
        for (AttributeEditor<?,?> attributeEditor: attributeEditors){
            if (attributeEditor.canEdit(attribute.get())){
                borderPane.setCenter(attributeEditor.createContent());
                attributeEditor.bind(attribute);
            }
        }
    }

    public void unbind(Attribute<?,?> attribute){

    }

    @Override
    public Node createContent() {
        return borderPane;
    }
}
