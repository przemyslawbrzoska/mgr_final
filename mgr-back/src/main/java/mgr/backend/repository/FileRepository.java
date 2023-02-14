package mgr.backend.repository;

import mgr.backend.entity.FileData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<FileData, Long> {

    FileData findByType(String code);

    FileData findByName(String name);

    int deleteAllByType(String code);
}
