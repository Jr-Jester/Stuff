package Exams.logic;
import Exams.models.Device;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Scenario<T extends Device> {
    protected String name;
    private Predicate<T> condition;
    private Consumer<T> action;
    private Class<T> deviceClass;
    public Scenario (String name, Predicate<T> condition, Consumer<T> action, Class<T> deviceClass){
        this.name = name;
        this.condition = condition;
        this.action = action;
        this.deviceClass = deviceClass;
    }
    public void apply(T device) {
        if (condition.test(device)) {
            action.accept(device);
        }
    }

    public void check(Device device){
        if (deviceClass.isInstance(device)){
            apply(deviceClass.cast(device));
        }
    }
}