package mgr.backend.repository;

import mgr.backend.entity.TileData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TileRepository extends CrudRepository<TileData, Long> {

    public TileData findByPosXAndPosY(int posX, int posY);
}
