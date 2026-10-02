package retake6.models;

import retake6.interfaces.Operatable;

public abstract class FactoryEquipment implements Operatable {
    protected String id;
    protected String name;
    protected boolean status;
    protected double temperature;

    public FactoryEquipment(String id, String name, boolean status, double temperature) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public abstract String getDetails();
    @Override
    public void startWork(){
        status = true;
    }
    @Override
    public void stopWork(){
        status = false;
    }
    public boolean isWorking(){
        return status;
    }
}
