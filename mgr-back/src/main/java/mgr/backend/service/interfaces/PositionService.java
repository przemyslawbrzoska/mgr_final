package mgr.backend.service.interfaces;

import mgr.backend.entity.PositionData;

import java.util.List;
import java.util.Optional;

public interface PositionService {

    public void runPositionCalculation();

    Optional<PositionData> getPositionById(String id);

    List<PositionData> getPositions();
}
