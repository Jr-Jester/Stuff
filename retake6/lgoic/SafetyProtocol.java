package retake6.lgoic;

import retake6.models.FactoryEquipment;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SafetyProtocol <T extends FactoryEquipment>{
    private String protocolName;
    private Predicate<T> condition;
    private Consumer<T> action;
    private Class<T> equipmentClass;

    public SafetyProtocol(String protocolName, Predicate<T> condition, Consumer<T> action, Class<T> equipmentClass) {
        this.protocolName = protocolName;
        this.condition = condition;
        this.action = action;
        this.equipmentClass = equipmentClass;
    }

    public void apply(T equipment){
        if(condition.test(equipment)){
            action.accept(equipment);
        }
    }

    public void check(T equipment){
        if (equipmentClass.isInstance(equipment)) {
            apply(equipmentClass.cast(equipment));
        }
    }
}
