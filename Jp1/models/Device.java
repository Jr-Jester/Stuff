package Exams.models;

import Exams.interfaces.Controllable;

public abstract class Device implements Controllable {
    protected String id;
    protected String name;
    protected boolean status;
    protected double powerConsumption;

    public abstract String getDetails();

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setPowerConsumption(double powerConsumption) {
        this.powerConsumption = powerConsumption;
    }

    public String getName() {
        return name;
    }


    public double getPowerConsumption() {
        return powerConsumption;
    }

    public String getId() {
        return id;
    }

    @Override
    public void turnOn() {
        status = true;
    }

    @Override
    public void turnOf() {
        status = false;
    }

    @Override
    public boolean isOn() {
        return status;
    }
}

