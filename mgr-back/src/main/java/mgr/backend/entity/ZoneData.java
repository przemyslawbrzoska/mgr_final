package mgr.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.awt.*;

@Entity
@Table(name = "ZONES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ZoneData {

    public enum type {
        RECTANGLE("rect"),
        CIRCLE("circ");
        public final String label;

        private type(String label) {
            this.label = label;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZONE_SEQ")
    @SequenceGenerator(name = "ZONE_SEQ", sequenceName = "ZONE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "ZONE_IDENTIFIER", nullable = false)
    private String code;

    @Column(name = "ZONE_POS_X", nullable = false)
    private Integer posX;

    @Column(name = "ZONE_POS_Y", nullable = false)
    private Integer posY;
    @Column(name = "ZONE_HEIGHT", nullable = false)
    private Integer zoneHeight;

    @Column(name = "ZONE_WIDTH", nullable = false)
    private Integer zoneWidth;

    @Column(name = "ZONE_OCCURENCES")
    private Integer zoneOccurences;

    @Column(name = "ZONE_TYPE")
    private String zoneType;

    @Transient
    @JsonIgnore
    private Color color;

    @Transient
    private String zoneURL;

    public String getZoneUrl() {
        return "http://localhost:8080/zone/img/" + getId();
    }

    public void incrementZoneOccurences() {
        zoneOccurences++;
    }

    public boolean isPositionInRange(PositionData position) {
        if (this.zoneType.equals(type.RECTANGLE)) {
            return ((position.getPosX() >= this.getPosX() && position.getPosX() <= this.getPosX() + this.getZoneWidth())
                    && (position.getPosY() >= this.getPosY() && position.getPosY() <= this.getPosY() + this.getZoneHeight()));
        } else {
            return (Math.pow((this.getPosX() - position.getPosX()), 2) + Math.pow((this.getPosY() - position.getPosY()), 2))
                    < (this.getZoneHeight() * this.getZoneHeight());
        }

    }

}
