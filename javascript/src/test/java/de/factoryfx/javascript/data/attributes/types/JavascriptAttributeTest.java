package de.factoryfx.javascript.data.attributes.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.factoryfx.data.Data;
import org.junit.Assert;
import org.junit.Test;

public class JavascriptAttributeTest {

    @Test
    public void test_get_update_header() throws InterruptedException {
        Data data = new Data();
        JavascriptAttribute<Object> javascriptAttributeTest = new JavascriptAttribute<>(() -> Collections.singletonList(data),Object.class);
        Assert.assertEquals("var data = {};",javascriptAttributeTest.get().getHeaderCode().trim());
    }

    @Test
    public void test_listeners() throws InterruptedException {
        Data data = new Data();
        JavascriptAttribute<Object> javascriptAttributeTest = new JavascriptAttribute<>(() -> Collections.singletonList(data),Object.class);
        javascriptAttributeTest.setRunlaterExecutorForTest(runnable -> runnable.run());
        List<String> calls = new ArrayList<>();
        javascriptAttributeTest.internal_addListener((attribute, value) -> {
            calls.add(value.getCode());
        });
        javascriptAttributeTest.set(new Javascript<>("XXX"));
        Thread.sleep(2100);

        Assert.assertEquals(1,calls.size());
        Assert.assertEquals("XXX",calls.get(0));
    }

    @Test
    public void test_listeners_multiple() throws InterruptedException {
        Data data = new Data();
        JavascriptAttribute<Object> javascriptAttributeTest = new JavascriptAttribute<>(() -> Collections.singletonList(data),Object.class);
        javascriptAttributeTest.setRunlaterExecutorForTest(runnable -> runnable.run());
        List<String> calls = new ArrayList<>();
        javascriptAttributeTest.internal_addListener((attribute, value) -> {
            calls.add(value.getCode());
        });
        javascriptAttributeTest.set(new Javascript<>("XXX1"));
        javascriptAttributeTest.set(new Javascript<>("XXX2"));
        Assert.assertEquals("XXX2",calls.get(calls.size()-1));
    }
}