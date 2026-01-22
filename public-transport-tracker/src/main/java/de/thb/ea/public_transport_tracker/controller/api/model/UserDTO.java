package de.thb.ea.public_transport_tracker.controller.api.model;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import de.thb.ea.public_transport_tracker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<String> permissions;
    private Long createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer refreshVersion;

    public static UserDTO map(User user) {
        Long createdBy = null;
        if (user.getCreatedBy() != null) {
            createdBy = user.getCreatedBy().getId();
        }

        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .permissions(user.getPermissions().stream().map(e -> e.getName())
                .collect(Collectors.toSet()))
            .createdBy(createdBy)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .refreshVersion(user.getRefreshVersion())
            .build();
    }
}
