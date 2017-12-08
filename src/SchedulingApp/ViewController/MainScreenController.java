package SchedulingApp.ViewController;

import SchedulingApp.CalendarView.MonthlyCalendarView;
import SchedulingApp.CalendarView.WeeklyCalendarView;
import SchedulingApp.Model.Customer;
import static SchedulingApp.Model.CustomerRoster.getCustomerRoster;
import static SchedulingApp.Model.DBManager.setCustomerToInactive;
import static SchedulingApp.Model.DBManager.updateAppointmentList;
import static SchedulingApp.Model.DBManager.updateCustomerRoster;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static SchedulingApp.Model.DBManager.logInAppointmentNotification;

public class MainScreenController {

    @FXML
    private GridPane mainScreenGrid;
    @FXML
    private Button btnMainScreenToggleView;
    @FXML
    private Button btnMainScreenCurrentDate;
    @FXML
    private Button btnAddAppointment;
    @FXML
    private Button btnAppointmentSummary;
    @FXML
    private Button btnReports;
    @FXML
    private Button btnAddCustomer;
    @FXML
    private Button btnModifyCustomer;
    @FXML
    private Button btnRemoveCustomer;
    @FXML
    private Button btnExit;
    @FXML
    private TableView<Customer> tvCustomers;
    @FXML
    private TableColumn<Customer, String> tvCustomersNameColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersAddressColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersAddress2Column;
    @FXML
    private TableColumn<Customer, String> tvCustomersCityColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersCountryColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersPhoneColumn;

    // Holds index of the customer that will be modified
    private static int customerIndexToModify;
    // Holds whether the calendar is currently in monthly view or not (starts in monthly)
    private boolean monthlyView = true;
    // Initializes calendarViews and VBox's that contain them
    private MonthlyCalendarView monthlyCalendar;
    private WeeklyCalendarView weeklyCalendar;
    private VBox monthlyCalendarView;
    private VBox weeklyCalendarView;

