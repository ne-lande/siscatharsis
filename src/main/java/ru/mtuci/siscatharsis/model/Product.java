package ru.mtuci.siscatharsis.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "products")
@JsonIgnoreProperties({"licenses"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<License> licenses;

    public Product(String name, boolean isBlocked, List<License> licenses) {
        this.name = name;
        this.isBlocked = isBlocked;
        this.licenses = licenses;
    }

    public Product(String name, boolean isBlocked) {
        this.name = name;
        this.isBlocked = isBlocked;
    }
}
