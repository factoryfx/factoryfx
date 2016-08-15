package de.factoryfx.adminui.javafx;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.adminui.javafx.framework.view.LoadView;
import de.factoryfx.adminui.javafx.framework.view.SaveView;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage<T extends FactoryBase<? extends LiveObject, T>> {

    private final LoadView<?> loadView;
    private final FactoryTreeEditor<T> factoryTreeEditor;
    private final SaveView<T> saveView;

    public MainStage( FactoryTreeEditor<T> factoryTreeEditor, LoadView<T> loadView, SaveView<T> saveView) {
        this.loadView = loadView;
        this.factoryTreeEditor = factoryTreeEditor;
        this.saveView = saveView;
    }

    public void show(){
        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        stage.setScene(new Scene(borderPane,1000,800));
        stage.getScene().getStylesheets().add(getClass().getResource("/de/factoryfx/richclient/css/app.css").toExternalForm());

        TabPane tabPane = new TabPane();
        borderPane.setCenter(tabPane);


        MenuBar menuBar = new MenuBar();
        {
            Menu menu = new Menu("File");
            menuBar.getMenus().add(menu);

            {
                MenuItem menuItem = new MenuItem();
                menuItem.setText("Load");
                menu.getItems().add(menuItem);
                menuItem.setOnAction(event -> {
                    Tab tab = new Tab("Load");
                    tabPane.getTabs().add(tab);
                    tab.setContent(loadView.createContent());
                });
            }

            {
                MenuItem menuItem = new MenuItem();
                menuItem.setText("Save");
                menu.getItems().add(menuItem);
                menuItem.setOnAction(event -> {
                    Tab tab = new Tab("Save");
                    tabPane.getTabs().add(tab);
                    tab.setContent(saveView.createContent());
                });
            }
        }

        {
            Menu menu = new Menu("Data");
            menuBar.getMenus().add(menu);
            MenuItem menuItem = new MenuItem();
            menuItem.setText("Data Editor");
            menu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                Tab tab = new Tab("Tree Editor");
                tabPane.getTabs().add(tab);
                tab.setContent(factoryTreeEditor.createContent());
            });
        }

        borderPane.setTop(menuBar);

        Menu menu = new Menu("Runtime views");
        menuBar.getMenus().add(menu);
//
//        guiModel.runtimeQueryViews.forEach(view -> {
//            RuntimeView runtimeView = new RuntimeView(view);
//            MenuItem menuItem = new MenuItem(view.name);
//            menuItem.setOnAction(event -> {
//                Tab tab = new Tab(view.name);
//                tabPane.getTabs().add(tab);
//                tab.setContent(runtimeView.createContent());
//            });
//            menu.getItems().add(menuItem);
//        });


        stage.show();
    }
}
