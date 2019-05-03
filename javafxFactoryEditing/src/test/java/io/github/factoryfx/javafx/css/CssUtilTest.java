package io.github.factoryfx.javafx.css;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CssUtilTest {

    @Test
    public void test_getURL(){
        System.out.println(CssUtil.getURL());
        Assertions.assertNotNull(CssUtil.getURL());
    }

//    @Test //doesn't work headless, not mockito mockable
//    public void test_add(){
//        CssUtil.addToNode(new Pane());
//    }
}