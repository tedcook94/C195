package SchedulingApp.Model;

import SchedulingApp.ViewController.AppointmentSummaryController;
import SchedulingApp.ViewController.LogInController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;

public class DBManager {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String db = "U04Qgb";
    private static final String url = "jdbc:mysql://52.206.157.109/" + db;
    private static final String user = "U04Qgb";
    private static final String pass = "53688313293";

    private static String currentUser;
    private static int openCount = 0;

    // Check log in credentials to see if they are valid
    public static boolean checkLogInCredentials(String userName, String password) {
        int userId = getUserId(userName);
        boolean correctPassword = checkPassword(userId, password);
        if (correctPassword) {
            setCurrentUser(userName);
            try {
                Path path = Paths.get("UserLog.txt");
                Files.write(path, Arrays.asList("User " + currentUser + " logged in at " + Date.from(Instant.now()).toString() + "."),
                        StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }
    // Get userId from userName (returns -1 if no match found for userName)
    private static int getUserId(String userName) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int userId = -1;
        // Try-with-resources and catch block for database connection and handling server connection problem
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {

            // Retrieves userId for entered username
            ResultSet userIdSet = stmt.executeQuery("SELECT userId FROM user WHERE userName = '" + userName + "'");

            // Sets userId to unique value and retrieves int from ResultSet
            if (userIdSet.next()) {
                userId = userIdSet.getInt("userId");
            }
            userIdSet.close();
        }
        catch (SQLException e) {
            // Increment databaseError count in LogInController so error message can be displayed
            LogInController.incrementDatabaseError();
        }
        return userId;
    }
    // Check if password matches userId
    private static boolean checkPassword(int userId, String password) {
        // Try-with-resources and catch block for database connection and handling server connection problem
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            // Retrieves password by userId
            ResultSet passwordSet = stmt.executeQuery("SELECT password FROM user WHERE userId = " + userId);

            // Initializes dbPassword and retrieves String from ResultSet
            String dbPassword = null;
            if (passwordSet.next()) {
                dbPassword = passwordSet.getString("password");
            }
            else {
                return false;
            }
            passwordSet.close();
            // Checks dbPassword against user-entered password and returns boolean
            if (dbPassword.equals(password)) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Set currentUser string for public access
    private static void setCurrentUser(String userName) {
        currentUser = userName;
    }


    // Create notifications for user appointments occurring in the next 15 minutes
    public static void logInAppointmentNotification() {
        // Checks to see if the main screen has already been opened during this session
        if (openCount == 0) {
            // Create ObservableList of appointments that were created by the user who just logged in
            ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();
            for (Appointment appointment : AppointmentList.getAppointmentList()) {
                if (appointment.getCreatedBy().equals(currentUser)) {
                    userAppointments.add(appointment);
                }
            }
            // Check each appointment in userAppointments to see if any start in the next 15 minutes
            for (Appointment appointment : userAppointments) {
                // Create Date object for 15 minutes from now
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Date.from(Instant.now()));
                calendar.add(Calendar.MINUTE, 15);
                Date notificationCutoff = calendar.getTime();
                // If appointment start date is before 15 minutes from now, create alert with information about appointment
                if (appointment.getStartDate().before(notificationCutoff)) {
                    ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("notificationUpcomingAppointment"));
                    alert.setHeaderText(rb.getString("notificationUpcomingAppointment"));
                    alert.setContentText(rb.getString("notificationUpcomingAppointmentMessage") + "\n" + rb.getString("lblTitle")
                            + ": " + appointment.getTitle() + "\n" + rb.getString("lblDescription") + ": " + appointment.getDescription() +
                            "\n" + rb.getString("lblLocation") + ": " + appointment.getLocation() + "\n" + rb.getString("lblContact") +
                            ": " + appointment.getContact() + "\n" + rb.getString("lblUrl") + ": " + appointment.getUrl() + "\n" +
                            rb.getString("lblDate") + ": " + appointment.getDateString() + "\n" + rb.getString("lblStartTime") + ": " +
                            appointment.getStartString() + "\n" + rb.getString("lblEndTime") + ": " + appointment.getEndString());
                    alert.showAndWait();
                }
            }
            // Increment openCount so notifications won't be shown again this session
            openCount++;
        }
    }


    // Update customerRoster after database change
    public static void updateCustomerRoster() {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            // Retrieve customerRoster and clear
            ObservableList<Customer> customerRoster = CustomerRoster.getCustomerRoster();
            customerRoster.clear();
            // Create list of customerId's for all active customers
            ResultSet customerIdResultSet = stmt.executeQuery("SELECT customerId FROM customer WHERE active = 1");
            ArrayList<Integer> customerIdList = new ArrayList<>();
            while (customerIdResultSet.next()) {
                customerIdList.add(customerIdResultSet.getInt(1));
            }
            // Create Customer object for each customerId in list and add Customer to customerRoster list
            for (int customerId : customerIdList) {
                // Create new Customer object
                Customer customer = new Customer();
                // Retrieve customer information from database and set to Customer object
                ResultSet customerResultSet = stmt.executeQuery("SELECT customerName, active, addressId FROM customer WHERE customerId = " + customerId);
                customerResultSet.next();
                String customerName = customerResultSet.getString(1);
                int active = customerResultSet.getInt(2);
                int addressId = customerResultSet.getInt(3);
                customer.setCustomerId(customerId);
                customer.setCustomerName(customerName);
                customer.setActive(active);
                customer.setAddressId(addressId);
                // Retrieve address information from database and set to Customer object
                ResultSet addressResultSet = stmt.executeQuery("SELECT address, address2, postalCode, phone, cityId FROM address WHERE addressId = " + addressId);
                addressResultSet.next();
                String address = addressResultSet.getString(1);
                String address2 = addressResultSet.getString(2);
                String postalCode = addressResultSet.getString(3);
                String phone = addressResultSet.getString(4);
                int cityId = addressResultSet.getInt(5);
                customer.setAddress(address);
                customer.setAddress2(address2);
                customer.setPostalCode(postalCode);
                customer.setPhone(phone);
                customer.setCityId(cityId);
                // Retrieve city information from database and set to Customer object
                ResultSet cityResultSet = stmt.executeQuery("SELECT city, countryId FROM city WHERE cityId = " + cityId);
                cityResultSet.next();
                String city = cityResultSet.getString(1);
                int countryId = cityResultSet.getInt(2);
                customer.setCity(city);
                customer.setCountryId(countryId);
                // Retrieve country information from database and set to Customer object
                ResultSet countryResultSet = stmt.executeQuery("SELECT country FROM country WHERE countryId = " + countryId);
                countryResultSet.next();
                String country = countryResultSet.getString(1);
                customer.setCountry(country);
                // Add new Customer object to customerRoster
                customerRoster.add(customer);
            }
        }
        catch (SQLException e) {
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorConnectingToDatabase"));
            alert.setContentText(rb.getString("errorConnectingToDatabaseMessage"));
            alert.show();
        }
    }


