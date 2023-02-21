package mgr.backend.service;

import jakarta.annotation.PostConstruct;
import mgr.backend.configuration.ConfigProperties;
import mgr.backend.entity.RpiData;
import mgr.backend.entity.TileData;
import mgr.backend.repository.RpiRepository;
import mgr.backend.repository.TileRepository;
import mgr.backend.service.interfaces.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TileServiceImpl implements TileService {

    Logger logger = LoggerFactory.getLogger(TileServiceImpl.class);
    private final ConfigProperties configProperties;

    @Autowired
    private TileRepository tileRepository;

    @Autowired
    private RpiRepository rpiRepository;

    @Autowired
    public TileServiceImpl(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public TileData saveTileData(TileData requestData) {
        return tileRepository.save(requestData);
    }

    @Override
    public List<TileData> fetchTileList() {
        return (List<TileData>) tileRepository.findAll();
    }


    HashSet<TileData> allTiles = new HashSet<>();

    //not tested
    public void importTileMap(InputStream myObj) {
        int iterator = 0;

        logger.info("Start file import");
        tileRepository.deleteAll();
        logger.info("Tiles deleted");
        rpiRepository.deleteAll();
        logger.info("RPIS deleted");
        InputStreamReader streamReader = new InputStreamReader(myObj, StandardCharsets.UTF_8);
        BufferedReader myReader = new BufferedReader(streamReader);
        try {

            Set<TileData> tileDataSet = new HashSet<>();
            Map<String, RpiData> rpiDataSet = new HashMap<>();
            for (String line; (line = myReader.readLine()) != null; ) {
                String data = line;
                String[] inputLine = data.split("\\|");

                switch (inputLine[0]) {
                    case "r":
                        //init rpi
                        //kolejne   linie pliku to polozenie raspberry, code raspberry, pokryte kafle
                        //polozenie raspberry jest nonmnadatory, zbedne w pozycjonowaniu, ale moze ulatawiac ewentualne szukanie problemow
                        RpiData item = new RpiData();
                        item.setOriginalXPos(Integer.valueOf(inputLine[2]));
                        item.setOriginalYPos(Integer.valueOf(inputLine[3]));
                        item.setCode(inputLine[1]);
                        item.setDate(new Date());
                        rpiDataSet.put(inputLine[1], item);
                        break;
                    case "p":
                        TileData tile = new TileData(Integer.valueOf(inputLine[2]), Integer.valueOf(inputLine[3]));
                        TileData savedTile = tileRepository.findByPosXAndPosY(tile.getPosX(), tile.getPosY());
                        if (Objects.isNull(savedTile)) {
                            logger.info("Tile saved x:" + tile.getPosX() + " y: " + tile.getPosY());
                            savedTile = tileRepository.save(tile);
                        }
                        rpiDataSet.get(inputLine[1]).getTilesCovered().add(savedTile);
                        logger.info("rpiDataset" + iterator++);
                        break;
                }
            }
            rpiRepository.saveAll(rpiDataSet.values());
            logger.info("All rpis saved");
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @PostConstruct
    public void initTileMap() {
        if (configProperties.isRunInit()) {


            tileRepository.deleteAll();
            logger.info("Tiles deleted");
     
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    TileData item = new TileData(i, j);
                    allTiles.add(item);
                    tileRepository.save(item);
                }
                logger.info("Tiles created: " + i * 100);
            }
          
            rpiRepository.deleteAll();
            logger.info("RPIS deleted");
            
            int iterator = 0;
            for (int i = 0; i <= 10; i++) {
                for (int j = 0; j <= 10; j++) {
                    RpiData item = new RpiData();
                    item.setOriginalXPos(10 * i);
                    item.setOriginalYPos(10 * j);
                    item.setCode(createRpiIdentifier(iterator, i * 10, j * 10));
                    item.setTilesCovered(getCoveredTilesInRange(10 * i, 10 * j, 10));

                    item.setDate(new Date());
                    rpiRepository.save(item);
                    iterator++;
                }
                logger.info("RPIS created: " + i * 10);
            }
        }
    }

    private String createRpiIdentifier(int iterator, int i, int j) {
        String sb = iterator +
                "pos_x=" +
                i +
                "pos_y=" +
                j;
        return sb;
    }

    private Set<TileData> getCoveredTilesInRange(int i, int j, int range) {
        Set<TileData> result = allTiles.stream().filter(tile -> tileInRange(tile.getPosX(), tile.getPosY(), i, j, range)).collect(Collectors.toSet());
        return result;
    }

    private boolean tileInRange(int x, int y, int centerX, int centerY, int range) {
        return (Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2)) < (range * range);
    }


}
