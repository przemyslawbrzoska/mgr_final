package mgr.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "positions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PositionData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pos_seq")
    @SequenceGenerator(name = "pos_seq", sequenceName = "POS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "POSITION_POS_X", nullable = false)
    private int posX;

    @Column(name = "POSITION_POS_Y", nullable = false)
    private int posY;

    @Column(name = "CREATION_DATE", nullable = false)
    private LocalDateTime date;

    @Column(name = "MOBILE_CLIENT_ID", nullable = false)
    private String mobileClientIdentifier;

    @Column(name = "POSITION_ZONES")
    private String zones;

    public PositionData(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.zones = "";
    }

    public void addZone(String zoneCode) {
        this.zones += zoneCode;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "posX=" + posX +
                ", posY=" + posY +
                ", id=" + getMobileClientIdentifier() +
                '}';
    }


}
