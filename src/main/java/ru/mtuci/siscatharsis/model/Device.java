package ru.mtuci.siscatharsis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mac_address")
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DeviceLicense> deviceLicenses;

    public Device(String name, String macAddress, User user, List<DeviceLicense> deviceLicenses) {
        this.name = name;
        this.macAddress = macAddress;
        this.user = user;
        this.deviceLicenses = deviceLicenses;
    }

    public Device(String name, String macAddress, User user) {
        this.name = name;
        this.macAddress = macAddress;
        this.user = user;
    }
}
