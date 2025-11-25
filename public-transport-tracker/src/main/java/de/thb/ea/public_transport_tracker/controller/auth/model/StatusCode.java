package de.thb.ea.public_transport_tracker.controller.auth.model;

public enum StatusCode {
    SUCCESS (0),
    USERNAME_ALREADY_TAKEN (1),
    EMAIL_ALREADY_TAKEN (2),
    USER_NOT_FOUND (3),
    INVALID_CREDENTIALS (4);

    private final int index;

    StatusCode(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
