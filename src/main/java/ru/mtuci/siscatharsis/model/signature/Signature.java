package ru.mtuci.siscatharsis.model.signature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import ru.mtuci.siscatharsis.utils.MediaUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signatures")
// TODO: размеры фиксировать строго!!! так проще объекты сервер-к-клиенту обрабатывать
// TODO: + эцп стоит делать байтовым массивом а не строкой в base64!!!
// TODO: пересмотреть байтовые параметры, remainderHash и firstBytes
public class Signature {
        public enum Status {
                ACTUAL, DELETED, CORRUPTED
        }

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(name = "threat_name", nullable = false)
        private String threatName;

        @Column(name = "first_bytes")
        private byte[] firstBytes;

        @Column(name = "remainder_hash")
        private byte[] remainderHash;

        @Column(name = "remainder_length")
        private int remainderLength;

        @Column(name = "file_type")
        private String fileType;

        @Column(name = "offset_start")
        private int offsetStart;

        @Column(name = "offset_end")
        private int offsetEnd;

        @Column(name = "digital_signature")
        private byte[] digitalSignature;

        @Column(name = "status")
        private Status status;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private Instant updatedAt;

        @Version
        private Integer version;

        // in signing we ignore: guid, current status, updating time and digital signature itself
        @JsonIgnore
        // funny jackson quirks
        // any method that starts as get, acts as field when serialized
        public byte[] getBodyForSigning() throws IOException {
                ByteArrayOutputStream data = new ByteArrayOutputStream();

                data.write(MediaUtil.stringToByte(threatName));
                data.write(firstBytes);
                data.write(remainderHash);
                data.write(MediaUtil.intToByte(remainderLength));
                data.write(MediaUtil.stringToByte(fileType));
                data.write(MediaUtil.intToByte(offsetStart));
                data.write(MediaUtil.intToByte(offsetEnd));

                return data.toByteArray();
        }


}
