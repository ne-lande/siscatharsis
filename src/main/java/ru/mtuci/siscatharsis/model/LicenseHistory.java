package ru.mtuci.siscatharsis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "licence_history")
public class LicenseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "license_id")
    @JsonBackReference
    private License license;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(name = "status")
    private String status;

    @Column(name = "change_date")
    private Date changeDate;

    @Column(name = "description")
    private String description;

    public LicenseHistory(License license, User user, String status, Date changeDate, String description) {
        this.license = license;
        this.user = user;
        this.status = status;
        this.changeDate = changeDate;
        this.description = description;
    }

    public static LicenseHistory activate(License license) {
        return new LicenseHistory(license, license.getOwner(), "ACTIVATED", new Date(), "License activated by user");
    }

    public static LicenseHistory UpdateOnDevice(License license) {
        return new LicenseHistory(license, license.getOwner(), "UPDATED", new Date(), "License updated on device");
    }

    public static LicenseHistory create(License license) {
        return new LicenseHistory(license, license.getOwner(), "CREATED", new Date(), "License created by admin");
    }

    public static LicenseHistory update(License license) {
        return new LicenseHistory(license, license.getOwner(), "UPDATED", new Date(), "License updated by admin");
    }
}
