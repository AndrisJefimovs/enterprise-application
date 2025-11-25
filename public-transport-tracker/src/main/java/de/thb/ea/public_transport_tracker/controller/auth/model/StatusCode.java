package de.thb.ea.public_transport_tracker.controller.auth.model;

public class StatusCode {
    public static final Integer SUCCESS                 = 0;
    public static final Integer USERNAME_ALREADY_TAKEN  = 1;
    public static final Integer EMAIL_ALREADY_TAKEN     = 2;
    public static final Integer USER_NOT_FOUND          = 3;
    public static final Integer INVALID_CREDENTIALS     = 4;
}
