package Exams.main;

import java.util.List;
import java.util.Scanner;

import Exams.logic.Scenario;
import Exams.logic.SmartHomeManager;
import Exams.models.Device;
import Exams.models.SmartLight;

public class Main {
    public static Scanner scanner  = new Scanner(System.in);
    public static void main(String[] args){
        SmartHomeManager myHome = new SmartHomeManager();
        System.out.println("num through 1-7");
        Integer input = Integer.valueOf(scanner.nextLine());
        while(input != 7){
            switch(input) {
                case 1: {
                    System.out.println("room: ");
                    String room = scanner.nextLine();
                    System.out.println("id: ");
                    String id = scanner.nextLine();
                    System.out.println("name: ");
                    String name = scanner.nextLine();
                    System.out.println("status: ");
                    Boolean status = Boolean.valueOf(scanner.nextLine());
                    System.out.println("power consumption: ");
                    Double powerConsumption = Double.valueOf(scanner.nextLine());
                    System.out.println("brightness: ");
                    Integer brightness = Integer.valueOf(scanner.nextLine());
                    System.out.println("color: ");
                    String color = scanner.nextLine();
                    SmartLight light = new SmartLight();
                    light.setId(id);
                    light.setName(name);
                    light.setStatus(status);
                    light.setPowerConsumption(powerConsumption);
                    light.setBrightness(brightness);
                    light.setColor(color);
                    myHome.addDevice(room, light);
                }
                break;
                case 2: {
                    System.out.println("id: ");
                    String id = scanner.nextLine();
                    Device new_device = myHome.getDeviceById(id);
                    if (new_device.isOn()) {
                        new_device.turnOf();
                    } else {
                        new_device.turnOn();
                    }
                }
                break;
                case 3: {
                    Scenario<Device> phantomReset = new Scenario<>(
                            "phantom con",
                            device -> !device.isOn() && device.getPowerConsumption() > 0,
                            device -> device.setPowerConsumption(0.0),
                            Device.class
                    );
                    myHome.addScenario(phantomReset);

                }break;
                case 4:{
                    Scenario<SmartLight> lightSleep = new Scenario<>(
                            "Calm sleep ",
                            light -> light.isOn() && light.getBrightness() > 50,
                            light -> {light.setBrightness(10); light.setColor("Warm");},
                            SmartLight.class
                    );
                    myHome.addScenario(lightSleep);
                }break;
                case 5:{
                    myHome.executeAllScenarios();
                }break;
                case 6: {
                    Double allDevices = myHome.getAnalyticsStream()
                            .mapToDouble(Device::getPowerConsumption)
                            .sum();
                    System.out.println(allDevices);

                }break;
            }
            input = Integer.valueOf(scanner.nextLine());
        }
    }
}
