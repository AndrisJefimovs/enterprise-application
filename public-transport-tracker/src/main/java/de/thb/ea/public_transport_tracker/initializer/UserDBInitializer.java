package de.thb.ea.public_transport_tracker.initializer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.PermissionService;
import de.thb.ea.public_transport_tracker.service.RoleService;
import de.thb.ea.public_transport_tracker.service.UserService;


@Component
public class UserDBInitializer implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(UserDBInitializer.class);
    
    @Value("${application.users.admin.username}")
    private String adminUsername;

    @Value("${application.users.admin.email}")
    private String adminEmail;

    @Value("${application.users.admin.password}")
    private String adminPassword;

    private final String permissionNames[] = {
        "CREATE_USER",
        "READ_USER",
        "UPDATE_USER",
        "DELETE_USER",
        "UPDATE_USER_ROLES",
        "CREATE_ROLE",
        "READ_ROLE",
        "UPDATE_ROLE",
        "DELETE_ROLE"
    };

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;


    public UserDBInitializer(UserService userService, RoleService roleService,
                             PermissionService permissionService) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public void run(ApplicationArguments args) throws IllegalArgumentException {
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

        // create permissions if not exist
        for (String permissionName : permissionNames) {
            if (!permissionService.permissionExists(permissionName)) {
                Permission permission = permissionService.addNewPermission(permissionName);
                if (permission == null) {
                    logger.warn(String.format("Failed to create permission '%s'", permissionName));
                    logger.info(String.format("You may need to create permission '%s' manually",
                                              permissionName));
                }
                else {
                    logger.info(String.format("Successfully created permission '%s' with id %d",
                                              permission.getName(), permission.getId()));
                }
            }
        }

        // create user role if not exists
        String userRoleName = "USER";
        Role userRole = roleService.getRoleByName(userRoleName);

        if (userRole == null) {
            Set<Permission> permissions = new HashSet<>();
            permissions.add(permissionService.getPermissionByName("READ_USER"));
            permissions.add(permissionService.getPermissionByName("READ_ROLE"));

            userRole = Role.builder()
                           .name(userRoleName)
                           .permissions(permissions)
                           .build();
            
            userRole = roleService.addNewRole(userRole);
            if (userRole == null) {
                logger.warn(String.format("Failed to create role '%s'", userRoleName));
                logger.info("You may need to created the role by hand.");
            }
            else {
                logger.info(String.format("Successfully created role '%s' with id %d",
                                          userRole.getName(), userRole.getId()));
            }
        }
        else {
            logger.info(String.format("Role '%s' already exists.", userRole.getName()));
        }

        // create super admin role if not exists
        String superAdminRoleName = "SUPER_ADMIN";
        Role superAdminRole = roleService.getRoleByName(superAdminRoleName);

        if (superAdminRole == null) {
            Set<Permission> permissions = permissionService.getAllPermissions()
                                                           .stream().collect(Collectors.toSet());
            superAdminRole = Role.builder()
                                 .name(superAdminRoleName)
                                 .permissions(permissions)
                                 .build();

            superAdminRole = roleService.addNewRole(superAdminRole);
            if (superAdminRole == null) {
                logger.warn(String.format("Failed to create role '%s'", superAdminRoleName));
                logger.info("You may need to created the role by hand.");
            }
            else {
                logger.info(String.format("Successfully created role '%s' with id %d",
                                          superAdminRole.getName(), superAdminRole.getId()));
            }
        }
        else {
            logger.info(String.format("Role '%s' already exists.", superAdminRole.getName()));
        }

        // create super admin user
        
        List<User> superAdmins = userService.getUsersByRole(superAdminRole);
        if (superAdmins.isEmpty()) {
            User superAdmin = User.builder()
                                  .username(adminUsername)
                                  .email(adminEmail)
                                  .password(adminPassword)
                                  .roles(new HashSet<>(Arrays.asList(superAdminRole)))
                                  .build();
            superAdmin = userService.addNewUser(superAdmin);
            if (superAdmin == null) {
                logger.warn(String.format("Failed to create user '%s'", adminUsername));
                logger.info("You may need to create the user by hand.");
            }
            else {
                logger.info(String.format("Successfully created super admin user '%s'",
                                          superAdmin.getUsername()));
            }
        }
        else {
            logger.info("A super admin already exists, so no default super admin is created");
        }
    }
}
