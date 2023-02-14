package mgr.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "raspberries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RpiData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rpi_seq")
    @SequenceGenerator(name = "rpi_seq", sequenceName = "rpi_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CREATION_DATE", nullable = true)
    private Date date;

    @Column(name = "RPI_IDENTIFIER", nullable = false)
    private String code;

    @Column(name = "RPI_POS_X", nullable = false)
    private Integer originalXPos;

    @Column(name = "RPI_POS_Y", nullable = false)
    private Integer originalYPos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "TILES_COVERED",
            joinColumns = @JoinColumn(name = "RPI_DATA_ID"),
            inverseJoinColumns = @JoinColumn(name = "TILE_DATA_ID")
    )
    @JsonIgnore
    private Set<TileData> tilesCovered = new HashSet<>();

    public RpiData(Date date, Integer originalXPos, Integer originalYPos, Set<TileData> tilesCovered) {
        this.date = date;
        this.code = "0" + "pos_x=" + originalXPos + "pos_y=" + originalYPos;
        this.originalXPos = originalXPos;
        this.originalYPos = originalYPos;
        this.tilesCovered = tilesCovered;
    }
}
