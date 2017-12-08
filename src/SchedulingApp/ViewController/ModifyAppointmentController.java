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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static SchedulingApp.Model.AppointmentList.getAppointmentList;
import static SchedulingApp.Model.CustomerRoster.getCustomerRoster;
import static SchedulingApp.Model.DBManager.modifyAppointment;

public class ModifyAppointmentController {

    @FXML
    private Label lblModifyAppointment;
    @FXML
    private Label lblModifyAppointmentTitle;
    @FXML
    private TextField txtModifyAppointmentTitle;
    @FXML
    private Label lblModifyAppointmentDescription;
    @FXML
    private TextArea txtModifyAppointmentDescription;
    @FXML
    private Label lblModifyAppointmentLocation;
    @FXML
    private TextField txtModifyAppointmentLocation;
    @FXML
    private Label lblModifyAppointmentContact;
    @FXML
    private TextField txtModifyAppointmentContact;
    @FXML
    private Label lblModifyAppointmentUrl;
    @FXML
    private TextField txtModifyAppointmentUrl;
    @FXML
    private Label lblModifyAppointmentDate;
    @FXML
    private DatePicker dateModifyAppointmentDate;
    @FXML
    private Label lblModifyAppointmentStartTime;
    @FXML
    private TextField txtModifyAppointmentStartHour;
    @FXML
    private TextField txtModifyAppointmentStartMinute;
    @FXML
    private ChoiceBox<String> choiceModifyAppointmentStartAMPM;
    @FXML
    private Label lblModifyAppointmentEndTime;
    @FXML
    private TextField txtModifyAppointmentEndHour;
    @FXML
    private TextField txtModifyAppointmentEndMinute;
    @FXML
    private ChoiceBox<String> choiceModifyAppointmentEndAMPM;
    @FXML
    private TableView<Customer> tvModifyAppointmentAdd;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentAddNameColumn;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentAddCityColumn;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentAddCountryColumn;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentAddPhoneColumn;
    @FXML
    private Button btnModifyAppointmentAdd;
    @FXML
    private TableView<Customer> tvModifyAppointmentDelete;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentDeleteNameColumn;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentDeleteCityColumn;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentDeleteCountryColumn;
    @FXML
    private TableColumn<Customer, String> tvModifyAppointmentDeletePhoneColumn;
    @FXML
    private Button btnModifyAppointmentDelete;
    @FXML
    private Button btnModifyAppointmentSave;
    @FXML
    private Button btnModifyAppointmentCancel;

    // Initialize appointment object
    private Appointment appointment;
    // Get index of appointment to be modified
    int appointmentIndexToModify = AppointmentSummaryController.getAppointmentIndexToModify();
    // Initialize ObservableList to hold customers associated with appointment
    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();

