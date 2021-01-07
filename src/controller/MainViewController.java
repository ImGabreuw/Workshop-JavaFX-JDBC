package controller;

import application.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import util.Alerts;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    // BOA PRÁTICA: nome do componente = componente + nome do componente
    @FXML
    private MenuItem menuItemSeller;
    @FXML
    private MenuItem menuItemDepartment;
    @FXML
    private MenuItem menuItemAbout;

    /*
     BOA PRÁTICA: métodos = on + componente + nome da ação

     OBS: ação padrão = action
     */
    @FXML
    public void onMenuItemSellerAction() {
        System.out.println("onMenuItemSellerAction");
    }

    @FXML
    public void onMenuItemDepartmentAction() {
        this.loadView2("../view/DepartmentList.fxml");
    }

    @FXML
    public void onMenuItemAboutAction() {
        this.loadView("../view/AboutView.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // 'synchronized' = operação não será interrompida no multi-threading
    private synchronized void loadView(String absolutePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));

            VBox newVBox = loader.load();
            Scene mainScene = MainApplication.getMainScene();
            VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            Node mainNode = mainVbox.getChildren().get(0);

            mainVbox.getChildren().clear();
            mainVbox.getChildren().add(mainNode);
            mainVbox.getChildren().addAll(newVBox.getChildren());
        } catch (IOException e) {
            Alerts.showAlert(
                    "IO Exception",
                    "Error loading view",
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private synchronized void loadView2(String absolutePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));

            VBox newVBox = loader.load();
            Scene mainScene = MainApplication.getMainScene();
            VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            Node mainNode = mainVbox.getChildren().get(0);

            mainVbox.getChildren().clear();
            mainVbox.getChildren().add(mainNode);
            mainVbox.getChildren().addAll(newVBox.getChildren());

            DepartmentListController controller = loader.getController();

            controller.setService(new DepartmentService());
            controller.updateTableView();
        } catch (IOException e) {
            Alerts.showAlert(
                    "IO Exception",
                    "Error loading view",
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }
}
