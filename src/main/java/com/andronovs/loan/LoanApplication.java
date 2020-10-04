package com.andronovs.loan;

import com.andronovs.loan.entities.Role;
import com.andronovs.loan.entities.User;
import com.andronovs.loan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class LoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }

}

@Component
class LAASCommandLineRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LAASCommandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        User manager = new User();
        manager.setId(UUID.randomUUID());
        manager.setUsername("manager");
        manager.setPassword(passwordEncoder.encode("password"));
        manager.grantAuthority(Role.ROLE_MANAGER);

        userRepository.save(manager);

        User broker = new User();
        broker.setId(UUID.randomUUID());
        broker.setUsername("broker");
        broker.setPassword(passwordEncoder.encode("password"));
        broker.grantAuthority(Role.ROLE_BROKER);

        userRepository.save(broker);
    }
}
