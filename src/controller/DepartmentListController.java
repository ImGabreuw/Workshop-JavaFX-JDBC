package controller;

import application.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;
import util.Alerts;
import util.Utils;

import java.io.IOException;
import java.net.URL;
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
    public void onButtonNewAction(ActionEvent event) {
        Department department = new Department();

        this.createDialogForm(
                department,
                Utils.currentStage(event),
                "../view/DepartmentFormView.fxml"
        );
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

    private void createDialogForm(Department department, Stage parentStage, String absolutePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();

            controller.setDepartment(department);
            controller.setService(new DepartmentService());
            controller.updateFormData();

            Stage dialogStage = new Stage();

            dialogStage.setTitle("Enter Department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
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
