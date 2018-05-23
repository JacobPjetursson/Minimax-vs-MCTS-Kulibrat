package ai.Minimax.Experimental;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ListViewSelection extends Application {

    private ListView<String> m_listView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();

        // a horizontal panel to hold the list view and the label.
        HBox listViewPanel = new HBox();
        listViewPanel.setSpacing(10);

        // the text to be displayed when clicking on a new item in the list.
        final Text label = new Text("Nothing Selected.");
        label.setFont(Font.font(null, FontWeight.BOLD, 16));

        // create a list of items.
        m_listView = new ListView<String>(FXCollections.observableArrayList(
                "Item 1", "Item 2", "Item 3", "Item 4"));
        m_listView.prefWidth(100);
        m_listView.setMaxWidth(100);
        m_listView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {

                    public void changed(
                            ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        // change the label text value to the newly selected
                        // item.
                        label.setText("You Selected " + newValue);
                    }
                });

        listViewPanel.getChildren().addAll(m_listView, label);
        root.getChildren().addAll(listViewPanel);

        Scene scene = new Scene(root);
        primaryStage.setTitle("List View Selector");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}