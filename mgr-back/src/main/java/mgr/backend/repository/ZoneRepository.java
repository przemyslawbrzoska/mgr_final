package mgr.backend.repository;

import mgr.backend.entity.ZoneData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends CrudRepository<ZoneData, Long> {

    public void deleteByCode(String code);

}
