package io.github.factoryfx.javafx.data.editor.attribute;

public class AttributeEditorTest {



//    @Test
//    @Ignore // IllegalStateException: Toolkit not initialized
//    public void test_edit(){
//
//        StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());
//        stringAttribute.set("Hallo");
//
//        ArrayList<String> calls = new ArrayList<>();
//        AttributeEditor<String> attributeEditor = new AttributeEditor<>(stringAttribute,(boundTo) -> {
//            ChangeListener<String> stringChangeListener = (observable, oldValue, newValue) -> {
//                calls.add(boundTo.get());
//            };
//            boundTo.addListener(stringChangeListener);
//            stringChangeListener.changed(boundTo,boundTo.get(),boundTo.get());
//            return null;
//        });
//        attributeEditor.createVisualisation();
//
//        Assertions.assertEquals(1,calls.size());
//        Assertions.assertEquals("Hallo",calls.get(0));
//        stringAttribute.set("Welt");
//        Assertions.assertEquals(2,calls.size());
//        Assertions.assertEquals("Welt",calls.get(1));
//
//    }
//
//    @Test
//    @Ignore // IllegalStateException: Toolkit not initialized
//    public void test_change_value(){
//
//        StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());
//        stringAttribute.set("Hallo");
//
//        AttributeEditor<String> attributeEditor = new AttributeEditor<>(stringAttribute,(boundTo) -> {
//            boundTo.set("Welt");
//            return null;
//        });
//        attributeEditor.createVisualisation();
//
//        Assertions.assertEquals("Welt",stringAttribute.get());
//    }
}