    // Set labels to local language (default is English)
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
        btnMainScreenCurrentDate.setText(rb.getString("btnCurrentDate"));
        btnMainScreenToggleView.setText(rb.getString("btnToggleViewWeekly"));
        btnAddAppointment.setText(rb.getString("btnAddAppointment"));
        btnAppointmentSummary.setText(rb.getString("btnAppointmentSummary"));
        btnReports.setText(rb.getString("btnReports"));
        btnAddCustomer.setText(rb.getString("btnAddCustomer"));
        btnModifyCustomer.setText(rb.getString("btnModifyCustomer"));
        btnRemoveCustomer.setText(rb.getString("btnRemoveCustomer"));
        btnExit.setText(rb.getString("btnExit"));
        tvCustomersNameColumn.setText(rb.getString("lblTVCustomerName"));
        tvCustomersAddressColumn.setText(rb.getString("lblTVAddress"));
        tvCustomersAddress2Column.setText(rb.getString("lblTVAddress2"));
        tvCustomersCityColumn.setText(rb.getString("lblTVCity"));
        tvCustomersCountryColumn.setText(rb.getString("lblTVCountry"));
        tvCustomersPhoneColumn.setText(rb.getString("lblTVPhone"));
    }

    // Open add appointment window
    @FXML
    private void openAddAppointment(ActionEvent event) {
        try {
            Parent addAppointmentParent = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
            Scene addAppointmentScene = new Scene(addAppointmentParent);
            Stage addAppointmentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addAppointmentStage.setScene(addAppointmentScene);
            addAppointmentStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Open appointment summary window
    @FXML
    private void openAppointmentSummary(ActionEvent event) {
        try {
            Parent appointmentSummaryParent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
            Scene appointmentSummaryScene = new Scene(appointmentSummaryParent);
            Stage appointmentSummaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appointmentSummaryStage.setScene(appointmentSummaryScene);
            appointmentSummaryStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Open reports window
    @FXML
    private void openReports(ActionEvent event) {
        try {
            Parent reportsParent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            Scene reportsScene = new Scene(reportsParent);
            Stage reportsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            reportsStage.setScene(reportsScene);
            reportsStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Moves calendar to current date
    @FXML
    private void goToCurrentDate(ActionEvent event) {
        // Check if calendar is currently in monthly view
        if (monthlyView) {
            // Remove current calendarView
            mainScreenGrid.getChildren().remove(monthlyCalendarView);
            // Get current year-month
            YearMonth currentYearMonth = YearMonth.now();
            // Create and set new calendarView with current year-month
            monthlyCalendar = new MonthlyCalendarView(currentYearMonth);
            monthlyCalendarView = monthlyCalendar.getView();
            mainScreenGrid.add(monthlyCalendarView, 0, 0);
        }
        // If calendar is currently in weekly view
        else {
            // Remove current calendarView
            mainScreenGrid.getChildren().remove(weeklyCalendarView);
            // Get current date
            LocalDate currentLocalDate = LocalDate.now();
            // Create and set new calendarView with current date
            weeklyCalendar = new WeeklyCalendarView(currentLocalDate);
            weeklyCalendarView = weeklyCalendar.getView();
            mainScreenGrid.add(weeklyCalendarView, 0, 0);
        }
    }

    // Switch calendar between monthly and weekly view
    @FXML
    private void toggleCalendarView(ActionEvent event) {
        // Check if calendar is currently in monthly view
        if (monthlyView) {
            // Remove current calendarView
            mainScreenGrid.getChildren().remove(monthlyCalendarView);
            // Get calendar's current year-month
            YearMonth currentYearMonth = monthlyCalendar.getCurrentYearMonth();
            // Convert year-month to first day of same month
            LocalDate currentLocalDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), 1);
            // Create and set new calendarView with first day of month
            weeklyCalendar = new WeeklyCalendarView(currentLocalDate);
            weeklyCalendarView = weeklyCalendar.getView();
            mainScreenGrid.add(weeklyCalendarView, 0, 0);
            // Change button to say "Switch to Monthly View"
            btnMainScreenToggleView.setText(ResourceBundle.getBundle("MainScreen", Locale.getDefault()).getString("btnToggleViewMonthly"));
            // Set monthlyView to show calendar is currently in weekly view
            monthlyView = false;
        }
        // If calendar is currently in weekly view
        else {
            // Remove current calendarView
            mainScreenGrid.getChildren().remove(weeklyCalendarView);
            // Get calendar's current date
            LocalDate currentLocalDate = weeklyCalendar.getCurrentLocalDate();
            // Convert date to year-month
            YearMonth currentYearMonth = YearMonth.from(currentLocalDate);
            // Create and set new calendarView with year-month
            monthlyCalendar = new MonthlyCalendarView(currentYearMonth);
            monthlyCalendarView = monthlyCalendar.getView();
            mainScreenGrid.add(monthlyCalendarView, 0, 0);
            // Change button to say "Switch to Weekly View"
            btnMainScreenToggleView.setText(ResourceBundle.getBundle("MainScreen", Locale.getDefault()).getString("btnToggleViewWeekly"));
            // Set monthlyView to show calendar is currently in monthly view
            monthlyView = true;
        }
    }

    // Open add customer window
    @FXML
    private void openAddCustomer(ActionEvent event) {
        try {
            Parent addCustomerParent = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
            Scene addCustomerScene = new Scene(addCustomerParent);
            Stage addCustomerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addCustomerStage.setScene(addCustomerScene);
            addCustomerStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Open modify customer window
    @FXML
    private void openModifyCustomer(ActionEvent event) {
        // Get selected customer from table view
        Customer customerToModify = tvCustomers.getSelectionModel().getSelectedItem();
        // Check if no customer was selected
        if (customerToModify == null) {
            // Create alert saying a customer must be selected to be modified
            ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(rb.getString("errorModifyingCustomerMessage"));
            alert.showAndWait();
            return;
        }
        // Set the index of the customer to be modified
        customerIndexToModify = getCustomerRoster().indexOf(customerToModify);
        // Open modify customer window
        try {
            Parent modifyCustomerParent = FXMLLoader.load(getClass().getResource("ModifyCustomer.fxml"));
            Scene modifyCustomerScene = new Scene(modifyCustomerParent);
            Stage modifyCustomerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modifyCustomerStage.setScene(modifyCustomerScene);
            modifyCustomerStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove the selected customer
    @FXML
    private void removeCustomer(ActionEvent event) {
        // Get selected customer from table view
        Customer customerToRemove = tvCustomers.getSelectionModel().getSelectedItem();
        // Check if no customer was selected
        if (customerToRemove == null) {
            // Create alert saying a customer must be selected to be removed
            ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorRemovingCustomer"));
            alert.setContentText(rb.getString("errorRemovingCustomerMessage"));
            alert.showAndWait();
            return;
        }
        // Submit customer to be removed
        setCustomerToInactive(customerToRemove);
    }

    // Exit the main screen
    @FXML
    private void exit(ActionEvent event) {
        // Create alert to confirm exit
        ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmExit"));
        alert.setHeaderText(rb.getString("confirmExit"));
        alert.setContentText(rb.getString("confirmExitMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If 'OK' button was selected, exit the program
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    // Return the customer index to be modified
    public static int getCustomerIndexToModify() {
        return customerIndexToModify;
    }

    // Update the table view
    public void updateCustomerTableView() {
        updateCustomerRoster();
        tvCustomers.setItems(getCustomerRoster());
    }

    // Initialize screen elements
    @FXML
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign actions to buttons
        btnAddAppointment.setOnAction(event -> openAddAppointment(event));
        btnAppointmentSummary.setOnAction(event -> openAppointmentSummary(event));
        btnReports.setOnAction(event -> openReports(event));
        btnMainScreenCurrentDate.setOnAction(event -> goToCurrentDate(event));
        btnMainScreenToggleView.setOnAction(event -> toggleCalendarView(event));
        btnAddCustomer.setOnAction(event -> openAddCustomer(event));
        btnModifyCustomer.setOnAction(event -> openModifyCustomer(event));
        btnRemoveCustomer.setOnAction(event -> removeCustomer(event));
        btnExit.setOnAction(event -> exit(event));
        // Update appointment list
        updateAppointmentList();
        // Create calendarView and add to gridPane
        monthlyCalendar = new MonthlyCalendarView(YearMonth.now());
        monthlyCalendarView = monthlyCalendar.getView();
        mainScreenGrid.add(monthlyCalendarView, 0, 0);
        // Assign data to table view
        tvCustomersNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        tvCustomersAddressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        tvCustomersAddress2Column.setCellValueFactory(cellData -> cellData.getValue().address2Property());
        tvCustomersCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        tvCustomersCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        tvCustomersPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        // Update table view
        updateCustomerTableView();
        // Create appointment notifications if any need to be shown
        logInAppointmentNotification();
    }
}
