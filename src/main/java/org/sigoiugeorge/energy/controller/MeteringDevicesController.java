package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
        System.out.println("The input");
        System.out.print("list: ");
        for (Long devicesId : devicesIds) {
            System.out.print(devicesId + ", ");
        }
        System.out.println("user id: " + userId);
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
    public ResponseEntity<MeteringDevice> updateDevice(@RequestBody MeteringDevice device, @PathVariable Long deviceId) {
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
    public ResponseEntity<Boolean> verifyIfAddressIsUnique(@RequestBody Map<String,String> body) {
        System.out.println(body.get("address"));
        return ResponseEntity.ok().body(deviceService.addressIsUnique(body.get("address")));
    }
}

