package ru.mtuci.siscatharsis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "license_types")
public class LicenseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "default_duration")
    private int defaultDuration;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<License> licenses;

    public LicenseType(String name, int defaultDuration, String description, List<License> licenses) {
        this.name = name;
        this.defaultDuration = defaultDuration;
        this.description = description;
        this.licenses = licenses;
    }

    public LicenseType(String name, int defaultDuration, String description) {
        this.name = name;
        this.defaultDuration = defaultDuration;
        this.description = description;
    }
}
