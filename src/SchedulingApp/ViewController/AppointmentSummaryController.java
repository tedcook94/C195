package SchedulingApp.ViewController;

import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.AppointmentList;
import SchedulingApp.Model.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import static SchedulingApp.Model.DBManager.updateAppointmentList;
import static SchedulingApp.Model.AppointmentList.getAppointmentList;

public class AppointmentSummaryController {

    @FXML
    private TableView<Appointment> tvAppointmentSummary;
    @FXML
    private TableColumn<Appointment, String> tvAppointmentSummaryTitleColumn;
    @FXML
    private TableColumn<Appointment, String> tvAppointmentSummaryDateColumn;
    @FXML
    private TableColumn<Appointment, String> tvAppointmentSummaryContactColumn;
    @FXML
    private Button btnAppointmentSummaryGetInfo;
    @FXML
    private Button btnAppointmentSummaryModify;
    @FXML
    private Button btnAppointmentSummaryDelete;
    @FXML
    private Button btnAppointmentSummaryExit;
    @FXML
    private Label lblAppointmentSummaryTitle;
    @FXML
    private Label lblAppointmentSummaryDescription;
    @FXML
    private Label lblAppointmentSummaryLocation;
    @FXML
    private Label lblAppointmentSummaryContact;
    @FXML
    private Label lblAppointmentSummaryURL;
    @FXML
    private Label lblAppointmentSummaryDate;
    @FXML
    private Label lblAppointmentSummaryStartTime;
    @FXML
    private Label lblAppointmentSummaryEndTime;
    @FXML
    private Label lblAppointmentSummaryCreatedBy;

    // Holds index of the appointment that will be modified
    private static int appointmentIndexToModify;

    // Set labels to local language (default is English)
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        tvAppointmentSummaryTitleColumn.setText(rb.getString("lblTitle"));
        tvAppointmentSummaryDateColumn.setText(rb.getString("lblDate"));
        tvAppointmentSummaryContactColumn.setText(rb.getString("lblContact"));
        btnAppointmentSummaryGetInfo.setText(rb.getString("btnGetInfo"));
        btnAppointmentSummaryModify.setText(rb.getString("btnModify"));
        btnAppointmentSummaryDelete.setText(rb.getString("btnDelete"));
        btnAppointmentSummaryExit.setText(rb.getString("btnExit"));
        lblAppointmentSummaryTitle.setText(rb.getString("lblTitle") + ":");
        lblAppointmentSummaryDescription.setText(rb.getString("lblDescription") + ":");
        lblAppointmentSummaryLocation.setText(rb.getString("lblLocation") + ":");
        lblAppointmentSummaryContact.setText(rb.getString("lblContact") + ":");
        lblAppointmentSummaryURL.setText(rb.getString("lblUrl") + ":");
        lblAppointmentSummaryDate.setText(rb.getString("lblDate") + ":");
        lblAppointmentSummaryStartTime.setText(rb.getString("lblStartTime") + ":");
        lblAppointmentSummaryEndTime.setText(rb.getString("lblEndTime") + ":");
        lblAppointmentSummaryCreatedBy.setText(rb.getString("lblCreatedBy"));
    }

    // Show more information about selected appointment
    @FXML
    private void getMoreInfo(ActionEvent event) {
        // Get selected appointment from table view
        Appointment appointment = tvAppointmentSummary.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        // Check if an appointment was not selected
        if (appointment == null) {
            // Show alert saying an appointment must be selected to get more information
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorGettingInfo"));
            alert.setContentText(rb.getString("errorGettingInfoMessage"));
            alert.showAndWait();
        }
        else {
            // Update labels with appointment information
            lblAppointmentSummaryTitle.setText(rb.getString("lblTitle") + ": " + appointment.getTitle());
            lblAppointmentSummaryDescription.setText(rb.getString("lblDescription") + ": " + appointment.getDescription());
            lblAppointmentSummaryLocation.setText(rb.getString("lblLocation") + ": " + appointment.getLocation());
            lblAppointmentSummaryContact.setText(rb.getString("lblContact") + ": " + appointment.getContact());
            lblAppointmentSummaryURL.setText(rb.getString("lblUrl") + ": " + appointment.getUrl());
            lblAppointmentSummaryDate.setText(rb.getString("lblDate") + ": " + appointment.getDateString());
            lblAppointmentSummaryStartTime.setText(rb.getString("lblStartTime") + ": " + appointment.getStartString());
            lblAppointmentSummaryEndTime.setText(rb.getString("lblEndTime") + ": " + appointment.getEndString());
            lblAppointmentSummaryCreatedBy.setText(rb.getString("lblCreatedBy") + ": " + appointment.getCreatedBy());
        }
    }

    // Open modify appointment window
    @FXML
    private void openModifyAppointment(ActionEvent event) {
        // Get selected appointment from table view
        Appointment appointmentToModify = tvAppointmentSummary.getSelectionModel().getSelectedItem();
        // Check to see if no appointment was selected
        if (appointmentToModify == null) {
            // Create alert saying an appointment must be selected to be modified
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingAppointment"));
            alert.setContentText(rb.getString("errorModifyingAppointmentPleaseSelect"));
            alert.showAndWait();
            return;
        }
        // Set the index of the appointment that was selected to be modified
        appointmentIndexToModify = getAppointmentList().indexOf(appointmentToModify);
        // Open modify appointment window
        try {
            Parent modifyAppointmentParent = FXMLLoader.load(getClass().getResource("ModifyAppointment.fxml"));
            Scene modifyAppointmentScene = new Scene(modifyAppointmentParent);
            Stage modifyAppointmentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modifyAppointmentStage.setScene(modifyAppointmentScene);
            modifyAppointmentStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete the selected appointment
    @FXML
    private void deleteAppointment(ActionEvent event) {
        // Get the selected appointment from the table view
        Appointment appointmentToDelete = tvAppointmentSummary.getSelectionModel().getSelectedItem();
        // Check if no appointment was selected if
        if (appointmentToDelete == null) {
            // Show alert saying an appointment must be selected to delete
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorDeletingAppointment"));
            alert.setContentText(rb.getString("errorDeletingAppointmentMessage"));
            alert.showAndWait();
            return;
        }
        // Submit appointment to be deleted
        DBManager.deleteAppointment(appointmentToDelete);
    }

    // Exit the appointment summary windows
    @FXML
    private void exit(ActionEvent event) {
        // Return to the main screen
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

    // Return the appointment index to be modified
    public static int getAppointmentIndexToModify() {
        return appointmentIndexToModify;
    }

    // Update the table view
    @FXML
    public void updateAddAppointmentTableView() {
        updateAppointmentList();
        tvAppointmentSummary.setItems(AppointmentList.getAppointmentList());
    }

    // Initialize screen elements
    @FXML
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign actions to buttons
        btnAppointmentSummaryGetInfo.setOnAction(event -> getMoreInfo(event));
        btnAppointmentSummaryModify.setOnAction(event -> openModifyAppointment(event));
        btnAppointmentSummaryDelete.setOnAction(event -> deleteAppointment(event));
        btnAppointmentSummaryExit.setOnAction(event -> exit(event));
        // Assign data to table views
        tvAppointmentSummaryTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tvAppointmentSummaryDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        tvAppointmentSummaryContactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        // Update table view
        updateAddAppointmentTableView();
    }
}
