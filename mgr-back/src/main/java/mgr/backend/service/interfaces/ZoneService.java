package mgr.backend.service.interfaces;

import mgr.backend.dto.ZoneDataDTO;
import mgr.backend.entity.ZoneData;

import java.io.IOException;
import java.util.List;

public interface ZoneService {

    ZoneData saveZoneData(ZoneData zoneData) throws IOException;

    List<ZoneData> fetchZonesList();
    List<ZoneDataDTO> fetchZonesDTOList();

    void deleteZone(String code);
    ZoneData fetchZone(Long id);
}
