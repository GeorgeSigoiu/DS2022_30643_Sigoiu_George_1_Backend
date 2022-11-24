package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.MeteringDeviceRepo;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MeteringDeviceServiceImpl implements MeteringDeviceService {

    private final MeteringDeviceRepo repo;

    @Override
    public MeteringDevice create(@NotNull MeteringDevice entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("The device exists in database, it has an id!\n" + entity.toString());
        }
        return repo.save(entity);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(@NotNull MeteringDevice entity) {
        remove(entity.getId());
    }

    @Override
    public MeteringDevice get(long id) {
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Device with id=" + id + " does not exist!");
        }
        return repo.findById(id).get();
    }

    @Override
    public List<MeteringDevice> getAll() {
        return repo.findAll();
    }

    @Override
    public MeteringDevice update(@NotNull MeteringDevice entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("The device can not be updated because it does not have an id!");
        }
        return repo.save(entity);
    }

    @Override
    public Boolean addressIsUnique(String address) {
        Optional<MeteringDevice> byAddress = repo.findByAddress(address);
        return byAddress.isEmpty();
    }

    @Override
    public Boolean deviceExceededMaxHourlyConsumption(long deviceId, LocalDateTime timestamp) {
        Optional<MeteringDevice> byId = repo.findById(deviceId);
        if (byId.isEmpty()) {
            throw new RuntimeException("Device with id=" + deviceId + " does not exist!");
        }
        MeteringDevice meteringDevice = byId.get();
        List<EnergyConsumption> energyConsumption = new ArrayList<>(meteringDevice.getEnergyConsumption());
        if (energyConsumption.size() < 1) {
            return false;
        }
        LocalDate date1 = timestamp.toLocalDate();
        int hour1 = timestamp.getHour() + 1;

        LocalDateTime timestamp2 = timestamp.minusHours(1);
        LocalDate date2 = timestamp2.toLocalDate();
        int hour2 = timestamp2.getHour() + 1;

        Optional<Integer> maxForTimestamp = energyConsumption.stream()
                .filter(e -> e.getTimestamp().toLocalDate().equals(date1))
                .filter(e -> e.getTimestamp().getHour() + 1 == hour1)
                .map(EnergyConsumption::getEnergyConsumption)
                .max(Integer::compareTo);
        Optional<Integer> maxForTimestamp2 = energyConsumption.stream()
                .filter(e -> e.getTimestamp().toLocalDate().equals(date2))
                .filter(e -> e.getTimestamp().getHour() + 1 == hour2)
                .map(EnergyConsumption::getEnergyConsumption)
                .max(Integer::compareTo);

        if (maxForTimestamp.isEmpty()) {
            return false;
        }
        Integer integer1 = maxForTimestamp.get();
        if (maxForTimestamp2.isEmpty()) {
            return meteringDevice.getMaxHourlyEnergyConsumption() < integer1;
        }
        int diff = integer1 - maxForTimestamp2.get();
        return diff > meteringDevice.getMaxHourlyEnergyConsumption();
    }

}
