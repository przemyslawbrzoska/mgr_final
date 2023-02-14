package mgr.backend.repository;

import mgr.backend.entity.RequestData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface RequestRepository extends CrudRepository<RequestData, Long> {

    Set<RequestData> findAllByDateAfter(LocalDateTime currentDate);

    @Modifying
    @Query(
            value = "truncate table requests",
            nativeQuery = true
    )
    void truncateRequests();
}
