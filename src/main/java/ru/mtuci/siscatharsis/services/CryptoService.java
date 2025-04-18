package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.Crypto;
import ru.mtuci.siscatharsis.repositories.CryptoRepository;
import ru.mtuci.siscatharsis.utils.CryptoUtil;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

@Service
public class CryptoService {

        private final CryptoRepository cryptoRepository;

        @Autowired
        public CryptoService(CryptoRepository cryptoRepository) {
                this.cryptoRepository = cryptoRepository;
        }

        public Crypto generateNewKeypair() throws NoSuchAlgorithmException {
                KeyPair newKeyPair = CryptoUtil.generateNewKeypair();

                Crypto crypto = Crypto.builder()
                        .id(1L)
                        .privateKey(newKeyPair.getPrivate())
                        .publicKey(newKeyPair.getPublic())
                        .build();

                cryptoRepository.save(crypto);

                return crypto;
        }

        public Crypto getCurrentKeypair() {
                Crypto crypto = cryptoRepository.findById(1L).orElseThrow(() -> new RuntimeException("smh happened"));

                return crypto;
        }

        public String signWithCurrent(String input) throws Exception {
                Crypto crypto = this.getCurrentKeypair();
                PrivateKey privateKey = crypto.getPrivateKey();
                return CryptoUtil.sign(privateKey, input);
        }
}
