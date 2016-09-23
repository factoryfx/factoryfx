package de.factoryfx.javafx.editor.attribute;

import java.util.ArrayList;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.util.StringAttribute;
import org.junit.Assert;
import org.junit.Test;

public class AttributeEditorTest {



    @Test
    public void test_edit(){

        StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());
        stringAttribute.set("Hallo");

        ArrayList<String> calls = new ArrayList<>();
        AttributeEditor<String> attributeEditor = new AttributeEditor<>(boundTo -> {
            boundTo.addListener((observable, oldValue, newValue) -> {
                calls.add(boundTo.get());
            });
            return null;
        });
        attributeEditor.createContent();


        attributeEditor.bind(stringAttribute);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("Hallo",calls.get(0));
        stringAttribute.set("Welt");
        Assert.assertEquals(2,calls.size());
        Assert.assertEquals("Welt",calls.get(1));

    }

    @Test
    public void test_change_value(){

        StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());
        stringAttribute.set("Hallo");

        AttributeEditor<String> attributeEditor = new AttributeEditor<>(boundTo -> {
            boundTo.set("Welt");
            return null;
        });
        attributeEditor.bind(stringAttribute);
        attributeEditor.createContent();

        Assert.assertEquals("Welt",stringAttribute.get());
    }
}