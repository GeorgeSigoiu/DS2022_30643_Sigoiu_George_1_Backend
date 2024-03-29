package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sigoiugeorge.energy.dao.UserRepo;
import org.sigoiugeorge.energy.model.Credentials;
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
        List<User> allUsers = repo.findAll();
        Optional<User> firstUser = allUsers.stream().filter((u) -> u.getCredentials().getUsername().equals(username)).findFirst();
        if (firstUser.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        User user = firstUser.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        String encode = passwordEncoder.encode(user.getCredentials().getPassword());
        return new org.springframework.security.core.userdetails.User(user.getCredentials().getUsername(), encode, authorities);
    }

    @Override
    public User create(@NotNull User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("The user exists in database, he has an id!\n" + user);
        }
        Credentials credentials = user.getCredentials();
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
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
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("User with id=" + id + " does not exist!");
        }
        return repo.findById(id).get();
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
    @Nullable
    public User getUser(String username) {
        List<User> all = getAll();
        Optional<User> first = all.stream().filter(u -> u.getCredentials().getUsername().equals(username)).findFirst();
        if (first.isEmpty()) {
            return null;
        }
        return first.get();
    }

}
