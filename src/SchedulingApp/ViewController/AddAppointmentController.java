package SchedulingApp.ViewController;

import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.Customer;
import SchedulingApp.Model.CustomerRoster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static SchedulingApp.Model.DBManager.addNewAppointment;

public class AddAppointmentController {

    @FXML
    private Label lblAddAppointment;
    @FXML
    private Label lblAddAppointmentTitle;
    @FXML
    private TextField txtAddAppointmentTitle;
    @FXML
    private Label lblAddAppointmentDescription;
    @FXML
    private TextArea txtAddAppointmentDescription;
    @FXML
    private Label lblAddAppointmentLocation;
    @FXML
    private TextField txtAddAppointmentLocation;
    @FXML
    private Label lblAddAppointmentContact;
    @FXML
    private TextField txtAddAppointmentContact;
    @FXML
    private Label lblAddAppointmentUrl;
    @FXML
    private TextField txtAddAppointmentUrl;
    @FXML
    private Label lblAddAppointmentDate;
    @FXML
    private DatePicker dateAddAppointmentDate;
    @FXML
    private Label lblAddAppointmentStartTime;
    @FXML
    private TextField txtAddAppointmentStartHour;
    @FXML
    private TextField txtAddAppointmentStartMinute;
    @FXML
    private ChoiceBox<String> choiceAddAppointmentStartAMPM;
    @FXML
    private Label lblAddAppointmentEndTime;
    @FXML
    private TextField txtAddAppointmentEndHour;
    @FXML
    private TextField txtAddAppointmentEndMinute;
    @FXML
    private ChoiceBox<String> choiceAddAppointmentEndAMPM;
    @FXML
    private TableView<Customer> tvAddAppointmentAdd;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentAddNameColumn;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentAddCityColumn;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentAddCountryColumn;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentAddPhoneColumn;
    @FXML
    private Button btnAddAppointmentAdd;
    @FXML
    private TableView<Customer> tvAddAppointmentDelete;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentDeleteNameColumn;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentDeleteCityColumn;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentDeleteCountryColumn;
    @FXML
    private TableColumn<Customer, String> tvAddAppointmentDeletePhoneColumn;
    @FXML
    private Button btnAddAppointmentDelete;
    @FXML
    private Button btnAddAppointmentSave;
    @FXML
    private Button btnAddAppointmentCancel;

    // ObservableList to hold customers currently assigned to appointment
    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();

