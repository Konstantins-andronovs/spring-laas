package com.andronovs.loan.repository;

import com.andronovs.loan.entities.Loan;
import com.andronovs.loan.entities.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private static final Map<UUID, User> userData = new HashMap<>();

    public Optional<User> findByUsername(String username) {
        return userData.values().stream().filter(user -> user.getUsername().equals(username)).findFirst();
    };

    public User save(User user) {
        userData.put(user.getId(), user);
        return user;
    };
}
