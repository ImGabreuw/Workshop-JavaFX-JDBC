package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import util.Constraints;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable {

    private Department department;

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
    public void onButtonSaveAction() {
        System.out.println("onButtonSaveAction");
    }

    @FXML
    public void onButtonCancelAction() {
        System.out.println("onButtonCancelAction");
    }

    public void setDepartment(Department department) {
        this.department = department;
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
}