package SchedulingApp.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppointmentList {

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    // Getter for appointmentList
    public static ObservableList<Appointment> getAppointmentList() {
        return appointmentList;
    }
}
