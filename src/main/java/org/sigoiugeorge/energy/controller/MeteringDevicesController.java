package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.sigoiugeorge.energy.service.api.UserService;
import org.sigoiugeorge.energy.utils.EnergyConsumptionShort;
import org.sigoiugeorge.energy.utils.MeteringDeviceShort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MeteringDevicesController {

    private final MeteringDeviceService deviceService;
    private final UserService userService;

    @GetMapping("/get/devices")
    public ResponseEntity<List<MeteringDevice>> getAllDevices() {
        List<MeteringDevice> all = deviceService.getAll();
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/get/devices/consumption-exceeded-limit/username={username}")
    public ResponseEntity<List<MeteringDeviceShort>> getAllDevicesWithConsumptionExceededTheLimit(@PathVariable String username) {
        Set<MeteringDevice> meteringDevices = userService.getUser(username).getMeteringDevices();
        ArrayList<MeteringDevice> devices = new ArrayList<>(meteringDevices);
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        List<MeteringDeviceShort> list = new LinkedList<>();
        for (MeteringDevice device : devices) {
            List<EnergyConsumptionShort> consumptionsForDevice = getConsumptionsForDevice(startDate, device);
            List<EnergyConsumptionShort> energyConsumptionExceeded = filteredConsumptions(device, consumptionsForDevice);
            for (EnergyConsumptionShort ecs : energyConsumptionExceeded) {
                MeteringDeviceShort meteringDeviceShort = new MeteringDeviceShort(
                        device.getAddress(),
                        device.getMaxHourlyEnergyConsumption(),
                        ecs.getDate(),
                        ecs.getHour());
                list.add(meteringDeviceShort);
            }
        }

        return ResponseEntity.ok().body(list);
    }

    /**
     * Create a list of energy consumptions that exceeded the hourly limit
     */
    private @NotNull List<EnergyConsumptionShort> filteredConsumptions(@NotNull MeteringDevice device, @NotNull List<EnergyConsumptionShort> consumptionsForDevice) {
        List<EnergyConsumptionShort> energyConsumptionExceeded = new LinkedList<>();
        int lastConsumption = -1;
        for (EnergyConsumptionShort consumptionShort : consumptionsForDevice) {
            if (lastConsumption == -1) {
                lastConsumption = consumptionShort.getValue();
                continue;
            }
            int currentConsumption = consumptionShort.getValue();
            int diff = currentConsumption - lastConsumption;
            lastConsumption = currentConsumption;
            if (diff > device.getMaxHourlyEnergyConsumption()) {
                energyConsumptionExceeded.add(consumptionShort);
            }
        }
        return energyConsumptionExceeded;
    }

    /**
     * Create a list of objects that contains info grouped by date, hour and device id
     *
     * @param startDate energy consumption registered from startDate until now
     * @param device    the device
     */
    private @NotNull List<EnergyConsumptionShort> getConsumptionsForDevice(LocalDateTime startDate, @NotNull MeteringDevice device) {
        List<EnergyConsumption> energyConsumptions = device.getEnergyConsumption().stream().toList();
        List<EnergyConsumption> filtered = energyConsumptions.stream().filter(e -> e.getTimestamp().isAfter(startDate)).toList();
        List<EnergyConsumption> sorted = filtered.stream().sorted(Comparator.comparing(EnergyConsumption::getTimestamp)).toList();
        List<EnergyConsumptionShort> list = new LinkedList<>();
        for (EnergyConsumption energyConsumption : sorted) {
            LocalDateTime timestamp1 = energyConsumption.getTimestamp();
            LocalDate date1 = timestamp1.toLocalDate();
            int hour1 = timestamp1.getHour() + 1;
            Integer consumption = energyConsumption.getEnergyConsumption();
            EnergyConsumptionShort obj = new EnergyConsumptionShort();
            obj.setDeviceId(device.getId());
            obj.setDate(date1);
            obj.setHour(hour1);
            obj.setValue(consumption);
            if (list.contains(obj)) {
                list.remove(obj);
                list.add(obj);
            } else {
                list.add(obj);
            }
        }
        return list;
    }

    @PutMapping("/add/device={deviceId}-to-user={userId}")
    public ResponseEntity<User> addDeviceToUser(@PathVariable Long deviceId, @PathVariable Long userId) {
        MeteringDevice device = deviceService.get(deviceId);
        User user = userService.get(userId);
        device.setUser(user);
        deviceService.update(device);
        //because i need the device list updated
        user.addMeteringDevice(device);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/update/devices/to-user={userId}")
    public void updateTheUserForDevice(@RequestBody List<Long> devicesIds, @PathVariable Long userId) {
        User user;
        if (userId == null || userId <= 0) {
            user = null;
        } else {
            user = userService.get(userId);
        }
        for (Long devicesId : devicesIds) {
            MeteringDevice device = deviceService.get(devicesId);
            device.setUser(user);
            deviceService.update(device);
        }
    }

    @PostMapping("/add/device")
    public ResponseEntity<MeteringDevice> insertDevice(@RequestBody MeteringDevice device) {
        MeteringDevice meteringDevice = deviceService.create(device);
        return ResponseEntity.ok().body(meteringDevice);
    }

    @DeleteMapping("/delete/device-id={deviceId}")
    public void deleteDevice(@PathVariable Long deviceId) {
        deviceService.remove(deviceId);
    }

    @PutMapping("/update/device-id={deviceId}")
    public ResponseEntity<MeteringDevice> updateDevice(@RequestBody @NotNull MeteringDevice device, @PathVariable Long deviceId) {
        MeteringDevice meteringDevice = deviceService.get(deviceId);
        String address = device.getAddress();
        if (address != null) {
            meteringDevice.setAddress(address);
        }
        String description = device.getDescription();
        if (description != null) {
            meteringDevice.setDescription(description);
        }
        Integer maxHourlyEnergyConsumption = device.getMaxHourlyEnergyConsumption();
        if (maxHourlyEnergyConsumption != null) {
            meteringDevice.setMaxHourlyEnergyConsumption(maxHourlyEnergyConsumption);
        }
        MeteringDevice update = deviceService.update(meteringDevice);
        return ResponseEntity.ok().body(update);
    }

    @GetMapping("/get/devices/no-owner")
    public ResponseEntity<List<MeteringDevice>> getDevicesWithoutOwner() {
        List<MeteringDevice> collect = deviceService.getAll().stream().filter((d) -> d.getUser() == null).collect(Collectors.toList());
        return ResponseEntity.ok().body(collect);
    }

    @PostMapping("/verify/unique/device-address")
    public ResponseEntity<Boolean> verifyIfAddressIsUnique(@RequestBody @NotNull Map<String, String> body) {
        return ResponseEntity.ok().body(deviceService.addressIsUnique(body.get("address")));
    }

    @GetMapping("/get/consumption/for-date={date}/device-id={deviceId}")
    public ResponseEntity<Map<Integer, Integer>> getDailyConsumption(@PathVariable @NotNull String date, @PathVariable Long deviceId) {
        int endIndex = date.lastIndexOf(".");
        if (endIndex == -1) {
            endIndex = date.length();
        }
        date = date.substring(0, endIndex);
        LocalDateTime parsedDate = LocalDateTime.parse(date);

        Map<Integer, Integer> resultMap = new HashMap<>();
        Map<Integer, Integer> energyTodayMap = new HashMap<>();
        Map<Integer, Integer> energyYesterdayMap = new HashMap<>();
        for (int i = 0; i <= 24; i++) {
            resultMap.put(i, 0);
            energyTodayMap.put(i, 0);
            energyYesterdayMap.put(i, 0);
        }

        MeteringDevice device = deviceService.get(deviceId);
        List<EnergyConsumption> consumption = new ArrayList<>(device.getEnergyConsumption());
        List<EnergyConsumption> collectToday = new java.util.ArrayList<>(consumption.stream().filter(el -> el.getTimestamp().toLocalDate().equals(parsedDate.toLocalDate())).toList());
        List<EnergyConsumption> collectYesterday = new java.util.ArrayList<>(consumption.stream().filter(el -> el.getTimestamp().toLocalDate().equals(parsedDate.toLocalDate().minusDays(1))).toList());

        collectToday.sort(Comparator.comparing(EnergyConsumption::getTimestamp));
        collectYesterday.sort(Comparator.comparing(EnergyConsumption::getTimestamp));

        for (EnergyConsumption energyConsumption : collectToday) {
            int hour = energyConsumption.getTimestamp().getHour() + 1;
            energyTodayMap.put(hour, energyConsumption.getEnergyConsumption());
        }
        for (EnergyConsumption energyConsumption : collectYesterday) {
            int hour = energyConsumption.getTimestamp().getHour() + 1;
            energyYesterdayMap.put(hour, energyConsumption.getEnergyConsumption());
        }
        energyTodayMap.put(0, energyYesterdayMap.get(24));

        for (int current = 0; current <= 24; current++) {
            int before = current - 1;
            if (before == -1) {
                int dif = energyYesterdayMap.get(24) - energyYesterdayMap.get(23);
                if (dif < 0) dif = 0;
                resultMap.put(0, dif);
                continue;
            }
            int dif = energyTodayMap.get(current) - energyTodayMap.get(before);
            if (dif < 0) dif = 0;
            resultMap.put(current, dif);
        }

        return ResponseEntity.ok().body(resultMap);
    }
}

