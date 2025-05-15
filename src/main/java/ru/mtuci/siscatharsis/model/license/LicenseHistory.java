package ru.mtuci.siscatharsis.model.license;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import ru.mtuci.siscatharsis.model.user.User;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "licence_history")
public class LicenseHistory {

    public enum ChangeType {
        CREATE, UPDATE, ACTIVATE, RENEW
    }

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
    private ChangeType changeType;

    @Column(name = "change_date")
    private Date changeDate;
}
