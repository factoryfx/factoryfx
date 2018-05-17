package de.factoryfx.javafx.css;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

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