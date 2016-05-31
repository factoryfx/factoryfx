package de.factoryfx.richclient;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.guimodel.GenericTreeFactoryView;
import de.factoryfx.guimodel.ViewManager;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage<T extends FactoryBase<? extends LiveObject, T>> {

    private final ViewManager viewManager;
    private LoadView<?> loadView;
    private GenericTreeFactoryViewRichClient<T> genericTreeFactoryViewRichClient;

    public MainStage(ViewManager<T> viewManager,GenericTreeFactoryViewRichClient<T> genericTreeFactoryViewRichClient, LoadView<T> loadView) {
        this.viewManager = viewManager;
        this.loadView = loadView;
        this.genericTreeFactoryViewRichClient = genericTreeFactoryViewRichClient;
    }

    public void show(){
        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        stage.setScene(new Scene(borderPane,1000,800));

        TabPane tabPane = new TabPane();
        borderPane.setCenter(tabPane);


        MenuBar menuBar = new MenuBar();
        {
            Menu menu = new Menu("File");
            menuBar.getMenus().add(menu);
            MenuItem menuItem = new MenuItem();
            menuItem.setText("Load");
            menu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                Tab tab = new Tab("Tree Editor");
                tabPane.getTabs().add(tab);
                tab.setContent(loadView.createContent());
            });
        }

        {
            Menu menu = new Menu("Data");
            menuBar.getMenus().add(menu);
            MenuItem menuItem = new MenuItem();
            menuItem.setText("Tree Editor");
            menu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                Tab tab = new Tab("Tree Editor");
                tabPane.getTabs().add(tab);
                tab.setContent(genericTreeFactoryViewRichClient.createContent());
            });
        }

        borderPane.setTop(menuBar);
        viewManager.getViews().forEach(view -> {
            if (view instanceof GenericTreeFactoryView<?>){

            }
        });


        stage.show();
    }
}
