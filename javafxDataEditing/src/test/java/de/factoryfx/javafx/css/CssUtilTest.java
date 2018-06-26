package de.factoryfx.javafx.css;

import org.junit.Assert;
import org.junit.Test;

public class CssUtilTest {

    @Test
    public void test_getURL(){
        System.out.println(CssUtil.getURL());
        Assert.assertNotNull(CssUtil.getURL());
    }

//    @Test //doesn't work headless, not mockito mockable
//    public void test_add(){
//        CssUtil.addToNode(new Pane());
//    }
}