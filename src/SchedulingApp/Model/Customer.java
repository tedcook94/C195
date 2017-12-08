package SchedulingApp.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class Customer {

    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final IntegerProperty active;
    private final IntegerProperty addressId;
    private final StringProperty address;
    private final StringProperty address2;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final IntegerProperty cityId;
    private final StringProperty city;
    private final IntegerProperty countryId;
    private final StringProperty country;

    // Constructor
    public Customer() {
        customerId = new SimpleIntegerProperty();
        customerName = new SimpleStringProperty();
        active = new SimpleIntegerProperty();
        addressId = new SimpleIntegerProperty();
        address = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        postalCode = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        cityId = new SimpleIntegerProperty();
        city = new SimpleStringProperty();
        countryId = new SimpleIntegerProperty();
        country = new SimpleStringProperty();
    }

    // Setters
    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public void setActive(int active) {
        this.active.set(active);
    }

    public void setAddressId(int addressId) {
        this.addressId.set(addressId);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public void setCityId(int cityId) {
        this.cityId.set(cityId);
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public void setCountryId(int countryId) {
        this.countryId.set(countryId);
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    // Getters
    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public IntegerProperty activeProperty() {
        return active;
    }

    public IntegerProperty addressIdProperty() {
        return addressId;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty address2Property() {
        return address2;
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public IntegerProperty cityIdProperty() {
        return cityId;
    }

    public StringProperty cityProperty() {
        return city;
    }

    public IntegerProperty countryIdProperty() {
        return countryId;
    }

    public StringProperty countryProperty() {
        return country;
    }

    public int getCustomerId() {
        return this.customerId.get();
    }

    public String getCustomerName() {
        return this.customerName.get();
    }

    public int getActive() {
        return active.get();
    }

    public int getAddressId() {
        return this.addressId.get();
    }

    public String getAddress() {
        return this.address.get();
    }

    public String getAddress2() {
        return this.address2.get();
    }

    public String getPostalCode() {
        return this.postalCode.get();
    }

    public String getPhone() {
        return this.phone.get();
    }

    public int getCityId() {
        return this.cityId.get();
    }

    public String getCity() {
        return this.city.get();
    }

    public int getCountryId() {
        return this.countryId.get();
    }

    public String getCountry() {
        return this.country.get();
    }

    // Validation
    public static String isCustomerValid(String customerName, String address, String city,
                                         String country, String postalCode, String phone) {
        ResourceBundle rb = ResourceBundle.getBundle("Customer", Locale.getDefault());
        String errorMessage = "";
        if (customerName.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorCustomerName");
        }
        if (address.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorAddress");
        }
        if (city.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorCity");
        }
        if (country.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorCountry");
        }
        if (postalCode.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorPostalCode");
        }
        if (phone.length() == 0) {
            errorMessage = errorMessage + rb.getString("errorPhone");
        }
        return errorMessage;
    }
}
