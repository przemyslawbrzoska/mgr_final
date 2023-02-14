package mgr.backend.controller;

import mgr.backend.dto.PositionDTO;
import mgr.backend.entity.PositionData;
import mgr.backend.service.PositionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@RestController
@Order(value = 100)
@RequestMapping("/position")
public class PositionController {
    @Autowired
    private PositionServiceImpl positionService;

    String pattern = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("/allpositions")
    @CrossOrigin("*")
    public PositionDTO readPositions() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        ArrayList<PositionData> list = (ArrayList<PositionData>) positionService.getPositions();
        Integer[] x_pos = list.stream().map(x -> x.getPosX()).toArray(Integer[]::new);
        Integer[] y_pos = list.stream().map(x -> x.getPosY()).toArray(Integer[]::new);
        String[] identifier = list.stream().map(x -> x.getMobileClientIdentifier()).toArray(String[]::new);
        ArrayList<String> messages = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (!list.isEmpty()) {
            sb.append(simpleDateFormat.format(new Date()));
        }
        for (PositionData item : list) {
            sb.append("#Object ");
            sb.append(item.getMobileClientIdentifier());
            sb.append(" at position: (");
            sb.append(item.getPosX());
            sb.append(",");
            sb.append(item.getPosY());
            sb.append(") ");
        }


        return new PositionDTO(x_pos, y_pos, identifier, sb.toString());
    }

}

