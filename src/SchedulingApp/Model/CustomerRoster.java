package SchedulingApp.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerRoster {

    private static ObservableList<Customer> customerRoster = FXCollections.observableArrayList();

    // Getter for customerRoster
    public static ObservableList<Customer> getCustomerRoster() {
        return customerRoster;
    }
}
