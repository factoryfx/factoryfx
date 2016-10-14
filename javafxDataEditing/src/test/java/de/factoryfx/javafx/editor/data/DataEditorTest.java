package de.factoryfx.javafx.editor.data;

import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import org.junit.Assert;
import org.junit.Test;

public class DataEditorTest {

    @Test
    public void testBack() throws Exception {
        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign,new ExampleData1()),uniformDesign);


        ExampleData1 exampleData1 = new ExampleData1();
        ExampleData1 exampleData2 = new ExampleData1();
        ExampleData1 exampleData3 = new ExampleData1();
        ExampleData1 exampleData4 = new ExampleData1();


        dataEditor.edit(exampleData1);
        dataEditor.edit(exampleData2);
        dataEditor.edit(exampleData3);
        dataEditor.edit(exampleData4);

        dataEditor.back();
        Assert.assertEquals(exampleData3,dataEditor.bound.get());
        dataEditor.back();
        Assert.assertEquals(exampleData2,dataEditor.bound.get());
        dataEditor.back();
        Assert.assertEquals(exampleData1,dataEditor.bound.get());

        dataEditor.back();
        Assert.assertEquals(exampleData1,dataEditor.bound.get());
        dataEditor.back();
        Assert.assertEquals(exampleData1,dataEditor.bound.get());
    }

    @Test
    public void testBack_limit() throws Exception {
        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign,new ExampleData1()),uniformDesign);

        for (int i=0;i<100;i++){
            dataEditor.edit(new ExampleData1());
        }

        Assert.assertEquals(dataEditor.HISTORY_LIMIT,dataEditor.displayedEntities.size());
    }


    @Test
    public void testForward() throws Exception {
        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign,new ExampleData1()),uniformDesign);


        ExampleData1 exampleData1 = new ExampleData1();
        ExampleData1 exampleData2 = new ExampleData1();
        ExampleData1 exampleData3 = new ExampleData1();
        ExampleData1 exampleData4 = new ExampleData1();


        dataEditor.edit(exampleData1);
        dataEditor.edit(exampleData2);
        dataEditor.edit(exampleData3);
        dataEditor.edit(exampleData4);



        dataEditor.back();
        Assert.assertEquals(exampleData3, dataEditor.bound.get());
        dataEditor.back();
        Assert.assertEquals(exampleData2, dataEditor.bound.get());
        dataEditor.next();
        Assert.assertEquals(exampleData3,dataEditor.bound.get());
        dataEditor.next();
        Assert.assertEquals(exampleData4,dataEditor.bound.get());

        Assert.assertEquals(4,dataEditor.displayedEntities.size());

        dataEditor.next();
        Assert.assertEquals(exampleData4,dataEditor.bound.get());
    }
}