    // Set labels to local language (default is English)
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        lblModifyAppointment.setText(rb.getString("lblModifyAppointment"));
        lblModifyAppointmentTitle.setText(rb.getString("lblTitle"));
        txtModifyAppointmentTitle.setPromptText(rb.getString("lblTitle"));
        lblModifyAppointmentDescription.setText(rb.getString("lblDescription"));
        txtModifyAppointmentDescription.setPromptText(rb.getString("lblDescription"));
        lblModifyAppointmentLocation.setText(rb.getString("lblLocation"));
        txtModifyAppointmentLocation.setPromptText(rb.getString("lblLocation"));
        lblModifyAppointmentContact.setText(rb.getString("lblContact"));
        txtModifyAppointmentContact.setPromptText(rb.getString("lblContact"));
        lblModifyAppointmentUrl.setText(rb.getString("lblUrl"));
        txtModifyAppointmentUrl.setPromptText(rb.getString("lblUrl"));
        lblModifyAppointmentDate.setText(rb.getString("lblDate"));
        lblModifyAppointmentStartTime.setText(rb.getString("lblStartTime"));
        lblModifyAppointmentEndTime.setText(rb.getString("lblEndTime"));
        tvModifyAppointmentAddNameColumn.setText(rb.getString("lblNameColumn"));
        tvModifyAppointmentAddCityColumn.setText(rb.getString("lblCityColumn"));
        tvModifyAppointmentAddCountryColumn.setText(rb.getString("lblCountryColumn"));
        tvModifyAppointmentAddPhoneColumn.setText(rb.getString("lblPhoneColumn"));
        tvModifyAppointmentDeleteNameColumn.setText(rb.getString("lblNameColumn"));
        tvModifyAppointmentDeleteCityColumn.setText(rb.getString("lblCityColumn"));
        tvModifyAppointmentDeleteCountryColumn.setText(rb.getString("lblCountryColumn"));
        tvModifyAppointmentDeletePhoneColumn.setText(rb.getString("lblPhoneColumn"));
        btnModifyAppointmentAdd.setText(rb.getString("btnAdd"));
        btnModifyAppointmentDelete.setText(rb.getString("btnDelete"));
        btnModifyAppointmentSave.setText(rb.getString("btnSave"));
        btnModifyAppointmentCancel.setText(rb.getString("btnCancel"));
    }

    // Add customer to the lower table view of current customers
    @FXML
    private void addCustomerToDeleteTableView(ActionEvent event) {
        // Get selected customer from upper table view
        Customer customer = tvModifyAppointmentAdd.getSelectionModel().getSelectedItem();
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
        updateModifyAppointmentDeleteTableView();
    }

    // Remove customer from the lower table view of current customers
    @FXML
    private void deleteCustomerFromDeleteTableView(ActionEvent event) {
        // Get selected customer from lower table view
        Customer customer = tvModifyAppointmentDelete.getSelectionModel().getSelectedItem();
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
            updateModifyAppointmentDeleteTableView();
        }
    }

    // Submit appointment information to be updated in database
    @FXML
    private void saveModifyAppointment(ActionEvent event) {
        // Initialize customer
        Customer customer = null;
        // Check if currentCustomers contains a customer and get customer if it does
        if (currentCustomers.size() == 1) {
            customer = currentCustomers.get(0);
        }
        // Get other appointment information
        int appointmentId = appointment.getAppointmentId();
        String title = txtModifyAppointmentTitle.getText();
        String description = txtModifyAppointmentDescription.getText();
        String location = txtModifyAppointmentLocation.getText();
        String contact = txtModifyAppointmentContact.getText();
        // If contact field has been left empty, fill with customers name and phone
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustomerName() + ", " + customer.getPhone();
        }
        String url = txtModifyAppointmentUrl.getText();
        LocalDate appointmentDate = dateModifyAppointmentDate.getValue();
        String startHour = txtModifyAppointmentStartHour.getText();
        String startMinute = txtModifyAppointmentStartMinute.getText();
        String startAmPm = choiceModifyAppointmentStartAMPM.getSelectionModel().getSelectedItem();
        String endHour = txtModifyAppointmentEndHour.getText();
        String endMinute = txtModifyAppointmentEndMinute.getText();
        String endAmPm = choiceModifyAppointmentEndAMPM.getSelectionModel().getSelectedItem();
        // Submit appointment information for validation
        String errorMessage = Appointment.isAppointmentValid(customer, title, description, location,
                appointmentDate, startHour, startMinute, startAmPm, endHour, endMinute, endAmPm);
        // Check if errorMessage contains anything
        if (errorMessage.length() > 0) {
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
            // Show alert with errorMessage
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingAppointment"));
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
        // Submit information to be updated in database. Check if 'true' is returned
        if (modifyAppointment(appointmentId, customer, title, description, location, contact, url, startUTC, endUTC)) {
            try {
                // Return to appointment summary window
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
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

    // Cancel modifying appointment
    @FXML
    private void cancelModifyAppointment(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        // Show alert to confirm cancel
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelModifyingMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If 'OK' button was selected, return to appointment summary window
        if(result.get() == ButtonType.OK) {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
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
    public void updateModifyAppointmentAddTableView() {
        tvModifyAppointmentAdd.setItems(CustomerRoster.getCustomerRoster());
    }

    // Update lower table view
    public void updateModifyAppointmentDeleteTableView() {
        tvModifyAppointmentDelete.setItems(currentCustomers);
    }

    // Initialize screen elements
    @FXML
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign actions to buttons
        btnModifyAppointmentAdd.setOnAction(event -> addCustomerToDeleteTableView(event));
        btnModifyAppointmentDelete.setOnAction(event -> deleteCustomerFromDeleteTableView(event));
        btnModifyAppointmentSave.setOnAction(event -> saveModifyAppointment(event));
        btnModifyAppointmentCancel.setOnAction(event -> cancelModifyAppointment(event));
        // Get appointment to be modified via index
        appointment = getAppointmentList().get(appointmentIndexToModify);
        // Get appoinment information
        String title = appointment.getTitle();
        String description = appointment.getDescription();
        String location = appointment.getLocation();
        String contact = appointment.getContact();
        String url = appointment.getUrl();
        Date appointmentDate = appointment.getStartDate();
        // Transform appointmentDate into LocalDate
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(appointmentDate);
        int appointmentYear = calendar.get(Calendar.YEAR);
        int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
        int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
        LocalDate appointmentLocalDate = LocalDate.of(appointmentYear, appointmentMonth, appointmentDay);
        // Split time strings into hour, minute and AM/PM strings
        String startString = appointment.getStartString();
        String startHour = startString.substring(0,2);
        if (Integer.parseInt(startHour) < 10) {
            startHour = startHour.substring(1,2);
        }
        String startMinute = startString.substring(3,5);
        String startAmPm = startString.substring(6,8);
        String endString = appointment.getEndString();
        String endHour = endString.substring(0,2);
        if (Integer.parseInt(endHour) < 10) {
            endHour = endHour.substring(1,2);
        }
        String endMinute = endString.substring(3,5);
        String endAmPm = endString.substring(6,8);
        // Get customer to add to currentCustomers via customerId
        int customerId = appointment.getCustomerId();
        ObservableList<Customer> customerRoster = getCustomerRoster();
        for (Customer customer : customerRoster) {
            if (customer.getCustomerId() == customerId) {
                currentCustomers.add(customer);
            }
        }
        // Populate information fields with current appointment information
        txtModifyAppointmentTitle.setText(title);
        txtModifyAppointmentDescription.setText(description);
        txtModifyAppointmentLocation.setText(location);
        txtModifyAppointmentContact.setText(contact);
        txtModifyAppointmentUrl.setText(url);
        dateModifyAppointmentDate.setValue(appointmentLocalDate);
        txtModifyAppointmentStartHour.setText(startHour);
        txtModifyAppointmentStartMinute.setText(startMinute);
        choiceModifyAppointmentStartAMPM.setValue(startAmPm);
        txtModifyAppointmentEndHour.setText(endHour);
        txtModifyAppointmentEndMinute.setText(endMinute);
        choiceModifyAppointmentEndAMPM.setValue(endAmPm);
        // Assign data to table views
        tvModifyAppointmentAddNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvModifyAppointmentAddCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        tvModifyAppointmentAddCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvModifyAppointmentAddPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        tvModifyAppointmentDeleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvModifyAppointmentDeleteCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        tvModifyAppointmentDeleteCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvModifyAppointmentDeletePhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Update table views
        updateModifyAppointmentAddTableView();
        updateModifyAppointmentDeleteTableView();
    }
}
