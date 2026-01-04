package de.thb.ea.public_transport_tracker.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "trips",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"remote_id", "remote_origin"})
    }
)
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false, name = "remote_id")
    @Setter
    private String remoteId;

    @Column(length = 16, nullable = false, name = "remote_origin")
    @Setter
    private String remoteOrigin;

    @Column(length = 64)
    @Setter
    private String direction;

    @Column(length = 16)
    @Setter
    private String lineName;

    @Column(length = 32)
    @Setter
    private String type;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * This method can be used to ensure a that the object has no id. This way a new id
     * is generated when saving it to the repository.
     */
    public void forgetId() {
        id = null;
    }

}
