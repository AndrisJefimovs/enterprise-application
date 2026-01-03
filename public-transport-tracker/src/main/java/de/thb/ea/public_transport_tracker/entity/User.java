package de.thb.ea.public_transport_tracker.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;


@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, length = 24, nullable = false)
    @Setter
    private String username;

    @Column(unique = true, length = 127, nullable = false)
    @Setter
    private String email;

    @Column(length = 255)
    @Setter
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_permissions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Setter
    @Default
    private Set<Permission> permissions = new HashSet();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Default
    @Column(name = "refresh_version")
    @Setter
    private Integer refreshVersion = 0;

    @Column(name = "login_enabled")
    @Setter
    @Default
    private Boolean loginEnabled = true;


    /**
     * This method can be used to ensure that the user object has no id. This way a new id is
     * generated when saving it to the database.
     */
    public void forgetId() {
        id = null;
    }

    /**
     * Check if user has a specific permission.
     * 
     * @param permission
     * @return true if it has the permission; otherwise false.
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * This function returns all granted authorities of a user. Granted authorities can either be
     * roles "ROLE_<ROLE_NAME>" or permissions "PERM_<PERMISSION_NAME>"
     * 
     * @return collection of all graneted authorities 
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // add permission authority
        for (Permission permission : permissions) {
            authorities.add(new SimpleGrantedAuthority("PERM_" + permission.getName()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isLoginEnabled() {
        return loginEnabled && password != null;
    }
}
