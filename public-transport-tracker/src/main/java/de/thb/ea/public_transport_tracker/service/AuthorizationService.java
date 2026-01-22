package de.thb.ea.public_transport_tracker.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.config.property.AdminProperties;
import de.thb.ea.public_transport_tracker.config.security.SecurityContextFacade;
import de.thb.ea.public_transport_tracker.controller.api.model.UserDTO;
import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.exception.AuthorizationException;
import de.thb.ea.public_transport_tracker.service.exception.UserNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthorizationService {
    
    private final SecurityContextFacade security;
    private final AdminProperties adminProperties;
    private final UserService userService;

    /**
     * Simple wraper for <code>userService.isIdOfUser(Long, String)</code> to handle
     * UserNotFoundException and throw unchecked exception instead.
     * 
     * @param id
     * @param username
     * @return
     * @see UserService#isIdOfUser(Long, String)
     */
    private boolean isIdOfUser(Long id, String username) {
        try {
            return userService.isIdOfUser(id, username);
        }
        catch (UserNotFoundException e) {
            throw new AuthorizationException(e);
        }
    }

    /**
     * Get current user from context.
     * 
     * @return current user
     * @see de.thb.ea.public_transport_tracker.entity.User
     */
    public User getUser() {
        try {
            return userService.getUserByUsername(security.getUserDetails().getUsername());
        }
        catch (UserNotFoundException e) {
            throw new AuthorizationException("Couldn't load current user", e);
        }
    }

    /**
     * Check if current user has userId.
     * 
     * @param userId
     * @return true if current user as id; otherwise false.
     */
    public boolean hasId(Long userId) {
        return userId == getUser().getId();
    }

    /**
     * Check if current user has permission by permission name.
     * 
     * @param permissionName
     * @return true if current user as permission; otherwise false.
     * @see de.thb.ea.public_transport_tracker.entity.Permission#getName()
     */
    public boolean hasPermission(String permissionName) {
        return security.hasAuthority("PERM_" + permissionName.toUpperCase());
    }

    /**
     * Check if current user can create a user.
     * 
     * <p>
     * A user can create another user, if he has CREATE_USER permissions and he only gives
     * permissions he also has.
     * 
     * <ul>
     * <li>The <code>userDTO.id</code> has to be <code>null</code> since it is not allowed to set an
     * id explicitly.
     * <li>The <code>userDTO.createdBy</code> has to be <code>null</code>
     * <li>The <code>userDTO.createdAt</code> has to be <code>null</code>
     * <li>The <code>userDTO.updatedAt</code> has to be <code>null</code>
     * <li>The <code>userDTO.refreshVersion</code> has to be <code>null</code>
     * </ul>
     * 
     * <p>
     * <b>Note:</b> Returns always true if <code>userDTO</code> is <code>null</code>. With this
     * behavior when the method is used in an <code>@PreAuthorize()</code> the controller method is
     * being executed and other response codes like 400 can be implemented inside controller code.
     * 
     * @param userDTO   The <code>UserDTO</code> used to create the new user.
     * @return          <code>true</code> if user can be created; otherwise <code>false</code>.
     * @see de.thb.ea.public_transport_tracker.controller.api.model.UserDTO
     * @see org.springframework.security.access.prepost.PreAuthorize
     */
    public boolean canCreateUser(UserDTO userDTO) {
        if (userDTO == null) {
            return true;
        }
        if (
            !hasPermission("CREATE_USER")
            || userDTO.getId() != null // not allowed to set own id
            || userDTO.getCreatedBy() != null
            || userDTO.getCreatedAt() != null
            || userDTO.getUpdatedAt() != null
            || userDTO.getRefreshVersion() != null
        ) {
            return false;
        }

        for (String permissionName : userDTO.getPermissions()) {
            // cannot give someone permissions the user doesn't have
            if (!hasPermission(permissionName)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Check if current user can read a user.
     * 
     * <p>
     * A user can read a user if it is himself or it has read permissions.
     * 
     * <p>
     * <b>Note:</b> Returns always true if <code>userId</code> is <code>null</code> or doesn't
     * exist. With this behavior when the method is used in an <code>@PreAuthorize()</code> the
     * controller method is being executed and other response codes like 404 can be implemented
     * inside controller code.
     * 
     * @param userId    id of the other user.
     * @return          <code>true</code> if it can read user; otherwise <code>false</code>
     * @see de.thb.ea.public_transport_tracker.controller.api.model.UserDTO
     * @see org.springframework.security.access.prepost.PreAuthorize
     */
    public boolean canReadUser(Long userId) {
        if (userId == null || !userService.userIdExists(userId)) {
            return true;
        }
        return hasPermission("READ_USER")
            || hasId(userId);
    }

    /**
     * Check if current user can update a user.
     * 
     * <p>
     * A user can always update itself.
     * A user can update another user if it has UPDATE_USER permissions. The user can only
     * update permissions it has on its own. The <code>userId</code> cannot be updated. You cannot
     * change your own permissions.
     * 
     * <p>
     * The SYSTEM user cannot be updated.
     * The admin user can only be updated by its own and cannot remove its permissions or change its
     * username.
     * 
     * <ul>
     * <li>The <code>userDTO.createdBy</code> cannot be changed so safest way is to set to
     * <code>null</code>
     * <li>The <code>userDTO.createdAt</code> cannot be changed so safest way is to set to
     * <code>null</code>
     * <li>The <code>userDTO.updatedAt</code> cannot be changed so safest way is to set to
     * <code>null</code>
     * <li>The <code>userDTO.refreshVersion</code> cannot be changed so safest way is to set to
     * <code>null</code>
     * </ul>
     * 
     * <p>
     * You can use this function outside <code>@PreAuthorize()</code> to check if the current user
     * can update the user.
     * 
     * <p>
     * <b>Note:</b> Returns always true if <code>userId</code> is <code>null</code> or doesn't
     * exist. Also returns <code>true</code> if <code>userDTO</code> is <code>null</code> meaning
     * nothing is being updated. With this behavior when the method is used in an
     * <code>@PreAuthorize()</code> the controller method is being executed and other response codes
     * like 404 can be implemented inside controller code.
     * 
     * @param userId    the id of the user that shall be updated
     * @param userDTO   containing the fields to update
     * @return          <code>true</code> if user with <code>userId</code> can be updated with
     *                  <code>userDTO</code>
     * @see de.thb.ea.public_transport_tracker.controller.api.model.UserDTO
     * @see org.springframework.security.access.prepost.PreAuthorize
     */
    public boolean canUpdateUser(Long userId, UserDTO userDTO) {
        if (
            userId == null
            || userDTO == null
        ) {
            return true;
        }

        User userToUpdate;
        try {
            userToUpdate = userService.getUserById(userId);
        }
        catch (UserNotFoundException e) {
            // return always true if the user doesn't exist
            return true;
        }

        if (
            // cannot update id
            userDTO.getId() != null && userDTO.getId() != userId
            
            // cannot update SYSTEM user
            || isIdOfUser(userId, "SYSTEM")

            // cannot update the mentioned properties
            || userDTO.getCreatedBy() != null
                && !userDTO.getCreatedBy().equals(userToUpdate.getCreatedBy().getId())
            || userDTO.getCreatedAt() != null
                && !userDTO.getCreatedAt().equals(userToUpdate.getCreatedAt())
            || userDTO.getUpdatedAt() != null
                && !userDTO.getUpdatedAt().equals(userToUpdate.getUpdatedAt())
            || userDTO.getRefreshVersion() != null
                && !userDTO.getRefreshVersion().equals(userToUpdate.getRefreshVersion())
        ) {
            return false;
        }

        // check for admin update
        if (
            isIdOfUser(userId, adminProperties.getUsername())
                && (
                    // current user is not admin
                    !hasId(userId)
                    
                    // or admin username is being changed
                    || userDTO.getUsername() != null
                        && !userDTO.getUsername().equals(adminProperties.getUsername())
                )
        ) {
            return false;
        }

        // cannot change your own permissions
        if (
            // update yourself
            hasId(userId)
                // dont allow permissions to change
                && userDTO.getPermissions() != null
                && !userDTO.getPermissions().equals(
                    getUser().getPermissions()
                        .stream()
                        .map(e -> e.getName())
                        .collect(Collectors.toSet())
                )
        ) {
            return false;
        }

        // can only change permissions the current user has on its own
        if (userDTO.getPermissions() != null) {
            for (String permissionName : userDTO.getPermissions()) {
                // can only add permissions if current user has them on its own
                if (!userToUpdate.hasPermission(permissionName) && !hasPermission(permissionName)) {
                    return false;
                }
            }

            for (Permission permission : userToUpdate.getPermissions()) {
                // can only remove permissions if current user has them on its own
                if (
                    !userDTO.getPermissions().contains(permission.getName())
                    && !hasPermission(permission.getName())
                ) {
                    return false;
                }

            }
        }

        return hasPermission("UPDATE_USER")
            || hasId(userId); // user can update itself
    }


    /**
     * Check if current user can delete a user.
     * 
     * <p>
     * A user can delete itself. A user can delete other users if he has <i>DELETE_USER</i>
     * permissions.
     * 
     * <p>
     * <i>SYSTEM</i> user and <i>admin</i> user cannot be deletet.
     * 
     * <p>
     * <b>Note:</b> Returns always true if <code>userId</code> is <code>null</code> or doesn't
     * exist. With this behavior when the method is used in an <code>@PreAuthorize()</code> the
     * controller method is being executed and other response codes like 404 can be implemented
     * inside controller code.
     * 
     * @param userId    Id of user to delete.
     * @return          <code>true</code> if user can delete the user with <code>userId</code>;
     *                  otherwise <code>false</code>
     * @see de.thb.ea.public_transport_tracker.controller.api.model.UserDTO
     * @see org.springframework.security.access.prepost.PreAuthorize
     */
    public boolean canDeleteUser(Long userId) {
        if (userId == null || !userService.userIdExists(userId)) {
            return true;
        }

        if (
            isIdOfUser(userId, "SYSTEM")
            || isIdOfUser(userId, adminProperties.getUsername())
        ) {
            return false;
        }

        return hasPermission("DELETE_USER")
            || hasId(userId);
    }
}
