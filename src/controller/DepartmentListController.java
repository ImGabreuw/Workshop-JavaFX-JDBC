package controller;

import application.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable {

    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private Button buttonNew;

    private ObservableList<Department> departments;

    @FXML
    public void onButtonNewAction() {
        System.out.println("onButtonNewAction");
    }

    public void setService(DepartmentService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeNodes();
    }

    private void initializeNodes() {
        this.tableColumnId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );
        this.tableColumnName.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        Stage stage = (Stage) MainApplication.getMainScene().getWindow();

        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        if (service == null) throw new IllegalStateException("Service was null.");

        departments = FXCollections.observableList(
                service.findAll()
        );

        tableViewDepartment.setItems(departments);
    }
}
