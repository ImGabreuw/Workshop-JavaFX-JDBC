package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static Scene mainScene;

    public static Scene getMainScene() {
        return mainScene;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/MainView.fxml"));

            ScrollPane scrollPane = loader.load();
            mainScene = new Scene(scrollPane);

            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Sample JavaFX application");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
