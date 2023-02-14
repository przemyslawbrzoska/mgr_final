package mgr.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mgr.backend.entity.ZoneData;

@Getter
@Setter
@AllArgsConstructor
public class ZoneDataDTO {

    private String source;
    private String xref = "x";
    private String yref = "y";

    private Integer x;

    private Integer y;

    private Integer sizex;

    private Integer sizey;

    private String sizing = "stretch";

    private Double opacity = 0.5;

    private String layer;

    public ZoneDataDTO(ZoneData item) {
        this.source = item.getZoneUrl();
        if (item.getZoneType().equals("rect")) {
            this.x = item.getPosX();
            this.y = item.getPosY() + item.getZoneHeight();
            this.sizex = item.getZoneWidth();
            this.sizey = item.getZoneHeight();
        } else {
            this.x = item.getPosX();
            this.y = item.getPosY() + item.getZoneHeight() / 2;
            this.sizex = item.getZoneWidth() / 2;
            this.sizey = item.getZoneHeight() / 2;
        }
    }

    public ZoneDataDTO(String source, Integer x, Integer y, Integer sizex, Integer sizey) {
        this.source = source;
        this.x = x;
        this.y = y;
        this.sizex = sizex;
        this.sizey = sizey;
        this.opacity = 1.0;
        this.layer = "below";
    }
}
