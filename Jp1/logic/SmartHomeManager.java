package Exams.logic;
import Exams.models.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SmartHomeManager {
    private Map<String, List<Device>> rooms = new HashMap<>();
    private List<Scenario <? extends Device>> scenarios = new ArrayList<>();
    public void addDevice(String room, Device device){
        rooms.computeIfAbsent(room, k -> new ArrayList<>()).add(device);
    };
    public void addScenario(Scenario scenario){
        scenarios.add(scenario);
    };
    public Device getDeviceById(String id){
        return rooms.values().stream()
                .flatMap(List::stream)
                .filter(device -> device.getId().equals(id))
                .findFirst()
                .orElse(null);
    };
    public void executeAllScenarios(){
        if (scenarios.isEmpty()){
            System.out.println("no scenarios");
            return;
        }
        getAnalyticsStream().forEach(device->
                scenarios.forEach(scenario ->
                        scenario.check(device)));
    };
    public Stream<Device> getAnalyticsStream(){
        return rooms.values().stream().flatMap(List::stream);
    };
}