package de.thb.ea.public_transport_tracker.initializer;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.thb.ea.public_transport_tracker.config.AdminProperties;
import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.PermissionService;
import de.thb.ea.public_transport_tracker.service.UserService;


@Component
public class UserDBInitializer implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(UserDBInitializer.class);

    private final String permissionNames[] = {
        "CREATE_USER",
        "READ_USER",
        "UPDATE_USER",
        "DELETE_USER"
    };

    private final AdminProperties adminProperties;
    private final UserService userService;
    private final PermissionService permissionService;


    public UserDBInitializer(
        AdminProperties adminProperties, UserService userService,
        PermissionService permissionService
    ) {
        this.adminProperties = adminProperties;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public void run(ApplicationArguments args) throws IllegalArgumentException, Exception {
        // get cli args
        List<String> usernames = args.getOptionValues("admin-username");
        List<String> passwords = args.getOptionValues("admin-password");
        List<String> emails = args.getOptionValues("admin-email");

        if (usernames != null) {
            if (usernames.size() > 1) {
                throw new IllegalArgumentException("Multiple default admin usernames");
            }
            if (!usernames.isEmpty()) {
                adminProperties.setUsername(usernames.get(0));
            }
        }

        if (passwords != null) {
            if (passwords.size() > 1) {
                throw new IllegalArgumentException("Multiple default admin passwords");
            }
            if (!passwords.isEmpty()) {
                adminProperties.setPassword(passwords.get(0));
            }
        }

        if (emails != null) {
            if (emails.size() > 1) {
                throw new IllegalArgumentException("Multiple default admin email addresses");
            }
            if (!emails.isEmpty()) {
                adminProperties.setEmail(emails.get(0));
            }
        }

        // create permissions if not exist
        for (String permissionName : permissionNames) {
            if (!permissionService.permissionExists(permissionName)) {
                Permission permission = permissionService.addNewPermission(permissionName);

                if (permission == null) {
                    logger.warn(String.format("Failed to create permission '%s'", permissionName));
                    logger.info(String.format(
                        "You may need to create permission '%s' manually", permissionName
                    ));
                }
                else {
                    logger.debug(String.format(
                        "Successfully created permission '%s' with id %d",
                        permission.getName(), permission.getId()
                    ));
                }
            }
            else {
                logger.info(String.format("Permission '%s' already exists", permissionName));
            }
        }

        // create system user if not exists
        String systemUserName = "SYSTEM";
        User systemUser = userService.getUserByUsername(systemUserName);

        if (systemUser == null) {
            systemUser = userService.addNewUser(
                User.builder()
                    .username(systemUserName)
                    .email("")
                    .loginEnabled(false)
                    .build()
            );
            if (systemUser == null) {
                logger.error(String.format("Failed to create user '%s'", systemUserName));
                throw new Exception("Failed to create system user");
            }
            else {
                logger.debug(String.format(
                    "Successfully created user '%s' with id %d",
                    systemUser.getUsername(), systemUser.getId()
                ));
            }
        }
        else {
            logger.info(String.format(
                "System user '%s' (id %d) already exists",
                systemUser.getUsername(), systemUser.getId()
            ));
        }

        // create admin user
        User admin = userService.getUserByUsername(adminProperties.getUsername());
        if (admin == null) {
            admin = User.builder()
                .username(adminProperties.getUsername())
                .email(adminProperties.getEmail())
                .password(adminProperties.getPassword())
                .permissions(permissionService.getAllPermissions()
                    .stream().collect(Collectors.toSet()))
                .createdBy(systemUser)
                .build();

            admin = userService.addNewUser(admin);
            if (admin == null) {
                logger.warn(String.format(
                    "Failed to create user '%s'", adminProperties.getUsername()
                ));
                logger.info("You may need to create the user by hand.");
            }
            else {
                logger.debug(String.format(
                    "Successfully created super admin user '%s'", admin.getUsername()
                ));
            }
        }
        else {
            logger.info("The admin user already exists.");
        }
    }
}
