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
import model.services.SellerService;
import util.Alerts;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
        this.loadView("../view/SellerListView.fxml", (SellerListController controller) -> {
            controller.setService(new SellerService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemDepartmentAction() {
        this.loadView("../view/DepartmentListView.fxml", (DepartmentListController controller) -> {
            controller.setService(new DepartmentService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemAboutAction() {
        this.loadView("../view/AboutView.fxml", controller -> {});
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // 'synchronized' = operação não será interrompida no multi-threading
    private synchronized <T> void loadView(String absolutePath, Consumer<T> initialingAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));

            VBox newVBox = loader.load();
            Scene mainScene = MainApplication.getMainScene();
            VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            Node mainNode = mainVbox.getChildren().get(0);

            mainVbox.getChildren().clear();
            mainVbox.getChildren().add(mainNode);
            mainVbox.getChildren().addAll(newVBox.getChildren());

            T controller = loader.getController();
            initialingAction.accept(controller);
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
