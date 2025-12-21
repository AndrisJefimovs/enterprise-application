package de.thb.ea.public_transport_tracker.initializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.UserService;


@Component
public class AdminInitializer implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    @Value("${application.users.admin.username}")
    private String adminUsername;

    @Value("${application.users.admin.password}")
    private String adminPassword;

    private final UserService userService;


    public AdminInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<User> adminUsers = userService.getUsersByRole("ROLE_ADMIN");

        if (adminUsers.isEmpty()) {
            // get cli args
            List<String> usernames = args.getOptionValues("admin-username");
            List<String> passwords = args.getOptionValues("admin-password");

            if (usernames != null) {
                if (usernames.size() > 1)
                    throw new IllegalArgumentException("Multiple default admin usernames");
                if (!usernames.isEmpty())
                    adminUsername = usernames.get(0);
            }

            if (passwords != null) {
                if (passwords.size() > 1)
                    throw new IllegalArgumentException("Multiple default admin passwords");
                if (!passwords.isEmpty())
                    adminPassword = passwords.get(0);
            }

            Set<Role> roles = new HashSet<>();
            roles.add(Role.builder().name("ROLE_ADMIN").build());
            roles.add(Role.builder().name("ROLE_USER").build());

            Boolean ok = userService.addNewUserWithRoles(
                User.builder()
                    .username(adminUsername)
                    .email("")
                    .password(adminPassword)
                    .roles(roles)
                    .build()
            );

            if (ok)
                logger.info("Default Admin account created");
            else {
                logger.warn("Failed to create default Admin account");
                logger.info("You probably have to install the first admin account manually by" +
                            " accessing the database.");
            }
        }
        else
            logger.info("No default admin account needed; there is already an admin account.");
    }
}
