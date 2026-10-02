package retake6.lgoic;

import retake6.models.FactoryEquipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class FactoryManager {
    public HashMap<String, ArrayList<FactoryEquipment>> workshops = new HashMap<>();
    private ArrayList<SafetyProtocol> protocols = new ArrayList<>();

    public Stream<FactoryEquipment> getAnalyticsStream(){
        return workshops.values().stream().flatMap(List::stream);
    }
    public void addEquipment(String workshopName, FactoryEquipment equipment){
        workshops.computeIfAbsent(workshopName, a -> new ArrayList<FactoryEquipment>()).add(equipment);
    }
    public void addProtocol(SafetyProtocol<? extends FactoryEquipment> protocol){
        protocols.add(protocol);
    }
    public FactoryEquipment getEquipmentById(String id){
        return getAnalyticsStream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }
    public void applyAllProtocols(){
        getAnalyticsStream().forEach(a ->
                protocols.forEach(p-> p.check(a)));
    }
    public void printEq(){
        getAnalyticsStream().forEach(a -> System.out.println(a.getDetails()));
    }
}
