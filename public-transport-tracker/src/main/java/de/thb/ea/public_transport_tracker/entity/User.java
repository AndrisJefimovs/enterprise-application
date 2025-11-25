package de.thb.ea.public_transport_tracker.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Column(unique = true, length = 64, nullable = false)
    private String username;

    @Column(unique = true, length = 128, nullable = false)
    private String email;

    @Column(nullable = false)
    @Setter
    private String password;

    @Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Default
    @Column(name = "refresh_version")
    private Integer refreshVersion = 0;

    // the latest point where a refresh is possible (the time the last refresh token expires)
    @Column(name = "refresh_stop")
    private Date refreshStop;

    
    /**
     * This method sets and returns a new valid refresh version.
     * 
     * @return New refresh version
     */
    public Integer nextRefreshVersion() {
        refreshVersion += 1;
        return refreshVersion;
    }

    /**
     * Update the refreshStop if the new refreshStop is later then the old one.
     * 
     * This setter ensures that the refreshStop is always the time when all refresh tokens are
     * expired.
     * 
     * @param refreshStop The expiration time of a refresh token.
     * @throws IllegalArgumentException If the argument is null.
     */
    public void setRefreshStop(Date refreshStop) throws IllegalArgumentException {
        if (refreshStop == null)
            throw new IllegalArgumentException("refreshStop must not be null");

        if (this.refreshStop == null || this.refreshStop.before(refreshStop)) {
            this.refreshStop = refreshStop;
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                // get set of role names (strings)
                .stream().map(Role::getName).collect(Collectors.toSet())
                // get list of GrantedAuthorities
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
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
}
