package mgr.backend.service.interfaces;

import mgr.backend.entity.TileData;

import java.util.List;

public interface TileService {

    TileData saveTileData(TileData tileData);

    List<TileData> fetchTileList();

    void initTileMap();
}
