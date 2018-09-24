package de.factoryfx.javafx.data.editor.data;

import java.util.ArrayList;

import de.factoryfx.javafx.UniformDesignBuilder;
import de.factoryfx.javafx.data.editor.attribute.AttributeVisualisationMappingBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import org.junit.Assert;
import org.junit.Test;

public class DataEditorTest {

    @Test
    public void testBack() throws Exception {
        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()),uniformDesign);


        ExampleData1 exampleData1 = new ExampleData1();
        ExampleData1 exampleData2 = new ExampleData1();
        ExampleData1 exampleData3 = new ExampleData1();
        ExampleData1 exampleData4 = new ExampleData1();


        dataEditor.navigate(exampleData1);
        dataEditor.navigate(exampleData2);
        dataEditor.navigate(exampleData3);
        dataEditor.navigate(exampleData4);

        dataEditor.back();
        Assert.assertEquals(exampleData3,dataEditor.editData.get());
        dataEditor.back();
        Assert.assertEquals(exampleData2,dataEditor.editData.get());
        dataEditor.back();
        Assert.assertEquals(exampleData1,dataEditor.editData.get());

        dataEditor.back();
        Assert.assertEquals(exampleData1,dataEditor.editData.get());
        dataEditor.back();
        Assert.assertEquals(exampleData1,dataEditor.editData.get());
    }

    @Test
    public void testBack_limit() throws Exception {
        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()),uniformDesign);

        for (int i=0;i<100;i++){
            dataEditor.navigate(new ExampleData1());
        }

        Assert.assertEquals(DataEditorState.HISTORY_LIMIT,dataEditor.dataEditorState.displayedEntities.size());
    }


    @Test
    public void testForward() throws Exception {
        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()),uniformDesign);


        ExampleData1 exampleData1 = new ExampleData1();
        ExampleData1 exampleData2 = new ExampleData1();
        ExampleData1 exampleData3 = new ExampleData1();
        ExampleData1 exampleData4 = new ExampleData1();


        dataEditor.navigate(exampleData1);
        dataEditor.navigate(exampleData2);
        dataEditor.navigate(exampleData3);
        dataEditor.navigate(exampleData4);



        dataEditor.back();
        Assert.assertEquals(exampleData3, dataEditor.editData.get());
        dataEditor.back();
        Assert.assertEquals(exampleData2, dataEditor.editData.get());
        dataEditor.next();
        Assert.assertEquals(exampleData3,dataEditor.editData.get());
        dataEditor.next();
        Assert.assertEquals(exampleData4,dataEditor.editData.get());

        Assert.assertEquals(4,dataEditor.dataEditorState.displayedEntities.size());

        dataEditor.next();
        Assert.assertEquals(exampleData4,dataEditor.editData.get());
    }

    @Test
    public void test_navigated_hierarchy() throws Exception {
        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()),uniformDesign);

        ExampleData1 root = new ExampleData1();
        ExampleData2 value = new ExampleData2();
        root.referenceAttribute.set(value);

        dataEditor.navigate(root);
        dataEditor.navigate(value);

        Assert.assertEquals(2,dataEditor.dataEditorState.displayedEntities.size());
        Assert.assertEquals(root,dataEditor.dataEditorState.displayedEntities.get(0));
        Assert.assertEquals(value,dataEditor.dataEditorState.displayedEntities.get(1));

        dataEditor.navigate(root);
        Assert.assertEquals(2,dataEditor.dataEditorState.displayedEntities.size());
        Assert.assertEquals(root,dataEditor.dataEditorState.displayedEntities.get(0));

    }

    @Test
    public void test_navigated_hierarchy2() throws Exception {
        UniformDesign uniformDesign = UniformDesignBuilder.build();
        DataEditor dataEditor = new DataEditor(new AttributeVisualisationMappingBuilder(new ArrayList<>()),uniformDesign);

        ExampleData1 root = new ExampleData1();
        ExampleData2 value1 = new ExampleData2();
        root.referenceAttribute.set(value1);
        ExampleData2 value2 = new ExampleData2();
        root.referenceListAttribute.add(value2);

        dataEditor.navigate(root);
        dataEditor.navigate(value1);
        dataEditor.back();
        Assert.assertEquals(2,dataEditor.dataEditorState.displayedEntities.size());
        Assert.assertEquals(root,dataEditor.dataEditorState.displayedEntities.get(0));

        dataEditor.navigate(value2);
        Assert.assertEquals(2,dataEditor.dataEditorState.displayedEntities.size());
        Assert.assertEquals(root,dataEditor.dataEditorState.displayedEntities.get(0));
        Assert.assertEquals(value2,dataEditor.dataEditorState.displayedEntities.get(1));

    }
}
