package ru.mtuci.siscatharsis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "licenses")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, updatable = false)
    private UUID code;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id")
    @JsonBackReference
    private LicenseType type;

    @Column(name = "first_activation_date")
    private Date firstActivationDate;

    @Column(name = "ending_date")
    private Date endingDate;

    @Column(name = "is_blocked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isBlocked = false;

    @Column(name = "devices_count")
    private int devicesCount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private User owner;

    @Column(name = "duration")
    private int duration;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceLicense> deviceLicenses;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LicenseHistory> licenseHistories;

    public License(Long id, UUID code, User user, Product product, LicenseType type, Date firstActivationDate, Date endingDate, Boolean isBlocked, int devicesCount, User owner, int duration, String description) {
        this.id = id;
        this.code = code;
        this.user = user;
        this.product = product;
        this.type = type;
        this.firstActivationDate = firstActivationDate;
        this.endingDate = endingDate;
        this.isBlocked = isBlocked;
        this.devicesCount = devicesCount;
        this.owner = owner;
        this.duration = duration;
        this.description = description;
    }

    // For creating in license service
    public License(User owner, Product product, LicenseType type) {
        this.code = UUID.randomUUID();

        this.product = product;
        this.type = type;
        this.owner = owner;

        this.devicesCount = type.getDeviceCount();
        this.duration = type.getDuration();
        this.description = type.getDescription();
    }
}