    // Add customer to database if entry does not already exist
    public static void addNewCustomer(String customerName, String address, String address2,
                                   String city, String country, String postalCode, String phone) {
        // Get country, city and address Id's
        try {
            int countryId = calculateCountryId(country);
            int cityId = calculateCityId(city, countryId);
            int addressId = calculateAddressId(address, address2, postalCode, phone, cityId);
            // Check if customer is new or already exists in database
            if (checkIfCustomerExists(customerName, addressId)) {
                // Try-with-resources block for database connection
                try (Connection conn = DriverManager.getConnection(url, user, pass);
                     Statement stmt = conn.createStatement()) {
                    ResultSet activeResultSet = stmt.executeQuery("SELECT active FROM customer WHERE " +
                            "customerName = '" + customerName + "' AND addressId = " + addressId);
                    activeResultSet.next();
                    int active = activeResultSet.getInt(1);
                    // Check if existing customer is already active or set to inactive
                    if (active == 1) {
                        // Show alert if customer is already active
                        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(rb.getString("error"));
                        alert.setHeaderText(rb.getString("errorAddingCustomer"));
                        alert.setContentText(rb.getString("errorCustomerAlreadyExists"));
                        alert.showAndWait();
                    } else if (active == 0) {
                        // Set customer to active if they are currently inactive
                        setCustomerToActive(customerName, addressId);
                    }
                }
            }
            // Add new customer entry if customer does not already exist
            else {
                addCustomer(customerName, addressId);
            }
        }
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(rb.getString("errorRequiresDatabase"));
            alert.showAndWait();
        }
    }
    // Return countryId if entry already exists. If no matching entry, create new entry and return new countryId.
    public static int calculateCountryId(String country) {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet countryIdCheck = stmt.executeQuery("SELECT countryId FROM country WHERE country = '" + country + "'");
            // Check if entry already exists and return countryId if it does
            if (countryIdCheck.next()) {
                int countryId = countryIdCheck.getInt(1);
                countryIdCheck.close();
                return countryId;
            }
            else {
                countryIdCheck.close();
                int countryId;
                ResultSet allCountryId = stmt.executeQuery("SELECT countryId FROM country ORDER BY countryId");
                // Check last countryId value and add one to it for new countryId value
                if (allCountryId.last()) {
                    countryId = allCountryId.getInt(1) + 1;
                    allCountryId.close();
                }
                // If no values present, set countryId to beginning value of 1
                else {
                    allCountryId.close();
                    countryId = 1;
                }
                // Create new entry with new countryId value
                stmt.executeUpdate("INSERT INTO country VALUES (" + countryId + ", '" + country + "', CURRENT_DATE, " +
                        "'" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return countryId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    // Return cityId if entry already exists. If no matching entry, create new entry and return new cityId.
    public static int calculateCityId(String city, int countryId) {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet cityIdCheck = stmt.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "' AND countryid = " + countryId);
            // Check if entry already exists and return cityId if it does
            if (cityIdCheck.next()) {
                int cityId = cityIdCheck.getInt(1);
                cityIdCheck.close();
                return cityId;
            }
            else {
                cityIdCheck.close();
                int cityId;
                ResultSet allCityId = stmt.executeQuery("SELECT cityId FROM city ORDER BY cityId");
                // Check last cityId value and add one to it for new cityId value
                if (allCityId.last()) {
                    cityId = allCityId.getInt(1) + 1;
                    allCityId.close();
                }
                // If no values present, set countryId to beginning value of 1
                else {
                    allCityId.close();
                    cityId = 1;
                }
                // Create new entry with new cityId value
                stmt.executeUpdate("INSERT INTO city VALUES (" + cityId + ", '" + city + "', " + countryId + ", CURRENT_DATE, " +
                        "'" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return cityId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    // Return addressId if entry already exists. If no matching entry, create new entry and return new addressId.
    public static int calculateAddressId(String address, String address2, String postalCode, String phone, int cityId) {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet addressIdCheck = stmt.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "' AND " +
                    "address2 = '" + address2 + "' AND postalCode = '" + postalCode + "' AND phone = '" + phone + "' AND cityId = " + cityId);
            // Check if entry already exists and return addressId if it does
            if (addressIdCheck.next()) {
                int addressId = addressIdCheck.getInt(1);
                addressIdCheck.close();
                return addressId;
            }
            else {
                addressIdCheck.close();
                int addressId;
                ResultSet allAddressId = stmt.executeQuery("SELECT addressId FROM address ORDER BY addressId");
                // Check last addressId value and add one to it for new addressId value
                if (allAddressId.last()) {
                    addressId = allAddressId.getInt(1) + 1;
                    allAddressId.close();
                }
                // If no values present, set addressId to beginning value of 1
                else {
                    allAddressId.close();
                    addressId = 1;
                }
                // Create new entry with new addressId value
                stmt.executeUpdate("INSERT INTO address VALUES (" + addressId + ", '" + address + "', '" +address2 + "', " + cityId + ", " +
                        "'" + postalCode + "', '" + phone + "', CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return addressId;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    // Check if customer entry already exists
    private static boolean checkIfCustomerExists(String customerName, int addressId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet customerIdCheck = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "' " +
                    "AND addressId = " + addressId);
            // Check if entry already exists and return boolean
            if (customerIdCheck.next()) {
                customerIdCheck.close();
                return true;
            }
            else {
                customerIdCheck.close();
                return false;
            }
        }
    }
    // Change customer from "inactive" to "active"
    public static void setCustomerToActive(String customerName, int addressId) {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            // Show confirmation box to confirm setting customer to active
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(rb.getString("errorSetToActive"));
            Optional<ButtonType> result = alert.showAndWait();
            // If 'OK' is clicked, set customer to active
            if (result.get() == ButtonType.OK) {
                stmt.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, " +
                        "lastUpdateBy = '" + currentUser + "' WHERE customerName = '" + customerName + "' AND addressId = " + addressId);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Create new customer entry
    private static void addCustomer(String customerName, int addressId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet allCustomerId = stmt.executeQuery("SELECT customerId FROM customer ORDER BY customerId");
            int customerId;
            // Check last customerId value and add one to it for new customerId value
            if (allCustomerId.last()) {
                customerId = allCustomerId.getInt(1) + 1;
                allCustomerId.close();
            }
            // If no values present, set customerId to beginning value of 1
            else {
                allCustomerId.close();
                customerId = 1;
            }
            // Create new entry with new customerId value
            stmt.executeUpdate("INSERT INTO customer VALUES (" + customerId + ", '" + customerName + "', " + addressId + ", 1, " +
                    "CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
        }
    }


    // Modify existing customer entry
    public static int modifyCustomer(int customerId, String customerName, String address, String address2,
                                      String city, String country, String postalCode, String phone) {
        try {
            // Find customer's country, city and addressId's
            int countryId = calculateCountryId(country);
            int cityId = calculateCityId(city, countryId);
            int addressId = calculateAddressId(address, address2, postalCode, phone, cityId);
            // Check if customer already exists in the database
            if (checkIfCustomerExists(customerName, addressId)) {
                // If customer already exists, get customerId and use customerId to get and return their active status
                int existingCustomerId = getCustomerId(customerName, addressId);
                int activeStatus = getActiveStatus(existingCustomerId);
                return activeStatus;
            } else {
                // If customer does not already exist, update customer entry in database and clean the database of unused entries
                updateCustomer(customerId, customerName, addressId);
                cleanDatabase();
                return -1;
            }
        }
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(rb.getString("errorRequiresDatabase"));
            alert.showAndWait();
            return -1;
        }
    }
    // Get customerId from customerName and addressId
    private static int getCustomerId(String customerName, int addressId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet customerIdResultSet = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "' AND addressId = " + addressId);
            customerIdResultSet.next();
            int customerId = customerIdResultSet.getInt(1);
            return customerId;
        }
    }
    // Get active status of customer entry
    private static int getActiveStatus(int customerId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet activeResultSet = stmt.executeQuery("SELECT active FROM customer WHERE customerId = " + customerId);
            activeResultSet.next();
            int active = activeResultSet.getInt(1);
            return active;
        }
    }
    // Update customer entry if unique
    private static void updateCustomer(int customerId, String customerName, int addressId) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE customer SET customerName = '" + customerName + "', addressId = " + addressId + ", " +
                    "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE customerId = " + customerId);
        }
    }


    // Set customer to "inactive" and hide them in customer list
    public static void setCustomerToInactive(Customer customerToRemove) {
        int customerId = customerToRemove.getCustomerId();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Show alert to confirm the removal of customer
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmRemove"));
        alert.setHeaderText(rb.getString("confirmRemovingCustomer"));
        alert.setContentText(rb.getString("confirmRemovingCustomerMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // Check if OK button was clicked and set customer entry to inactive if it was
        if (result.get() == ButtonType.OK) {
            // Try-with-resources block for database connection
            try (Connection conn = DriverManager.getConnection(url,user,pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = " + customerId);
            }
            catch (SQLException e) {
                // Create alert notifying user that a database connection is needed for this function
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(rb.getString("error"));
                alert2.setHeaderText(rb.getString("errorModifyingCustomer"));
                alert2.setContentText(rb.getString("errorRequiresDatabase"));
                alert2.showAndWait();
            }
            // Update customerRoster, which will remove the inactive customer from the roster
            updateCustomerRoster();
        }
    }


    // Updates appointmentList with all current appointments that have yet to occur
    public static void updateAppointmentList() {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            // Retrieve appointmentList and clear
            ObservableList<Appointment> appointmentList = AppointmentList.getAppointmentList();
            appointmentList.clear();
            // Create list of appointmentId's for all appointments that are in the future
            ResultSet appointmentResultSet = stmt.executeQuery("SELECT appointmentId FROM appointment WHERE start >= CURRENT_TIMESTAMP");
            ArrayList<Integer> appointmentIdList = new ArrayList<>();
            while(appointmentResultSet.next()) {
                appointmentIdList.add(appointmentResultSet.getInt(1));
            }
            // Create Appointment object for each appointmentId in list and add Appointment to appointmentList
            for (int appointmentId : appointmentIdList) {
                // Retrieve appointment info from database
                appointmentResultSet = stmt.executeQuery("SELECT customerId, title, description, location, contact, url, start, end, createdBy FROM appointment WHERE appointmentId = " + appointmentId);
                appointmentResultSet.next();
                int customerId = appointmentResultSet.getInt(1);
                String title = appointmentResultSet.getString(2);
                String description = appointmentResultSet.getString(3);
                String location = appointmentResultSet.getString(4);
                String contact = appointmentResultSet.getString(5);
                String url = appointmentResultSet.getString(6);
                Timestamp startTimestamp = appointmentResultSet.getTimestamp(7);
                Timestamp endTimestamp = appointmentResultSet.getTimestamp(8);
                String createdBy = appointmentResultSet.getString(9);
                // Transform startTimestamp and endTimestamp to ZonedDateTimes
                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date startDate = utcFormat.parse(startTimestamp.toString());
                java.util.Date endDate = utcFormat.parse(endTimestamp.toString());
                // Assign appointment info to new Appointment object
                Appointment appointment = new Appointment(appointmentId, customerId, title, description, location, contact, url, startTimestamp, endTimestamp, startDate, endDate, createdBy);
                // Add new Appointment object to appointmentList
                appointmentList.add(appointment);
            }
        }
        catch (Exception e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorRequiresDatabase"));
            alert.showAndWait();
        }
    }


    // Add appointment to database if entry does not already exist
    public static boolean addNewAppointment(Customer customer, String title, String description, String location,
                                         String contact, String url, ZonedDateTime startUTC, ZonedDateTime endUTC) {
        // Transform ZonedDateTimes to Timestamps
        String startUTCString = startUTC.toString();
        startUTCString = startUTCString.substring(0,10) + " " + startUTCString.substring(11,16) + ":00";
        Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
        String endUTCString = endUTC.toString();
        endUTCString = endUTCString.substring(0,10) + " " + endUTCString.substring(11,16) + ":00";
        Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
        // Check if appointment overlaps with existing appointments. Show alert if it does.
        if (doesAppointmentOverlap(startTimestamp, endTimestamp)) {
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorAppointmentOverlaps"));
            alert.showAndWait();
            return false;
        }
        // Add new appointment entry if it does not overlap with others
        else {
            int customerId = customer.getCustomerId();
            addAppointment(customerId, title, description, location, contact, url, startTimestamp, endTimestamp);
            return true;
        }
    }
    // Check if new appointment overlaps with any existing appointments and return true if it does
    private static boolean doesAppointmentOverlap(Timestamp startTimestamp, Timestamp endTimestamp) {
        updateAppointmentList();
        ObservableList<Appointment> appointmentList = AppointmentList.getAppointmentList();
        for (Appointment appointment: appointmentList) {
            Timestamp existingStartTimestamp = appointment.getStartTimestamp();
            Timestamp existingEndTimestamp = appointment.getEndTimestamp();
            // Check various scenarios for where overlap would occur and return true if any occur
            if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        // If none of the above scenarios occur, return false
        return false;
    }
    // Create new appointment entry
    private static void addAppointment(int customerId, String title, String description, String location,
                                       String contact, String url, Timestamp startTimestamp, Timestamp endTimestamp) {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(DBManager.url,user,pass);
             Statement stmt = conn.createStatement()) {
            ResultSet allAppointmentId = stmt.executeQuery("SELECT appointmentId FROM appointment ORDER BY appointmentId");
            int appointmentId;
            // Check last appointmentId value and add one to it for new appointmentId value
            if (allAppointmentId.last()) {
                appointmentId = allAppointmentId.getInt(1) + 1;
                allAppointmentId.close();
            }
            else {
                allAppointmentId.close();
                appointmentId = 1;
            }
            // Create new entry with appointmentId value
            stmt.executeUpdate("INSERT INTO appointment VALUES (" + appointmentId +", " + customerId + ", '" + title + "', '" +
                    description + "', '" + location + "', '" + contact + "', '" + url + "', '" + startTimestamp + "', '" + endTimestamp + "', " +
                    "CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
        }
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorRequiresDatabase"));
            alert.showAndWait();
        }
    }


    // Modify existing appointment entry
    public static boolean modifyAppointment(int appointmentId, Customer customer, String title, String description, String location,
                                         String contact, String url, ZonedDateTime startUTC, ZonedDateTime endUTC) {
        try {
            // Transform ZonedDateTimes to Timestamps
            String startUTCString = startUTC.toString();
            startUTCString = startUTCString.substring(0, 10) + " " + startUTCString.substring(11, 16) + ":00";
            Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
            String endUTCString = endUTC.toString();
            endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
            Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
            // Check if appointment overlaps with other existing appointments. Show alert and return false if it does.
            if (doesAppointmentOverlapOthers(startTimestamp, endTimestamp)) {
                ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorModifyingAppointment"));
                alert.setContentText(rb.getString("errorAppointmentOverlaps"));
                alert.showAndWait();
                return false;
            } else {
                // If overlap doesn't occur, update appointment entry and return true
                int customerId = customer.getCustomerId();
                updateAppointment(appointmentId, customerId, title, description, location, contact, url, startTimestamp, endTimestamp);
                return true;
            }
        }
        catch (Exception e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorRequiresDatabase"));
            alert.showAndWait();
            return false;
        }
    }
    // Update appointment entry if times are valid
    private static void updateAppointment(int appointmentId, int customerId, String title, String description, String location,
                                          String contact, String url, Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(DBManager.url,user,pass);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE appointment SET customerId = " + customerId + ", title = '" + title + "', description = '" + description + "', " +
                    "location = '" + location + "', contact = '" + contact + "', url = '" + url + "', start = '" + startTimestamp + "', end = '" + endTimestamp + "', " +
                    "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE appointmentId = " + appointmentId);
        }
    }
    // Check if new appointment overlaps with any other existing appointments and return true if it does
    private static boolean doesAppointmentOverlapOthers(Timestamp startTimestamp, Timestamp endTimestamp) throws SQLException, ParseException {
        int appointmentIndexToRemove = AppointmentSummaryController.getAppointmentIndexToModify();
        ObservableList<Appointment> appointmentList = AppointmentList.getAppointmentList();
        appointmentList.remove(appointmentIndexToRemove);
        for (Appointment appointment: appointmentList) {
            Timestamp existingStartTimestamp = appointment.getStartTimestamp();
            Timestamp existingEndTimestamp = appointment.getEndTimestamp();
            // Check various scenarios for where overlap would occur and return true if any occur
            if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                return true;
            }
            if (startTimestamp.equals(existingStartTimestamp)) {
                return true;
            }
            if (endTimestamp.equals(existingEndTimestamp)) {
                return true;
            }
        }
        // If none of the above scenarios occur, return false
        return false;
    }


    // Delete appointment entry from database by appointmentId
    public static void deleteAppointment(Appointment appointmentToDelete) {
        int appointmentId = appointmentToDelete.getAppointmentId();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Show alert to confirm deleting appointment entry
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmDelete"));
        alert.setHeaderText(rb.getString("confirmDeleteAppointment"));
        alert.setContentText(rb.getString("confirmDeleteAppointmentMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // Check if OK button was clicked and delete appointment entry if it was
        if (result.get() == ButtonType.OK) {
            // Try-with-resources block for database connection
            try (Connection conn = DriverManager.getConnection(url,user,pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM appointment WHERE appointmentId =" + appointmentId);
            }
            catch (Exception e) {
                // Create alert notifying user that a database connection is needed for this function
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(rb.getString("error"));
                alert2.setHeaderText(rb.getString("errorModifyingAppointment"));
                alert2.setContentText(rb.getString("errorRequiresDatabase"));
                alert2.showAndWait();
            }
            // Update appointmentList to remove deleted appointment
            updateAppointmentList();
        }
    }


    // Create report for number of appointment types by month
    public static void generateAppointmentTypeByMonthReport() {
        updateAppointmentList();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Initialize report string
        String report = rb.getString("lblAppointmentTypeByMonthTitle");
        ArrayList<String> monthsWithAppointments = new ArrayList<>();
        // Check year and month of each appointment. Add new year-month combos to ArrayList
        for (Appointment appointment : AppointmentList.getAppointmentList()) {
            Date startDate = appointment.getStartDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String yearMonth = year + "-" + month;
            if (month < 10) {
                yearMonth = year + "-0" + month;
            }
            if (!monthsWithAppointments.contains(yearMonth)) {
                monthsWithAppointments.add(yearMonth);
            }
        }
        // Sort year-months
        Collections.sort(monthsWithAppointments);
        for (String yearMonth : monthsWithAppointments) {
            // Get year and month values again
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(5,7));
            // Initialize typeCount int
            int typeCount = 0;
            ArrayList<String> descriptions = new ArrayList<>();
            for (Appointment appointment : AppointmentList.getAppointmentList()) {
                // Get appointment start date
                Date startDate = appointment.getStartDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                // Get appointment year and month values
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                // If year and month match, get appointment description
                if (year == appointmentYear && month == appointmentMonth) {
                    String description = appointment.getDescription();
                    // If appointment description is not in ArrayList, add it and increment typeCount
                    if (!descriptions.contains(description)) {
                        descriptions.add(description);
                        typeCount++;
                    }
                }
            }
            // Add year-month to report
            report = report + yearMonth + ": " + typeCount + "\n";
            report = report + rb.getString("lblTypes");
            // Add each type description to report
            for (String description : descriptions) {
                report = report + " " + description + ",";
            }
            // Remove trailing comma from type descriptions
            report = report.substring(0, report.length()-1);
            // Add paragraph break between months
            report = report + "\n \n";
        }
        // Print report to AppointmentTypeByMonth.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("AppointmentTypeByMonth.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Create report for each consultant's (user's) schedule
    public static void generateScheduleForConsultants() {
        updateAppointmentList();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Initialize report string
        String report = rb.getString("lblConsultantScheduleTitle");
        ArrayList<String> consultantsWithAppointments = new ArrayList<>();
        // Check createdBy of each appointment. Add new createdBy's to ArrayList
        for (Appointment appointment : AppointmentList.getAppointmentList()) {
            String consultant = appointment.getCreatedBy();
            if (!consultantsWithAppointments.contains(consultant)) {
                consultantsWithAppointments.add(consultant);
            }
        }
        // Sort consultants
        Collections.sort(consultantsWithAppointments);
        for (String consultant : consultantsWithAppointments) {
            // Add consultant name to report
            report = report + consultant + ": \n";
            for (Appointment appointment : AppointmentList.getAppointmentList()) {
                String appointmentConsultant = appointment.getCreatedBy();
                // Check if appointment createdBy matches consultant
                if (consultant.equals(appointmentConsultant)) {
                    // Get appointment date and title
                    String date = appointment.getDateString();
                    String title = appointment.getTitle();
                    Date startDate = appointment.getStartDate();
                    // Modify times to AM/PM format
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    // Get timezone
                    String timeZone = startDate.toString().substring(20,23);
                    // Add appointment info to report
                    report = report + date + ": " + title + rb.getString("lblFrom") + startTime + rb.getString("lblTo") +
                            endTime + " " + timeZone + ". \n";
                }
            }
            // Add paragraph break between consultants
            report = report + "\n \n";
        }
        // Print report to ScheduleByConsultant.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByConsultant.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Create report for each customer's upcoming meetings
    public static void generateUpcomingMeetingsByCustomer() {
        updateAppointmentList();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Initialize report string
        String report = rb.getString("lblCustomerScheduleTitle");
        ArrayList<Integer> customerIdsWithAppointments = new ArrayList<>();
        // Check customerId of each appointment. Add new customerId's to ArrayList
        for (Appointment appointment : AppointmentList.getAppointmentList()) {
            int customerId = appointment.getCustomerId();
            if (!customerIdsWithAppointments.contains(customerId)) {
                customerIdsWithAppointments.add(customerId);
            }
        }
        // Sort customerId's
        Collections.sort(customerIdsWithAppointments);
        updateCustomerRoster();
        for (int customerId : customerIdsWithAppointments) {
            for (Customer customer : CustomerRoster.getCustomerRoster()) {
                // Go through customerRoster and find match for customerId
                int customerIdToCheck = customer.getCustomerId();
                if (customerId == customerIdToCheck) {
                    // Add customer name to report
                    report = report + customer.getCustomerName() + ": \n";
                }
            }
            for (Appointment appointment : AppointmentList.getAppointmentList()) {
                int appointmentCustomerId = appointment.getCustomerId();
                // Check if appointment's customerId matches customer
                if (customerId == appointmentCustomerId) {
                    // Get appointment date and description
                    String date = appointment.getDateString();
                    String description = appointment.getDescription();
                    Date startDate = appointment.getStartDate();
                    // Modify times to AM/PM format
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    // Get timezone
                    String timeZone = startDate.toString().substring(20,23);
                    // Add appointment info to report
                    report = report + date + ": " + description + rb.getString("lblFrom") + startTime + rb.getString("lblTo") +
                            endTime + " " + timeZone + ". \n";
                }
            }
            // Add paragraph break between customers
            report = report + "\n \n";
        }
        // Print report to ScheduleByCustomer.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByCustomer.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Clean up database entries that are no longer associated with any customers
    private static void cleanDatabase() {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            // Create list of addressId's used in Customer table
            ResultSet addressIdResultSet = stmt.executeQuery("SELECT DISTINCT addressId FROM customer ORDER BY addressId");
            ArrayList<Integer> addressIdListFromCustomer = new ArrayList<>();
            while (addressIdResultSet.next()) {
                addressIdListFromCustomer.add(addressIdResultSet.getInt(1));
            }
            // Create list of addressId's used in Address table
            addressIdResultSet = stmt.executeQuery("SELECT DISTINCT addressId FROM address ORDER BY addressId");
            ArrayList<Integer> addressIdListFromAddress = new ArrayList<>();
            while (addressIdResultSet.next()) {
                addressIdListFromAddress.add(addressIdResultSet.getInt(1));
            }
            // Create list of addressId's that exist in Address table but are not used in Customer table
            for (int i = 0; i < addressIdListFromCustomer.size(); i++) {
                for (int j = 0; j < addressIdListFromAddress.size(); j++) {
                    if (addressIdListFromCustomer.get(i) == addressIdListFromAddress.get(j)) {
                        addressIdListFromAddress.remove(j);
                        j--;
                    }
                }
            }
            // Delete Address table entries by remaining addressId's, if any remain
            if (addressIdListFromAddress.isEmpty()) {}
            else {
                for (int addressId : addressIdListFromAddress) {
                    stmt.executeUpdate("DELETE FROM address WHERE addressId = " + addressId);
                }
            }

            // Create list of cityId's used in Address table
            ResultSet cityIdResultSet = stmt.executeQuery("SELECT DISTINCT cityId FROM address ORDER BY cityId");
            ArrayList<Integer> cityIdListFromAddress = new ArrayList<>();
            while (cityIdResultSet.next()) {
                cityIdListFromAddress.add(cityIdResultSet.getInt(1));
            }
            // Create list of cityId's used in City table
            cityIdResultSet = stmt.executeQuery("SELECT DISTINCT cityId FROM city ORDER BY cityId");
            ArrayList<Integer> cityIdListFromCity = new ArrayList<>();
            while (cityIdResultSet.next()) {
                cityIdListFromCity.add(cityIdResultSet.getInt(1));
            }
            // Create list of cityId's that exist in City table but are not used in Address table
            for (int i = 0; i < cityIdListFromAddress.size(); i++) {
                for (int j = 0; j < cityIdListFromCity.size(); j++) {
                    if (cityIdListFromAddress.get(i) == cityIdListFromCity.get(j)) {
                        cityIdListFromCity.remove(j);
                        j--;
                    }
                }
            }
            // Delete City table entries by remaining cityId's, if any remain
            if (cityIdListFromCity.isEmpty()) {}
            else {
                for (int cityId : cityIdListFromCity) {
                    stmt.executeUpdate("DELETE FROM city WHERE cityId = " + cityId);
                }
            }

            // Create list of countryId's used in City table
            ResultSet countryIdResultSet = stmt.executeQuery("SELECT DISTINCT countryId FROM city ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCity = new ArrayList<>();
            while (countryIdResultSet.next()) {
                countryIdListFromCity.add(countryIdResultSet.getInt(1));
            }
            // Create list of countryId's used in Country table
            countryIdResultSet = stmt.executeQuery("SELECT DISTINCT countryId FROM country ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCountry = new ArrayList<>();
            while (countryIdResultSet.next()) {
                countryIdListFromCountry.add(countryIdResultSet.getInt(1));
            }
            // Create list of countryId's that exist in Country table but are not used in City table
            for (int i = 0; i < countryIdListFromCity.size(); i++) {
                for (int j = 0; j < countryIdListFromCountry.size(); j++) {
                    if (countryIdListFromCity.get(i) == countryIdListFromCountry.get(j)) {
                        countryIdListFromCountry.remove(j);
                        j--;
                    }
                }
            }
            // Delete Country table entries by remaining countryId's, if any remain
            if (countryIdListFromCountry.isEmpty()) {}
            else {
                for (int countryId : countryIdListFromCountry) {
                    stmt.executeUpdate("DELETE FROM country WHERE countryId = " + countryId);
                }
            }
        }
        catch (SQLException e) {
            // Create alert notifying user that a database connection is needed for this function
            ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorRequiresDatabase"));
            alert.showAndWait();
        }
    }
}
