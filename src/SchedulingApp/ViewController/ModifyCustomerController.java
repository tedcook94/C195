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
import java.sql.*;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static SchedulingApp.Model.CustomerRoster.getCustomerRoster;
import static SchedulingApp.Model.DBManager.*;

public class ModifyCustomerController {

    @FXML
    private Label lblModifyCustomerTitle;
    @FXML
    private Label lblModifyCustomerName;
    @FXML
    private TextField txtModifyCustomerName;
    @FXML
    private Label lblModifyCustomerAddress;
    @FXML
    private TextField txtModifyCustomerAddress;
    @FXML
    private Label lblModifyCustomerAddress2;
    @FXML
    private TextField txtModifyCustomerAddress2;
    @FXML
    private Label lblModifyCustomerCity;
    @FXML
    private TextField txtModifyCustomerCity;
    @FXML
    private Label lblModifyCustomerCountry;
    @FXML
    private TextField txtModifyCustomerCountry;
    @FXML
    private Label lblModifyCustomerPostalCode;
    @FXML
    private TextField txtModifyCustomerPostalCode;
    @FXML
    private Label lblModifyCustomerPhone;
    @FXML
    private TextField txtModifyCustomerPhone;
    @FXML
    private Button btnModifyCustomerSave;
    @FXML
    private Button btnModifyCustomerCancel;

    // Initialize customer object
    private Customer customer;
    // Get index of customer to be modified
    int customerIndexToModify = MainScreenController.getCustomerIndexToModify();

    // Set labels to local language (default is English)
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
        lblModifyCustomerTitle.setText(rb.getString("lblModifyCustomer"));
        lblModifyCustomerName.setText(rb.getString("lblCustomerName"));
        txtModifyCustomerName.setPromptText(rb.getString("lblCustomerName"));
        lblModifyCustomerAddress.setText(rb.getString("lblAddress"));
        txtModifyCustomerAddress.setPromptText(rb.getString("lblAddress"));
        lblModifyCustomerAddress2.setText(rb.getString("lblAddress2"));
        txtModifyCustomerAddress2.setPromptText(rb.getString("lblAddress2"));
        lblModifyCustomerCity.setText(rb.getString("lblCity"));
        txtModifyCustomerCity.setPromptText(rb.getString("lblCity"));
        lblModifyCustomerCountry.setText(rb.getString("lblCountry"));
        txtModifyCustomerCountry.setPromptText(rb.getString("lblCountry"));
        lblModifyCustomerPostalCode.setText(rb.getString("lblPostalCode"));
        txtModifyCustomerPostalCode.setPromptText(rb.getString("lblPostalCode"));
        lblModifyCustomerPhone.setText(rb.getString("lblPhone"));
        txtModifyCustomerPhone.setPromptText(rb.getString("lblPhone"));
        btnModifyCustomerSave.setText(rb.getString("btnSave"));
        btnModifyCustomerCancel.setText(rb.getString("btnCancel"));
    }

    // Submit customer information to be updated in database
    @FXML
    private void saveModifyCustomer(ActionEvent event) {
        // Get customer information
        int customerId = customer.getCustomerId();
        String customerName = txtModifyCustomerName.getText();
        String address = txtModifyCustomerAddress.getText();
        String address2 = txtModifyCustomerAddress2.getText();
        String city = txtModifyCustomerCity.getText();
        String country = txtModifyCustomerCountry.getText();
        String postalCode = txtModifyCustomerPostalCode.getText();
        String phone = txtModifyCustomerPhone.getText();
        // Submit customer information for validation
        String errorMessage = Customer.isCustomerValid(customerName, address, city, country, postalCode, phone);
        // Check if errorMessage contains anything
        if (errorMessage.length() > 0) {
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
            // Show alert with errorMessage
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        // Submit information to be updated in database. Save active status that is returned
        int modifyCustomerCheck = modifyCustomer(customerId, customerName, address, address2, city, country, postalCode, phone);
        // Check if active status is 1
        if (modifyCustomerCheck == 1) {
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
            // Create alert saying that customer already exists
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(rb.getString("errorCustomerAlreadyExists"));
            alert.showAndWait();

        }
        // Check if active status is 0
        else if (modifyCustomerCheck == 0) {
            // Calculate country, city and addressId's
            int countryId = calculateCountryId(country);
            int cityId = calculateCityId(city, countryId);
            int addressId = calculateAddressId(address, address2, postalCode, phone, cityId);
            // Submit customer to be set to active
            setCustomerToActive(customerName, addressId);
        }
        try {
            // Return to main screen
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

    // Cancel modifying customer
    @FXML
    private void cancelModifyCustomer(ActionEvent event) {
        // Create alert box for cancel
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelModifyMessage"));
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
        btnModifyCustomerSave.setOnAction(event -> saveModifyCustomer(event));
        btnModifyCustomerCancel.setOnAction(event -> cancelModifyCustomer(event));
        // Get customer to be modified via index
        customer = getCustomerRoster().get(customerIndexToModify);
        // Get customer information
        String customerName = customer.getCustomerName();
        String address = customer.getAddress();
        String address2 = customer.getAddress2();
        String city = customer.getCity();
        String country = customer.getCountry();
        String postalCode = customer.getPostalCode();
        String phone = customer.getPhone();
        // Populate information fields with current customer information
        txtModifyCustomerName.setText(customerName);
        txtModifyCustomerAddress.setText(address);
        txtModifyCustomerAddress2.setText(address2);
        txtModifyCustomerCity.setText(city);
        txtModifyCustomerCountry.setText(country);
        txtModifyCustomerPostalCode.setText(postalCode);
        txtModifyCustomerPhone.setText(phone);
    }
}
