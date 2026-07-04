package Exams.models;

public class SmartLight extends Device{
    private int brightness;
    private String color;

    public void setBrightness(int brightness) {
        if (brightness <= 100 && brightness >= 0){
            this.brightness = brightness;
        }else{
            throw new IllegalArgumentException("incorrect brightness");
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBrightness() {
        return brightness;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getDetails() {
        return color + brightness;
    }

}
