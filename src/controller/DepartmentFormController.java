package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.security.error.DbException;
import model.services.DepartmentService;
import util.Alerts;
import util.Constraints;
import util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable {

    private Department department;
    private DepartmentService service;

    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldName;

    @FXML
    private Label labelErrorName;

    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
        if (department == null) {
            throw new IllegalStateException("Department was null");
        }

        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        try {
            department = getFormData();
            service.saveOrUpdate(department);

            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert(
                    "Error saving object",
                    null,
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void onButtonCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setService(DepartmentService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
    }

    public void updateFormData() {
        if (department == null) {
            throw new IllegalStateException("Department was null!");
        }

        textFieldId.setText(
                String.valueOf(department.getId())
        );
        textFieldName.setText(
                department.getName()
        );
    }

    private Department getFormData() {
        Department department = new Department();

        department.setId(
                Utils.tryToParseToInt(textFieldId.getText())
        );
        department.setName(
                textFieldName.getText()
        );

        return department;
    }
}
