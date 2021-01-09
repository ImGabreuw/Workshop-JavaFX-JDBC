package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.security.error.DbException;
import model.security.error.ValidationException;
import model.services.SellerService;
import util.Alerts;
import util.Constraints;
import util.Utils;
import view.listeners.DataChangeListener;

import java.net.URL;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller seller;
    private SellerService service;

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
        if (seller == null) {
            throw new IllegalStateException("Seller was null");
        }

        if (service == null) {
            throw new IllegalStateException("Service was null");
        }

        try {
            seller = this.getFormData();
            service.saveOrUpdate(seller);
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

    public void setService(SellerService service) {
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
        if (seller == null) {
            throw new IllegalStateException("Seller was null!");
        }

        textFieldId.setText(
                String.valueOf(seller.getId())
        );
        textFieldName.setText(
                seller.getName()
        );
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
