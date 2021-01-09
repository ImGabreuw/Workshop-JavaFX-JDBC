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
import model.security.error.ValidationException;
import model.services.DepartmentService;
import util.Alerts;
import util.Constraints;
import util.Utils;
import view.listeners.DataChangeListener;

import java.net.URL;
import java.util.*;

public class DepartmentFormController implements Initializable {

    private Department department;
    private DepartmentService service;

    private final List<DataChangeListener> DATA_CHANGE_LISTENERS = new ArrayList<>();

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
            department = this.getFormData();
            service.saveOrUpdate(department);
            this.notifyDataChangeListener();

            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert(
                    "Error saving object",
                    null,
                    e.getMessage(),
                    Alert.AlertType.ERROR
            );
        } catch (ValidationException e) {
            setErrorMessages(e.getERRORS());
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

        ValidationException exception = new ValidationException("Validation error");

        department.setId(
                Utils.tryParseToInt(textFieldId.getText())
        );

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
            exception.addError(
                    "name",
                    "Field can´t be null"
            );
        }
        department.setName(
                textFieldName.getText()
        );

        if (exception.getERRORS().size() > 0) {
            throw exception;
        }

        return department;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        this.DATA_CHANGE_LISTENERS.add(listener);
    }

    private void notifyDataChangeListener() {
        this.DATA_CHANGE_LISTENERS
                .forEach(DataChangeListener::onDataChanged);
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("name")) {
            labelErrorName.setText(errors.get("name"));
        }
    }
}
