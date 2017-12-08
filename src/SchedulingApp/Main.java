package SchedulingApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.TimeZone;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Locale.setDefault(new Locale.Builder().setLanguage("fr").build());
        //TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        primaryStage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("ViewController/LogIn.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
