package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.utils.MeteringDeviceShort;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<MeteringDevice> devices = userService.getUser(username).getMeteringDevices();
        List<MeteringDeviceShort> devicesWithConsumption = new ArrayList<>();
        for (MeteringDevice device : devices) {
            List<EnergyConsumption> consumptions = device.getEnergyConsumption();
            if (consumptions.size() < 1) {
                continue;
            }
            consumptions.sort(Comparator.comparing(EnergyConsumption::getEnergyConsumption));
            EnergyConsumption lastConsumption = consumptions.get(consumptions.size() - 1);
            if (lastConsumption.getEnergyConsumption() > device.getMaxHourlyEnergyConsumption()) {
                MeteringDeviceShort device1 = new MeteringDeviceShort(device.getAddress(), device.getMaxHourlyEnergyConsumption());
                devicesWithConsumption.add(device1);
            }
        }
        return ResponseEntity.ok().body(devicesWithConsumption);
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
        List<EnergyConsumption> consumption = device.getEnergyConsumption();
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

