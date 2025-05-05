package ru.mtuci.siscatharsis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
