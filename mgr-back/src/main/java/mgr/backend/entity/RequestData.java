package mgr.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mgr.backend.dto.RequestDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_seq")
    @SequenceGenerator(name = "req_seq", sequenceName = "REQ_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "READ_DATE", nullable = false)
    private LocalDateTime date;

    @Column(name = "MOBILE_CLIENT_ID", nullable = false)
    private String mobileClientIdentifier;
    @Column(name = "RPI_IDENTIFIER", nullable = false)
    private String rpiIdentifier;

    public RequestData(RequestDTO requestDTO) {
        this.date = LocalDateTime.ofInstant(Instant
                .ofEpochMilli(Long.parseLong(requestDTO.timestamp) * 1000), ZoneId.of("Europe/Warsaw"));
        this.mobileClientIdentifier = requestDTO.NS;
        this.rpiIdentifier = requestDTO.receiver;
    }

    public RequestData(RequestDTO requestDTO, long perfTime) {
        this.date = LocalDateTime.ofInstant(Instant
                .ofEpochMilli(perfTime * 1000), ZoneId.of("Europe/Warsaw"));
        this.mobileClientIdentifier = requestDTO.NS;
        this.rpiIdentifier = requestDTO.receiver;
    }
}
