package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.security.error.DbException;
import model.security.error.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;
import util.Alerts;
import util.Constraints;
import util.Utils;
import view.listeners.DataChangeListener;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller seller;
    private SellerService sellerService;
    private DepartmentService departmentService;

    private final List<DataChangeListener> DATA_CHANGE_LISTENERS = new ArrayList<>();

    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private DatePicker datePickerBirthDate;
    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelErrorName;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorBirthDate;
    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;

    private ObservableList<Department> observableListDepartment;

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
        if (seller == null) {
            throw new IllegalStateException("Seller was null");
        }

        if (sellerService == null) {
            throw new IllegalStateException("Service was null");
        }

        try {
            seller = this.getFormData();
            sellerService.saveOrUpdate(seller);
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

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void setServices(SellerService sellerService, DepartmentService departmentService) {
        this.sellerService = sellerService;
        this.departmentService = departmentService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 70);
        Constraints.setTextFieldDouble(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldEmail, 60);
        Utils.formatDatePicker(datePickerBirthDate, "dd/MM/yyyy");

        this.initializeComboBoxDepartment();
    }

    public void updateFormData() {
        if (seller == null) {
            throw new IllegalStateException("Seller was null!");
        }

        textFieldId.setText(
                String.valueOf(seller.getId())
        );
        textFieldName.setText(
                seller.getName()
        );
        textFieldEmail.setText(
                seller.getEmail()
        );
        Locale.setDefault(Locale.US);
        textFieldBaseSalary.setText(
                String.format("%.2f", seller.getBaseSalary())
        );

        if (seller.getBirthDate() != null) {
            datePickerBirthDate.setValue(
                    LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault())
            );
        }

        if (seller.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        } else {
            comboBoxDepartment.setValue(seller.getDepartment());
        }
    }

    private Seller getFormData() {
        Seller seller = new Seller();

        ValidationException exception = new ValidationException("Validation error");

        seller.setId(
                Utils.tryToParseToInt(textFieldId.getText())
        );

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
            exception.addError(
                    "name",
                    "Field can´t be null"
            );
        }
        seller.setName(
                textFieldName.getText()
        );

        if (exception.getERRORS().size() > 0) {
            throw exception;
        }

        return seller;
    }

    public void loadAssociatedObjects() {
        if (departmentService == null) {
            throw new IllegalStateException("DepartmentService was nulll");
        }

        observableListDepartment = FXCollections.observableList(
                departmentService.findAll()
        );

        comboBoxDepartment.setItems(observableListDepartment);
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

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty ? "" : item.getName());
            }
        };

        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }
}
