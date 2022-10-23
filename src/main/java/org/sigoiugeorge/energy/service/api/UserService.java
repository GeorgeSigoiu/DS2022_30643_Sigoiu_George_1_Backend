package org.sigoiugeorge.energy.service.api;

import com.sun.istack.NotNull;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.model.MeteringDevice;

import java.util.List;

public interface UserService extends CrudOperationsService<User> {

    User getUser(String username);

    void addMeteringDevice(long userId, @NotNull MeteringDevice device);

    void addMeteringDevice(User user, @NotNull MeteringDevice device);

    void removeMeteringDevice(long userId, long deviceId);

    void removeMeteringDevice(@NotNull User user, long deviceId);

    void removeMeteringDevice(long userId, @NotNull MeteringDevice device);

    void removeMeteringDevice(@NotNull User user, @NotNull MeteringDevice device);

    @NotNull
    List<MeteringDevice> getAllMeteringDevices(long userId);

    @NotNull
    List<MeteringDevice> getAllMeteringDevices(@NotNull User user);

}
