package mgr.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PositionDTO {

    private Integer[] posX;

    private Integer[] posY;

    private String[] labels;

    private String message;

}
