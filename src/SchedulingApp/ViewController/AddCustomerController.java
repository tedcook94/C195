package SchedulingApp.ViewController;

import SchedulingApp.Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static SchedulingApp.Model.DBManager.addNewCustomer;

public class AddCustomerController {

    @FXML
    private Label lblAddCustomerTitle;
    @FXML
    private Label lblAddCustomerName;
    @FXML
    private TextField txtAddCustomerName;
    @FXML
    private Label lblAddCustomerAddress;
    @FXML
    private TextField txtAddCustomerAddress;
    @FXML
    private Label lblAddCustomerAddress2;
    @FXML
    private TextField txtAddCustomerAddress2;
    @FXML
    private Label lblAddCustomerCity;
    @FXML
    private TextField txtAddCustomerCity;
    @FXML
    private Label lblAddCustomerCountry;
    @FXML
    private TextField txtAddCustomerCountry;
    @FXML
    private Label lblAddCustomerPostalCode;
    @FXML
    private TextField txtAddCustomerPostalCode;
    @FXML
    private Label lblAddCustomerPhone;
    @FXML
    private TextField txtAddCustomerPhone;
    @FXML
    private Button btnAddCustomerSave;
    @FXML
    private Button btnAddCustomerCancel;

    // Set labels to local language (default is English)
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
        lblAddCustomerTitle.setText(rb.getString("lblAddCustomer"));
        lblAddCustomerName.setText(rb.getString("lblCustomerName"));
        txtAddCustomerName.setPromptText(rb.getString("lblCustomerName"));
        lblAddCustomerAddress.setText(rb.getString("lblAddress"));
        txtAddCustomerAddress.setPromptText(rb.getString("lblAddress"));
        lblAddCustomerAddress2.setText(rb.getString("lblAddress2"));
        txtAddCustomerAddress2.setPromptText(rb.getString("lblAddress2"));
        lblAddCustomerCity.setText(rb.getString("lblCity"));
        txtAddCustomerCity.setPromptText(rb.getString("lblCity"));
        lblAddCustomerCountry.setText(rb.getString("lblCountry"));
        txtAddCustomerCountry.setPromptText(rb.getString("lblCountry"));
        lblAddCustomerPostalCode.setText(rb.getString("lblPostalCode"));
        txtAddCustomerPostalCode.setPromptText(rb.getString("lblPostalCode"));
        lblAddCustomerPhone.setText(rb.getString("lblPhone"));
        txtAddCustomerPhone.setPromptText(rb.getString("lblPhone"));
        btnAddCustomerSave.setText(rb.getString("btnSave"));
        btnAddCustomerCancel.setText(rb.getString("btnCancel"));
    }

    // Submit customer information to be added to database
    @FXML
    private void saveAddCustomer(ActionEvent event) {
        // Retrieve values from text boxes
        String customerName = txtAddCustomerName.getText();
        String address = txtAddCustomerAddress.getText();
        String address2 = txtAddCustomerAddress2.getText();
        String city = txtAddCustomerCity.getText();
        String country = txtAddCustomerCountry.getText();
        String postalCode = txtAddCustomerPostalCode.getText();
        String phone = txtAddCustomerPhone.getText();
        // Check if all fields are valid
        String errorMessage = Customer.isCustomerValid(customerName, address, city, country, postalCode, phone);
        // If error message contains something, create error message box
        if (errorMessage.length() > 0) {
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        // If error message is empty, add customer to database and return to main screen
        try {
            addNewCustomer(customerName, address, address2, city, country, postalCode, phone);
            Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene mainScreenScene = new Scene(mainScreenParent);
            Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainScreenStage.setScene(mainScreenScene);
            mainScreenStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cancel adding a new customer
    @FXML
    private void cancelAddCustomer(ActionEvent event) {
        // Create alert box for cancel
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelAddMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // Check if OK button was clicked and return to main screen if it was
        if (result.get() == ButtonType.OK) {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Initialize screen elements
    @FXML
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign actions to buttons
        btnAddCustomerSave.setOnAction(event -> saveAddCustomer(event));
        btnAddCustomerCancel.setOnAction(event -> cancelAddCustomer(event));
    }
}
