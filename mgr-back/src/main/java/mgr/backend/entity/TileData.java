package mgr.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Entity
@Table(name = "TILES")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TileData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tiles_seq")
    @SequenceGenerator(name = "tiles_seq", sequenceName = "TILES_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "POS_X", nullable = false)
    private int posX;

    @Column(name = "POS_Y", nullable = false)
    private int posY;


    public TileData(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileData tileData = (TileData) o;
        return posX == tileData.posX && posY == tileData.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }
}
