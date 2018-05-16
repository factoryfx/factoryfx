package de.factoryfx.javafx.css;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class CssUtil {
    public static void addToNode(Parent node ){
        node.getStylesheets().add(CssUtil.class.getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
    }

    public static void addToScene(Scene scene ){
        scene.getStylesheets().add(CssUtil.class.getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
    }

    public static String getURL(){
//        Optional<Module> specificModule = ModuleLayer.boot().findModule("de.factoryfx.javafxDataEditing");
//        try {
//            specificModule.get().getResourceAsStream("de/factoryfx/javafx/css/app.css");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return "";


        return CssUtil.class.getResource("/de/factoryfx/javafx/css/app.css").toExternalForm();

    }
}
