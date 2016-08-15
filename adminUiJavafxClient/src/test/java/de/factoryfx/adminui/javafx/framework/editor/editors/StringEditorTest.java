package de.factoryfx.adminui.javafx.framework.editor.editors;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class StringEditorTest {

    @Test
    public void test_canEdit(){
        StringEditor stringEditor = new StringEditor();
        Assert.assertTrue(stringEditor.canEdit("fddfgfdg"));
        Assert.assertFalse(stringEditor.canEdit(new Date()));
    }
}