package com.alexstrog.model;

public class Car {
    private Long id;
    private String vendor;
    private String vehicleModel;
    private int horsePower;

    public Car() {

    }

    public Car(String vendor, String vehicleModel, int horsePower) {
        this.vendor = vendor;
        this.vehicleModel = vehicleModel;
        this.horsePower = horsePower;
    }

    public Car(Long id, String vendor, String vehicleModel, int horsePower) {
        this.id = id;
        this.vendor = vendor;
        this.vehicleModel = vehicleModel;
        this.horsePower = horsePower;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", vendor='" + vendor + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", horsePower=" + horsePower +
                '}';
    }
}
