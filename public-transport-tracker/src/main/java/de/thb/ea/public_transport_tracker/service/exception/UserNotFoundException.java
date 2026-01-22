package de.thb.ea.public_transport_tracker.service.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends ServiceException {

    private Long userId;
    private String username;
    private String email;

    public UserNotFoundException(String msg) {
        super(msg);
        userId = null;
        username = null;
        email = null;
    }

    public static UserNotFoundException fromUserId(Long userId) {
        UserNotFoundException e = new UserNotFoundException(
            String.format("No user found with id %d", userId)
        );
        e.setUserId(userId);
        return e;
    }

    public static UserNotFoundException fromUsername(String username) {
        UserNotFoundException e = new UserNotFoundException(
            String.format("No user found with username '%s'", username)
        );
        e.setUsername(username);
        return e;
    }

    public static UserNotFoundException fromEmail(String email) {
        UserNotFoundException e = new UserNotFoundException(
            String.format("No user found with email '%s'", email)
        );
        e.setEmail(email);
        return e;
    }

    private void setUserId(Long userId) {
        this.userId = userId;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setEmail(String email) {
        this.email = email;
    }
    
}
