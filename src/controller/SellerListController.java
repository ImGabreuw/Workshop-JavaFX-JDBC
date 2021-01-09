package controller;

import application.MainApplication;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.security.error.DbIntegrityException;
import model.services.SellerService;
import util.Alerts;
import util.Utils;
import view.listeners.DataChangeListener;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    private SellerService service;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;
    @FXML
    private TableColumn<Seller, String> tableColumnName;
    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit;
    @FXML
    private TableColumn<Seller, Seller> tableColumnRemove;

    @FXML
    private Button buttonNew;

    private ObservableList<Seller> sellers;

    @FXML
    public void onButtonNewAction(ActionEvent event) {
        Seller seller = new Seller();

        this.createDialogForm(
                seller,
                Utils.currentStage(event),
                "../view/SellerFormView.fxml"
        );
    }

    public void setService(SellerService service) {
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

        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null.");
        }

        sellers = FXCollections.observableList(
                service.findAll()
        );

        tableViewSeller.setItems(sellers);
        this.initEditButtons();
        this.initRemoveButtons();
    }

    private void createDialogForm(Seller seller, Stage parentStage, String absolutePath) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));
//            Pane pane = loader.load();
//
//            SellerFormController controller = loader.getController();
//
//            controller.setService(seller);
//            controller.setService(new SellerService());
//            controller.subscribeDataChangeListener(this);
//            controller.updateFormData();
//
//            Stage dialogStage = new Stage();
//
//            dialogStage.setTitle("Enter seller data");
//            dialogStage.setScene(new Scene(pane));
//            dialogStage.setResizable(false);
//            dialogStage.initOwner(parentStage);
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.showAndWait();
//        } catch (IOException e) {
//            Alerts.showAlert(
//                    "IO Exception",
//                    "Error loading view",
//                    e.getMessage(),
//                    Alert.AlertType.ERROR
//            );
//        }
    }

    private void initEditButtons() {
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);

                if (seller == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);

                button.setOnAction(
                        event -> createDialogForm(
                                seller,
                                Utils.currentStage(event),
                                "/view/SellerFormView.fxml"
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
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);
                
                if (seller == null) {
                    setGraphic(null);
                    return;
                }
                
                setGraphic(button);
                
                button.setOnAction(event -> removeEntity(seller));
            }
        });
    }

    private void removeEntity(Seller seller) {
        Optional<ButtonType> result = Alerts.showConfirmation(
                "Confirmation",
                "Are you sure to delete?"
        );

        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service was null");
            }

            try {
                service.remove(seller);
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
