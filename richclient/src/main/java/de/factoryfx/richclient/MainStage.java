package de.factoryfx.richclient;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.guimodel.GenericTreeFactoryView;
import de.factoryfx.guimodel.ViewManager;
import de.factoryfx.richclient.framework.view.LoadView;
import de.factoryfx.richclient.framework.view.SaveView;
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
    private final LoadView<?> loadView;
    private final GenericTreeFactoryViewRichClient<T> genericTreeFactoryViewRichClient;
    private final SaveView<T> saveView;

    public MainStage(ViewManager<T> viewManager, GenericTreeFactoryViewRichClient<T> genericTreeFactoryViewRichClient, LoadView<T> loadView, SaveView<T> saveView) {
        this.viewManager = viewManager;
        this.loadView = loadView;
        this.genericTreeFactoryViewRichClient = genericTreeFactoryViewRichClient;
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