    // Set labels to local language (default is English)
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        lblAddAppointment.setText(rb.getString("lblAddAppointment"));
        lblAddAppointmentTitle.setText(rb.getString("lblTitle"));
        txtAddAppointmentTitle.setPromptText(rb.getString("lblTitle"));
        lblAddAppointmentDescription.setText(rb.getString("lblDescription"));
        txtAddAppointmentDescription.setPromptText(rb.getString("lblDescription"));
        lblAddAppointmentLocation.setText(rb.getString("lblLocation"));
        txtAddAppointmentLocation.setPromptText(rb.getString("lblLocation"));
        lblAddAppointmentContact.setText(rb.getString("lblContact"));
        txtAddAppointmentContact.setPromptText(rb.getString("lblContact"));
        lblAddAppointmentUrl.setText(rb.getString("lblUrl"));
        txtAddAppointmentUrl.setPromptText(rb.getString("lblUrl"));
        lblAddAppointmentDate.setText(rb.getString("lblDate"));
        lblAddAppointmentStartTime.setText(rb.getString("lblStartTime"));
        lblAddAppointmentEndTime.setText(rb.getString("lblEndTime"));
        tvAddAppointmentAddNameColumn.setText(rb.getString("lblNameColumn"));
        tvAddAppointmentAddCityColumn.setText(rb.getString("lblCityColumn"));
        tvAddAppointmentAddCountryColumn.setText(rb.getString("lblCountryColumn"));
        tvAddAppointmentAddPhoneColumn.setText(rb.getString("lblPhoneColumn"));
        tvAddAppointmentDeleteNameColumn.setText(rb.getString("lblNameColumn"));
        tvAddAppointmentDeleteCityColumn.setText(rb.getString("lblCityColumn"));
        tvAddAppointmentDeleteCountryColumn.setText(rb.getString("lblCountryColumn"));
        tvAddAppointmentDeletePhoneColumn.setText(rb.getString("lblPhoneColumn"));
        btnAddAppointmentAdd.setText(rb.getString("btnAdd"));
        btnAddAppointmentDelete.setText(rb.getString("btnDelete"));
        btnAddAppointmentSave.setText(rb.getString("btnSave"));
        btnAddAppointmentCancel.setText(rb.getString("btnCancel"));
    }

    // Add customer to the lower table view of current customers
    @FXML
    private void addCustomerToDeleteTableView(ActionEvent event) {
        // Get selected customer from upper table view
        Customer customer = tvAddAppointmentAdd.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        // Check if no customer was selected
        if (customer == null) {
            // Create alert saying a customer must be selected to be added
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(rb.getString("errorAddingCustomerSelectOne"));
            alert.showAndWait();
            return;
        }
        // Check if currentCustomers already contains a customer
        if (currentCustomers.size() > 0) {
            // Create alert saying only one customer can be added to an appointment
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(rb.getString("errorAddingCustomerOnlyOne"));
            alert.showAndWait();
            return;
        }
        // If no customers currently in currentCustomers, add selected customer
        currentCustomers.add(customer);
        // Update lower table view to show newly added customer
        updateAddAppointmentDeleteTableView();
    }

    // Remove customer from the lower table view of current customers
    @FXML
    private void deleteCustomerFromDeleteTableView(ActionEvent event) {
        // Get selected customer from lower table view
        Customer customer = tvAddAppointmentDelete.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        // Check if no customer was selected
        if (customer == null) {
            // Create alert saying a customer must be selected to be removed
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorRemovingCustomer"));
            alert.setContentText(rb.getString("errorRemovingCustomerMessage"));
            alert.showAndWait();
            return;
        }
        // Show alert to confirm removing customer from appointment
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmRemove"));
        alert.setHeaderText(rb.getString("confirmRemoveCustomer"));
        alert.setContentText(rb.getString("confirmRemoveCustomerMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If 'OK' is selected remove customer from currentCustomers
        if (result.get() == ButtonType.OK) {
            currentCustomers.remove(customer);
            // Update lower table view to reflect customer being removed from appointment
            updateAddAppointmentDeleteTableView();
        }
    }

    // Submit appointment information to be added to database
    @FXML
    private void saveAddAppointment(ActionEvent event) {
        // Initialize customer
        Customer customer = null;
        // Check if currentCustomers contains a customer and get customer if it does
        if (currentCustomers.size() == 1) {
            customer = currentCustomers.get(0);
        }
        // Get other appointment information
        String title = txtAddAppointmentTitle.getText();
        String description = txtAddAppointmentDescription.getText();
        String location = txtAddAppointmentLocation.getText();
        String contact = txtAddAppointmentContact.getText();
        // If contact field has been left empty, fill with customers name and phone
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustomerName() + ", " + customer.getPhone();
        }
        String url = txtAddAppointmentUrl.getText();
        LocalDate appointmentDate = dateAddAppointmentDate.getValue();
        String startHour = txtAddAppointmentStartHour.getText();
        String startMinute = txtAddAppointmentStartMinute.getText();
        String startAmPm = choiceAddAppointmentStartAMPM.getSelectionModel().getSelectedItem();
        String endHour = txtAddAppointmentEndHour.getText();
        String endMinute = txtAddAppointmentEndMinute.getText();
        String endAmPm = choiceAddAppointmentEndAMPM.getSelectionModel().getSelectedItem();
        // Submit appointment information for validation
        String errorMessage = Appointment.isAppointmentValid(customer, title, description, location,
                appointmentDate, startHour, startMinute, startAmPm, endHour, endMinute, endAmPm);
        // Check if errorMessage contains anything
        if (errorMessage.length() > 0) {
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
            // Show alert with errorMessage
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        localDateFormat.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;
        // Format date and time strings into Date objects
        try {
            startLocal = localDateFormat.parse(appointmentDate.toString() + " " + startHour + ":" + startMinute + " " + startAmPm);
            endLocal = localDateFormat.parse(appointmentDate.toString() + " " + endHour + ":" + endMinute + " " + endAmPm);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        // Create ZonedDateTime out of Date objects
        ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        // Submit information to be added to database. Check if 'true' is returned
        if (addNewAppointment(customer, title, description, location, contact, url, startUTC, endUTC)) {
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
    }

    // Cancel adding a new appointment
    @FXML
    private void cancelAddAppointment(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        // Show alert to confirm cancel
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelAddingMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If the 'OK' button is clicked, return to main screen
        if(result.get() == ButtonType.OK) {
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

    // Update upper table view
    public void updateAddAppointmentAddTableView() {
        tvAddAppointmentAdd.setItems(CustomerRoster.getCustomerRoster());
    }

    // Update lower table view
    public void updateAddAppointmentDeleteTableView() {
        tvAddAppointmentDelete.setItems(currentCustomers);
    }

    // Initialize screen elements
    @FXML
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign actions to buttons
        btnAddAppointmentAdd.setOnAction(event -> addCustomerToDeleteTableView(event));
        btnAddAppointmentDelete.setOnAction(event -> deleteCustomerFromDeleteTableView(event));
        btnAddAppointmentSave.setOnAction(event -> saveAddAppointment(event));
        btnAddAppointmentCancel.setOnAction(event -> cancelAddAppointment(event));
        // Assign data to table views
        tvAddAppointmentAddNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvAddAppointmentAddCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        tvAddAppointmentAddCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvAddAppointmentAddPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        tvAddAppointmentDeleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvAddAppointmentDeleteCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        tvAddAppointmentDeleteCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvAddAppointmentDeletePhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Update table views
        updateAddAppointmentAddTableView();
        updateAddAppointmentDeleteTableView();
    }
}
