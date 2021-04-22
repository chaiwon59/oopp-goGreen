package client.models;

public class Car {
    private String year;
    private String brand;
    private String model;


    /**
     * Creating a car.
     *
     * @param year  The year the car is build.
     * @param brand The brand of the car.
     * @param model The model of that car.
     */

    public Car(String year, String brand, String model) {
        this.year = year;
        this.brand = brand;
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


}