package SchedulingApp.ViewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static SchedulingApp.Model.DBManager.checkLogInCredentials;


public class LogInController {

    @FXML
    private Label lblLogInTitle;
    @FXML
    private Label lblLogInUsername;
    @FXML
    private TextField txtLogInUsername;
    @FXML
    private Label lblLogInPassword;
    @FXML
    private TextField txtLogInPassword;
    @FXML
    private Label lblLogInErrorMessage;
    @FXML
    private Button btnLogInSubmit;

    // Signal as to whether a database error message needs to be shown
    public static int databaseError = 0;

    // Set labels to local language (default is English)
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("LogIn", Locale.getDefault());
        lblLogInTitle.setText(rb.getString("lblTitle"));
        lblLogInUsername.setText(rb.getString("lblUsername"));
        lblLogInPassword.setText(rb.getString("lblPassword"));
        btnLogInSubmit.setText(rb.getString("btnSubmit"));
    }

    // Submit log-in credentials to be checked
    @FXML
    private void submitLogIn(ActionEvent event) {
        // Retrieves user's inputs and clears password field
        String userName = txtLogInUsername.getText();
        String password = txtLogInPassword.getText();
        txtLogInPassword.setText("");
        ResourceBundle rb = ResourceBundle.getBundle("LogIn", Locale.getDefault());
        // Returns error message in window if either username or password fields are blank
        if (userName.equals("") || password.equals("")) {
            lblLogInErrorMessage.setText(rb.getString("lblNoUserPass"));
            return;
        }
        // Check credentials against database
        boolean correctCredentials = checkLogInCredentials(userName, password);
        // Check if credentials were correct
        if (correctCredentials) {
            try {
                // Show main screen
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
        // Check if databaseError has been set
        else if (databaseError > 0) {
            // Show connection error message
            lblLogInErrorMessage.setText(rb.getString("lblConnectionError"));
        }
        else {
            // Show message saying username/password were incorrect
            lblLogInErrorMessage.setText(rb.getString("lblWrongUserPass"));
        }
    }

    // Increment databaseError signal
    @FXML
    public static void incrementDatabaseError() {
        databaseError++;
    }

    // Initialize screen elements
    @FXML
    public void initialize() {
        // Set local language
        setLanguage();
        // Assign action to button
        btnLogInSubmit.setOnAction(event -> submitLogIn(event));
    }
}
