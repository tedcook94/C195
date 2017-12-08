package SchedulingApp.ViewController;

import SchedulingApp.Model.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportsController {

    @FXML
    private Label lblReports;
    @FXML
    private Label lblAppointmentTypesByMonthReport;
    @FXML
    private Button btnGenerateAppointmentTypeByMonth;
    @FXML
    private Label lblConsultantScheduleReport;
    @FXML
    private Button btnGenerateConsultantSchedule;
    @FXML
    private Label lblCustomerScheduleReport;
    @FXML
    private Button btnGenerateCustomerSchedule;
    @FXML
    private Button btnReportsExit;

    // Set labels to local language (default is English)
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        lblReports.setText(rb.getString("lblReports"));
        lblAppointmentTypesByMonthReport.setText(rb.getString("lblAppointmentTypesByMonth"));
        btnGenerateAppointmentTypeByMonth.setText(rb.getString("btnGenerate"));
        lblConsultantScheduleReport.setText(rb.getString("lblConsultantSchedule"));
        btnGenerateConsultantSchedule.setText(rb.getString("btnGenerate"));
        lblCustomerScheduleReport.setText(rb.getString("lblCustomerSchedule"));
        btnGenerateCustomerSchedule.setText(rb.getString("btnGenerate"));
        btnReportsExit.setText(rb.getString("btnExit"));
    }

    // Exit reports screen
    private void exit(ActionEvent event) {
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

    // Initialize screen elements
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign actions to buttons
        btnGenerateAppointmentTypeByMonth.setOnAction(event -> DBManager.generateAppointmentTypeByMonthReport());
        btnGenerateConsultantSchedule.setOnAction(event -> DBManager.generateScheduleForConsultants());
        btnGenerateCustomerSchedule.setOnAction(event -> DBManager.generateUpcomingMeetingsByCustomer());
        btnReportsExit.setOnAction(event -> exit(event));
    }
}
