package de.thb.ea.public_transport_tracker.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @Default
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Setter
    @Default
    private Set<Permission> permissions = new HashSet<>();


    /**
     * This method can be used to ensure that the role object has no id. This way a new id is
     * generated when saving it to the repository.
     */
    public void forgetId() {
        id = null;
    }


    /**
     * Check if role has a specific permission.
     * 
     * @param permission
     * @return true if it has the permission; otherwise false.
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    
}
