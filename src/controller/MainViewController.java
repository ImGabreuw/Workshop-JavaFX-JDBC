package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

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
        System.out.println("onMenuItemDepartmentAction");
    }

    @FXML
    public void onMenuItemAboutAction() {
        System.out.println("onMenuItemAboutAction");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
