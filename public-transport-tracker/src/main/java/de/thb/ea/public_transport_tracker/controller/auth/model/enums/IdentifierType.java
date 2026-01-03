package de.thb.ea.public_transport_tracker.controller.auth.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum IdentifierType {
    USERNAME("username"),
    EMAIL("email");

    private final String value;

    IdentifierType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static IdentifierType from(String value) {
        return Arrays.stream(values())
                .filter(t -> t.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Invalid identifier type: " + value));
    }

}
