package io.github.factoryfx.javafx.factory.css;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class CssUtil {
    public static void addToNode(Parent node ){
        node.getStylesheets().add(CssUtil.class.getResource("/io/github/factoryfx/javafx/css/app.css").toExternalForm());
    }

    public static void addToScene(Scene scene ){
        scene.getStylesheets().add(CssUtil.class.getResource("/io/github/factoryfx/javafx/css/app.css").toExternalForm());
    }

    public static String getURL(){
        //npe here is probably not a real error, try reimport intellij
        //https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000430279-Can-t-access-resource-with-Java-10?page=1#community_comment_360000102619
        return CssUtil.class.getResource("/io/github/factoryfx/javafx/css/app.css").toExternalForm();
    }
}
