package org.sigoiugeorge.energy.controller;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class MeteringDevicesController {

    private final MeteringDeviceService service;

    @GetMapping("/get/devices")
    public ResponseEntity<List<MeteringDevice>> getAllDevices() {
        List<MeteringDevice> all = service.getAll();
        return ResponseEntity.ok().body(all);
    }

    @PostMapping("/add/device")
    public ResponseEntity<MeteringDevice> insertDevice(@RequestBody MeteringDevice device) {
        MeteringDevice meteringDevice = service.create(device);
        return ResponseEntity.ok().body(meteringDevice);
    }

    @DeleteMapping("/delete/device-id={deviceId}")
    public void deleteDevice(@PathVariable Long deviceId) {
        service.remove(deviceId);
    }

    @PutMapping("/update/device-id={deviceId}")
    public ResponseEntity<MeteringDevice> updateDevice(@RequestBody MeteringDevice device, @PathVariable Long deviceId) {
        MeteringDevice meteringDevice = service.get(deviceId);
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
        MeteringDevice update = service.update(meteringDevice);
        return ResponseEntity.ok().body(update);
    }

    @GetMapping("/get/devices/no-owner")
    public ResponseEntity<List<MeteringDevice>> getDevicesWithoutOwner() {
        List<MeteringDevice> collect = service.getAll().stream().filter((d) -> d.getUser() == null).collect(Collectors.toList());
        return ResponseEntity.ok().body(collect);
    }
}

