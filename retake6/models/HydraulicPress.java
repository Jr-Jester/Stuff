package retake6.models;

import java.text.Format;

public class HydraulicPress extends FactoryEquipment{
    private double pressure;

    public HydraulicPress(String id, String name, boolean status, double temperature, double pressure) {
        super(id, name, status, temperature);
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    @Override
    public String getDetails() {
        return String.format("id: %s, name: %s, status: %b, temp: %f, pressure: %f", id, name, status, temperature, pressure);
    }
}
