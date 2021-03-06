package controller;

import application.MainApplication;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.security.error.DbIntegrityException;
import model.services.DepartmentService;
import util.Alerts;
import util.Utils;
import view.listeners.DataChangeListener;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;
    @FXML
    private TableColumn<Department, Department> tableColumnEdit;
    @FXML
    private TableColumn<Department, Department> tableColumnRemove;

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

    @Override
    public void onDataChanged() {
        this.updateTableView();
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
        if (service == null) {
            throw new IllegalStateException("Service was null.");
        }

        departments = FXCollections.observableList(
                service.findAll()
        );

        tableViewDepartment.setItems(departments);
        this.initEditButtons();
        this.initRemoveButtons();
    }

    private void createDialogForm(Department department, Stage parentStage, String absolutePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();

            controller.setDepartment(department);
            controller.setService(new DepartmentService());
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();

            dialogStage.setTitle("Enter Department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert(
                    "IO Exception",
                    "Error loading view",
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void initEditButtons() {
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);

                if (department == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);

                button.setOnAction(
                        event -> createDialogForm(
                                department,
                                Utils.currentStage(event),
                                "/view/DepartmentFormView.fxml"
                        )
                );
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnRemove.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("remove");

            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);
                
                if (department == null) {
                    setGraphic(null);
                    return;
                }
                
                setGraphic(button);
                
                button.setOnAction(event -> removeEntity(department));
            }
        });
    }

    private void removeEntity(Department department) {
        Optional<ButtonType> result = Alerts.showConfirmation(
                "Confirmation",
                "Are you sure to delete?"
        );

        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Serivce was null");
            }

            try {
                service.remove(department);
                this.updateTableView();
            } catch (DbIntegrityException e) {
                Alerts.showAlert(
                        "Error removing object",
                        null,
                        e.getMessage(),
                        Alert.AlertType.ERROR
                );
            }
        }
    }
}
