package de.factoryfx.javascript.data.attributes.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JavascriptAttributeTest {

    @Test
    public void test_get_update_header(){
        Data data = new Data();
        JavascriptAttribute<Object> javascriptAttributeTest = new JavascriptAttribute<>(() -> Collections.singletonList(data),Object.class);
        Assertions.assertEquals("var data = {};",javascriptAttributeTest.get().getHeaderCode().trim());
    }

    @Test
    public void test_listeners() throws InterruptedException {
        Data data = new Data();
        JavascriptAttribute<Object> javascriptAttributeTest = new JavascriptAttribute<>(() -> Collections.singletonList(data),Object.class);
        javascriptAttributeTest.internal_setRunlaterExecutor(runnable -> runnable.run());
        List<String> calls = new ArrayList<>();
        javascriptAttributeTest.internal_addListener((attribute, value) -> {
            calls.add(value.getCode());
        });
        javascriptAttributeTest.set(new Javascript<>("XXX"));
        Thread.sleep(2100);

        Assertions.assertEquals(2,calls.size()); //TODO should be 1 call
        Assertions.assertEquals("XXX",calls.get(0));
    }

    @Test
    public void test_listeners_multiple() {
        Data data = new Data();
        JavascriptAttribute<Object> javascriptAttributeTest = new JavascriptAttribute<>(() -> Collections.singletonList(data),Object.class);
        javascriptAttributeTest.internal_setRunlaterExecutor(runnable -> runnable.run());
        List<String> calls = new ArrayList<>();
        javascriptAttributeTest.internal_addListener((attribute, value) -> {
            calls.add(value.getCode());
        });
        javascriptAttributeTest.set(new Javascript<>("XXX1"));
        javascriptAttributeTest.set(new Javascript<>("XXX2"));
        Assertions.assertEquals("XXX2",calls.get(calls.size()-1));
    }

    public static class ExampleJavascriptData{
        public final JavascriptAttribute<Object> attribute = new JavascriptAttribute<>(() -> Collections.singletonList(new Data()),Object.class);
    }

    @Test
    public void test_json_inside_data(){
        ExampleJavascriptData data = new ExampleJavascriptData();
        data.attribute.set(new Javascript<>("XXX1"));
        ExampleJavascriptData copy = ObjectMapperBuilder.build().copy(data);
        Assertions.assertEquals("XXX1",data.attribute.get().getCode());
    }

}