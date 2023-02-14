package mgr.backend.service.interfaces;

import mgr.backend.entity.RpiData;

import java.util.List;
import java.util.Set;

public interface RpiService {

    RpiData saveRpiData(RpiData requestData);

    List<RpiData> fetchRpiList();

    List<RpiData> fetchRpiListByCode(Set<String> code);

    RpiData updateRpiData(RpiData rpiData, Long requestDataId);

    //no delete
}
