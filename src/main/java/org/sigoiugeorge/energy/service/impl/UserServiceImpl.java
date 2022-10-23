package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.UserRepo;
import org.sigoiugeorge.energy.model.Credentials;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.model.User;
import org.sigoiugeorge.energy.service.api.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("load user by username: " + username);
        List<User> allUsers = repo.findAll();
        Optional<User> firstUser = allUsers.stream().filter((u) -> u.getCredentials().getUsername().equals(username)).findFirst();
        if (firstUser.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        User user = firstUser.get();
        System.out.println("user found: " + user);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        String encode = passwordEncoder.encode(user.getCredentials().getPassword());
        return new org.springframework.security.core.userdetails.User(user.getCredentials().getUsername(), encode, authorities);
    }

    @Override
    public User create(@NotNull User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("The user exists in database, he has an id!\n" + user.toString());
        }
        Credentials credentials = user.getCredentials();
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        System.out.println(credentials.getPassword());
        user.setCredentials(credentials);
        return repo.save(user);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(@NotNull User entity) {
        remove(entity.getId());
    }

    @Override
    public User get(long id) {
        return repo.getReferenceById(id);
    }

    @Override
    public List<User> getAll() {
        return repo.findAll();
    }

    //maybe this needs also password encode
    @Override
    public User update(@NotNull User entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("In order to update the user, the ID can not be null!");
        }
        return repo.save(entity);
    }

    @Override
    public void addMeteringDevice(long userId, MeteringDevice device) {
        User user = repo.getReferenceById(userId);
        addMeteringDevice(user, device);
    }

    @Override
    public void addMeteringDevice(@NotNull User user, MeteringDevice device) {
        user.addMeteringDevice(device);
        create(user);
    }

    @Override
    public void removeMeteringDevice(long userId, long deviceId) {
        User user = get(userId);
        List<MeteringDevice> meteringDevices = user.getMeteringDevices();
        int index = -1;
        boolean found = false;
        for (MeteringDevice device : meteringDevices) {
            index++;
            if (device.getId() == deviceId) {
                found = true;
                break;
            }
        }
        if (found) {
            user.removeMeteringDevice(index);
            create(user);
        }
    }

    @Override
    public void removeMeteringDevice(@NotNull User user, long deviceId) {
        Long userId = user.getId();
        removeMeteringDevice(userId, deviceId);
    }

    @Override
    public void removeMeteringDevice(long userId, MeteringDevice device) {
        User user = get(userId);
        removeMeteringDevice(user, device);
    }

    @Override
    public void removeMeteringDevice(@NotNull User user, MeteringDevice device) {
        user.removeMeteringDevice(device);
        create(user);
    }

    @Override
    public List<MeteringDevice> getAllMeteringDevices(long userId) {
        User user = get(userId);
        return user.getMeteringDevices();
    }

    @Override
    public List<MeteringDevice> getAllMeteringDevices(@NotNull User user) {
        return user.getMeteringDevices();
    }

}
