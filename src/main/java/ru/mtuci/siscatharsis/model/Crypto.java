package ru.mtuci.siscatharsis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.security.PrivateKey;
import java.security.PublicKey;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cryptos")
public class Crypto {
        @Id
        private Long id = 1L;

        @Column(name = "private_key", length = 1024)
        PrivateKey privateKey;

        @Column(name = "public_key", length = 1024)
        PublicKey publicKey;
}
