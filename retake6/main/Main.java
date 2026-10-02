package retake6.main;

import retake5.models.RoboticArm;
import retake6.lgoic.FactoryManager;
import retake6.lgoic.SafetyProtocol;
import retake6.models.FactoryEquipment;
import retake6.models.HydraulicPress;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static FactoryManager manager = new FactoryManager();
    public static void main(String[] args){
        System.out.print("""
                1 - new eq
                2 - launch eq
                3 - phantom protocol
                4 - add new protocol
                5 - apply all protocols
                6 - analysis
                7 - end
                """);
        int input = Integer.parseInt(scanner.nextLine());
        while (input != 7){
            switch(input){
                case(1):{
                    addEquipment(manager);
                }break;
                case(2):{
                    launchEquipment(manager);
                }break;
                case(3): {
                    phantomReset(manager);
                }break;
                case(4):{
                    newProtocol(manager);
                }break;
                case(5):{
                    applyAllProtocols(manager);
                }break;
                case(6):{
                    analyzeEq8(manager);
                }
            }
            System.out.print("""
                1 - new eq
                2 - launch eq
                3 - phantom protocol
                4 - add new protocol
                5 - apply all protocols
                6 - analysis
                7 - end
                """);
            input = Integer.parseInt(scanner.nextLine());
        }
    }
    public static void addEquipment(FactoryManager manager){
        System.out.println("1 - arm, 2 - press, 3 - conveyor");
        int equipment = Integer.parseInt(scanner.nextLine());
        System.out.print("id: ");
        String id = scanner.nextLine();
        System.out.print("name: ");
        String name = scanner.nextLine();
        System.out.print("workshop name: ");
        String workshopName = scanner.nextLine();
        switch(equipment){
            case(2):{
                HydraulicPress press = new HydraulicPress(id, name, false, 200.0, 200.0);
                manager.addEquipment(workshopName, press);
            }
        }
        manager.printEq();
    }

    public static void launchEquipment(FactoryManager manager){
        System.out.print("id: ");
        String id = scanner.nextLine();
        FactoryEquipment equipment = manager.getEquipmentById(id);
        if (equipment.isWorking()){
            equipment.stopWork();
        }else{
            equipment.startWork();
        }
        manager.printEq();
    }

    public static void phantomReset(FactoryManager manager){
        SafetyProtocol<?extends FactoryEquipment> protocol = new SafetyProtocol<>("phantomReset",
                a -> (!a.isWorking()) && (a.getTemperature() > 20.0),
                a -> a.setTemperature(20.0), FactoryEquipment.class);
        manager.addProtocol(protocol);
    }

    public static void newProtocol(FactoryManager manager){
        SafetyProtocol<?extends FactoryEquipment> protocol = new SafetyProtocol<>("phantomReset",
                a -> (!a.isWorking()) && (a.getPressure() > 0.0),
                a -> a.setTemperature(0.0), HydraulicPress.class);
        manager.addProtocol(protocol);
    }
    public static void applyAllProtocols(FactoryManager manager){
        manager.applyAllProtocols();
        manager.printEq();
    }
    public static boolean analyzeEq(FactoryManager manager){
        return manager.getAnalyticsStream().anyMatch(a -> a.getTemperature() > 15.0);
    }
    public static void analyzeEq2(FactoryManager manager){
        manager.getAnalyticsStream().sorted(Comparator.comparingDouble(FactoryEquipment::getTemperature).reversed()).limit(3);
    }
    public static void analyzeEq3(FactoryManager manager){
        System.out.println(manager.getAnalyticsStream().filter(a -> a.isWorking())
                .mapToDouble(FactoryEquipment::getTemperature).sum());
    }
    public static void analyzeEq4(FactoryManager manager){
        System.out.println(manager.workshops.keySet().stream().filter(workshopName -> manager.workshops.get(workshopName).stream()
                .anyMatch(a -> !a.isWorking())).toList());
    }
    public static Map<String, Long> analyzeEq5(FactoryManager manager){
        return manager.getAnalyticsStream().collect(Collectors.groupingBy(
                a -> a.getClass().getSimpleName(), Collectors.counting()
                )
        );
    }
    public static Optional<FactoryEquipment> analyzeEq6(FactoryManager manager){
        return manager.getAnalyticsStream().min(Comparator.comparingDouble(FactoryEquipment::getTemperature))
                .filter(FactoryEquipment::isWorking);
    }
    public static String analyzeEq7(FactoryManager manager){
        return manager.getAnalyticsStream().filter(a -> a.isWorking()).map(FactoryEquipment::getName)
                .collect(Collectors.joining(", "));
    }
    public static void analyzeEq8(FactoryManager manager){
        manager.getAnalyticsStream().sorted(Comparator.comparingDouble(FactoryEquipment::getTemperature).reversed())
                .limit(3).forEach(a -> System.out.println(a.getDetails()));
    }
}
