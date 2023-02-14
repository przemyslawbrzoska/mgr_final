package mgr.backend.repository;

import mgr.backend.entity.RpiData;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Order(value = 96)
public interface RpiRepository extends CrudRepository<RpiData, Long> {
    List<RpiData> findByCodeIn(Set<String> codes);
}
