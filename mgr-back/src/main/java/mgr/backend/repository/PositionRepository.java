package mgr.backend.repository;

import mgr.backend.entity.PositionData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PositionRepository extends CrudRepository<PositionData, Long> {

    public PositionData findTopByMobileClientIdentifierOrderByDateDesc(String mobileClientIdentifier);

    @Query(value = "SELECT DISTINCT ON (MOBILE_CLIENT_ID) MOBILE_CLIENT_ID, ID," +
            " CREATION_DATE, POSITION_POS_X, POSITION_POS_Y, POSITION_ZONES FROM POSITIONS" +
            " WHERE CREATION_DATE > current_timestamp - interval '30 seconds' ORDER BY MOBILE_CLIENT_ID, id DESC",
            nativeQuery = true)
    List<PositionData> findDistinctPositions();
}
