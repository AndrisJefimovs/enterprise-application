package de.thb.ea.public_transport_tracker.controller.api.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<Long> roleIds;
    private Date createdAt;
    private Date updatedAt;
    private Integer refreshVersion;

    public static UserDTO mapFull(User user) {
        Set<Long> _roleIds = new HashSet<>();
        for (Role role : user.getRoles())
            _roleIds.add(role.getId());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleIds(_roleIds)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .refreshVersion(user.getRefreshVersion())
                .build();
    }

    public static UserDTO mapSparse(User user) {
        Set<Long> _roleIds = new HashSet<>();
        for (Role role : user.getRoles())
            _roleIds.add(role.getId());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleIds(_roleIds)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
