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

            //normalnie to powinno zostać wprowadzone do systemu jako wynik pomiarów
            //zakładamy pole 100x100, rpi co 10m(11 czujników w linii), zasięg rpi "okragly"
            //wyczysc tabele
            tileRepository.deleteAll();
            logger.info("Tiles deleted");
            //init kwadratow
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    TileData item = new TileData(i, j);
                    allTiles.add(item);
                    tileRepository.save(item);
                }
                logger.info("Tiles created: " + i * 100);
            }
            //wyczysc rpi
            rpiRepository.deleteAll();
            logger.info("RPIS deleted");
            //init rpi
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

////
//            Pattern patternX = Pattern.compile("=(.*?)p");
//            Pattern patternY = Pattern.compile("y=(.*?)b");
//
//            for (String item : rpiIdentifiers5) {
//                Matcher matcherX = patternX.matcher(item);
//                Matcher matcherY = patternY.matcher(item);
//                matcherX.find();
//                matcherY.find();
//                rpiRepository.save(new RpiData(new Date(),
//                        Integer.valueOf(matcherX.group(1)),
//                        Integer.valueOf(matcherY.group(1)),
//                        getCoveredTilesInRange(Integer.valueOf(matcherX.group(1)), Integer.valueOf(matcherY.group(1)), 75)
//                        ));
//
//
//            }


//            rpiRepository.save(new RpiData(new Date(), 0, 0, getCoveredTilesInRange(0, 0, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 12, getCoveredTilesInRange(0, 20, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 40, getCoveredTilesInRange(0, 40, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 60, getCoveredTilesInRange(0, 60, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 80, getCoveredTilesInRange(0, 80, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 100, getCoveredTilesInRange(0, 100, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 100, getCoveredTilesInRange(0, 100, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 100, getCoveredTilesInRange(0, 100, 20)));
//            rpiRepository.save(new RpiData(new Date(), 0, 100, getCoveredTilesInRange(0, 100, 20)));
//
//            rpiRepository.save(new RpiData(new Date(), 20, 0,  getCoveredTilesInRange(20, 0, 20)));
//            rpiRepository.save(new RpiData(new Date(), 20, 20, getCoveredTilesInRange(20, 20, 20)));
//            rpiRepository.save(new RpiData(new Date(), 20, 40, getCoveredTilesInRange(20, 40, 20)));
//            rpiRepository.save(new RpiData(new Date(), 20, 60, getCoveredTilesInRange(20, 60, 20)));
//            rpiRepository.save(new RpiData(new Date(), 20, 80, getCoveredTilesInRange(20, 80, 20)));
//            rpiRepository.save(new RpiData(new Date(), 20, 100, getCoveredTilesInRange(20, 100, 20)));
//
//            rpiRepository.save(new RpiData(new Date(), 40, 0,  getCoveredTilesInRange(40, 0, 20)));
//            rpiRepository.save(new RpiData(new Date(), 40, 20, getCoveredTilesInRange(40, 20, 20)));
//            rpiRepository.save(new RpiData(new Date(), 40, 40, getCoveredTilesInRange(40, 40, 20)));
//            rpiRepository.save(new RpiData(new Date(), 40, 60, getCoveredTilesInRange(40, 60, 20)));
//            rpiRepository.save(new RpiData(new Date(), 40, 80, getCoveredTilesInRange(40, 80, 20)));
//            rpiRepository.save(new RpiData(new Date(), 40, 100, getCoveredTilesInRange(40, 100, 20)));
//
//            rpiRepository.save(new RpiData(new Date(), 60, 0,  getCoveredTilesInRange(60, 0, 20)));
//            rpiRepository.save(new RpiData(new Date(), 60, 20, getCoveredTilesInRange(60, 20, 20)));
//            rpiRepository.save(new RpiData(new Date(), 60, 40, getCoveredTilesInRange(60, 40, 20)));
//            rpiRepository.save(new RpiData(new Date(), 60, 60, getCoveredTilesInRange(60, 60, 20)));
//            rpiRepository.save(new RpiData(new Date(), 60, 80, getCoveredTilesInRange(60, 80, 20)));
//            rpiRepository.save(new RpiData(new Date(), 60, 100, getCoveredTilesInRange(60, 100, 20)));
//
//            rpiRepository.save(new RpiData(new Date(), 80, 0,  getCoveredTilesInRange(80, 0, 20)));
//            rpiRepository.save(new RpiData(new Date(), 80, 20, getCoveredTilesInRange(80, 20, 20)));
//            rpiRepository.save(new RpiData(new Date(), 80, 40, getCoveredTilesInRange(80, 40, 20)));
//            rpiRepository.save(new RpiData(new Date(), 80, 60, getCoveredTilesInRange(80, 60, 20)));
//            rpiRepository.save(new RpiData(new Date(), 80, 80, getCoveredTilesInRange(80, 80, 20)));
//            rpiRepository.save(new RpiData(new Date(), 80, 100, getCoveredTilesInRange(80, 100, 20)));
//
//            rpiRepository.save(new RpiData(new Date(), 100, 0,  getCoveredTilesInRange(100, 0, 20)));
//            rpiRepository.save(new RpiData(new Date(), 100, 20, getCoveredTilesInRange(100, 20, 20)));
//            rpiRepository.save(new RpiData(new Date(), 100, 40, getCoveredTilesInRange(100, 40, 20)));
//            rpiRepository.save(new RpiData(new Date(), 100, 60, getCoveredTilesInRange(100, 60, 20)));
//            rpiRepository.save(new RpiData(new Date(), 100, 80, getCoveredTilesInRange(100, 80, 20)));
//            rpiRepository.save(new RpiData(new Date(), 100, 100, getCoveredTilesInRange(100, 100, 20)));
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

    public void drawCoverage(RpiData rpi) {

    }

    String[] rpiIdentifiers5 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=100b",
            "0pos_x=50pos_y=50b",
            "0pos_x=100pos_y=0b",
            "0pos_x=100pos_y=100b",

    };

    String[] rpiIdentifiers9 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=50b",
            "0pos_x=0pos_y=100b",

            "0pos_x=50pos_y=0b",
            "0pos_x=50pos_y=50b",
            "0pos_x=50pos_y=100b",

            "0pos_x=100pos_y=0b",
            "0pos_x=100pos_y=50b",
            "0pos_x=100pos_y=100b",

    };

    String[] rpiIdentifiers16 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=33b",
            "0pos_x=0pos_y=66b",
            "0pos_x=0pos_y=99b",

            "0pos_x=33pos_y=0b",
            "0pos_x=33pos_y=33b",
            "0pos_x=33pos_y=66b",
            "0pos_x=33pos_y=99b",

            "0pos_x=66pos_y=0b",
            "0pos_x=66pos_y=33b",
            "0pos_x=66pos_y=66b",
            "0pos_x=66pos_y=99b",

            "0pos_x=99pos_y=0b",
            "0pos_x=99pos_y=33b",
            "0pos_x=99pos_y=66b",
            "0pos_x=99pos_y=99b",

    };


    String[] rpiIdentifiers25 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=25b",
            "0pos_x=0pos_y=50b",
            "0pos_x=0pos_y=75b",
            "0pos_x=0pos_y=100b",

            "0pos_x=25pos_y=0b",
            "0pos_x=25pos_y=25b",
            "0pos_x=25pos_y=50b",
            "0pos_x=25pos_y=75b",
            "0pos_x=25pos_y=100b",

            "0pos_x=50pos_y=0b",
            "0pos_x=50pos_y=25b",
            "0pos_x=50pos_y=50b",
            "0pos_x=50pos_y=75b",
            "0pos_x=50pos_y=100b",

            "0pos_x=75pos_y=0b",
            "0pos_x=75pos_y=25b",
            "0pos_x=75pos_y=50b",
            "0pos_x=75pos_y=75b",
            "0pos_x=75pos_y=100b",

            "0pos_x=100pos_y=0b",
            "0pos_x=100pos_y=25b",
            "0pos_x=100pos_y=50b",
            "0pos_x=100pos_y=75b",
            "0pos_x=100pos_y=100b",

    };
    String[] rpiIdentifiers36 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=20b",
            "0pos_x=0pos_y=40b",
            "0pos_x=0pos_y=60b",
            "0pos_x=0pos_y=80b",
            "0pos_x=0pos_y=100b",

            "0pos_x=20pos_y=0b",
            "0pos_x=20pos_y=20b",
            "0pos_x=20pos_y=40b",
            "0pos_x=20pos_y=60b",
            "0pos_x=20pos_y=80b",
            "0pos_x=20pos_y=100b",

            "0pos_x=40pos_y=0b",
            "0pos_x=40pos_y=20b",
            "0pos_x=40pos_y=40b",
            "0pos_x=40pos_y=60b",
            "0pos_x=40pos_y=80b",
            "0pos_x=40pos_y=100b",

            "0pos_x=60pos_y=0b",
            "0pos_x=60pos_y=20b",
            "0pos_x=60pos_y=40b",
            "0pos_x=60pos_y=60b",
            "0pos_x=60pos_y=80b",
            "0pos_x=60pos_y=100b",

            "0pos_x=80pos_y=0b",
            "0pos_x=80pos_y=20b",
            "0pos_x=80pos_y=40b",
            "0pos_x=80pos_y=60b",
            "0pos_x=80pos_y=80b",
            "0pos_x=80pos_y=100b",

            "0pos_x=100pos_y=0b",
            "0pos_x=100pos_y=20b",
            "0pos_x=100pos_y=40b",
            "0pos_x=100pos_y=60b",
            "0pos_x=100pos_y=80b",
            "0pos_x=100pos_y=100b",
    };

    String[] rpiIdentifiers49 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=16b",
            "0pos_x=0pos_y=32b",
            "0pos_x=0pos_y=48b",
            "0pos_x=0pos_y=64b",
            "0pos_x=0pos_y=80b",
            "0pos_x=0pos_y=96b",

            "0pos_x=16pos_y=0b",
            "0pos_x=16pos_y=16b",
            "0pos_x=16pos_y=32b",
            "0pos_x=16pos_y=48b",
            "0pos_x=16pos_y=64b",
            "0pos_x=16pos_y=80b",
            "0pos_x=16pos_y=96b",

            "0pos_x=32pos_y=0b",
            "0pos_x=32pos_y=16b",
            "0pos_x=32pos_y=32b",
            "0pos_x=32pos_y=48b",
            "0pos_x=32pos_y=64b",
            "0pos_x=32pos_y=80b",
            "0pos_x=32pos_y=96b",

            "0pos_x=48pos_y=0b",
            "0pos_x=48pos_y=16b",
            "0pos_x=48pos_y=32b",
            "0pos_x=48pos_y=48b",
            "0pos_x=48pos_y=64b",
            "0pos_x=48pos_y=80b",
            "0pos_x=48pos_y=96b",

            "0pos_x=64pos_y=0b",
            "0pos_x=64pos_y=16b",
            "0pos_x=64pos_y=32b",
            "0pos_x=64pos_y=48b",
            "0pos_x=64pos_y=64b",
            "0pos_x=64pos_y=80b",
            "0pos_x=64pos_y=96b",

            "0pos_x=80pos_y=0b",
            "0pos_x=80pos_y=16b",
            "0pos_x=80pos_y=32b",
            "0pos_x=80pos_y=48b",
            "0pos_x=80pos_y=64b",
            "0pos_x=80pos_y=80b",
            "0pos_x=80pos_y=96b",

            "0pos_x=96pos_y=0b",
            "0pos_x=96pos_y=16b",
            "0pos_x=96pos_y=32b",
            "0pos_x=96pos_y=48b",
            "0pos_x=96pos_y=64b",
            "0pos_x=96pos_y=80b",
            "0pos_x=96pos_y=96b",
    };

    String[] rpiIdentifiers64 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=14b",
            "0pos_x=0pos_y=28b",
            "0pos_x=0pos_y=42b",
            "0pos_x=0pos_y=56b",
            "0pos_x=0pos_y=70b",
            "0pos_x=0pos_y=84b",
            "0pos_x=0pos_y=98b",

            "0pos_x=14pos_y=0b",
            "0pos_x=14pos_y=14b",
            "0pos_x=14pos_y=28b",
            "0pos_x=14pos_y=42b",
            "0pos_x=14pos_y=56b",
            "0pos_x=14pos_y=70b",
            "0pos_x=14pos_y=84b",
            "0pos_x=14pos_y=98b",

            "0pos_x=28pos_y=0b",
            "0pos_x=28pos_y=14b",
            "0pos_x=28pos_y=28b",
            "0pos_x=28pos_y=42b",
            "0pos_x=28pos_y=56b",
            "0pos_x=28pos_y=70b",
            "0pos_x=28pos_y=84b",
            "0pos_x=28pos_y=98b",

            "0pos_x=42pos_y=0b",
            "0pos_x=42pos_y=14b",
            "0pos_x=42pos_y=28b",
            "0pos_x=42pos_y=42b",
            "0pos_x=42pos_y=56b",
            "0pos_x=42pos_y=70b",
            "0pos_x=42pos_y=84b",
            "0pos_x=42pos_y=98b",

            "0pos_x=56pos_y=0b",
            "0pos_x=56pos_y=14b",
            "0pos_x=56pos_y=28b",
            "0pos_x=56pos_y=42b",
            "0pos_x=56pos_y=56b",
            "0pos_x=56pos_y=70b",
            "0pos_x=56pos_y=84b",
            "0pos_x=56pos_y=98b",

            "0pos_x=70pos_y=0b",
            "0pos_x=70pos_y=14b",
            "0pos_x=70pos_y=28b",
            "0pos_x=70pos_y=42b",
            "0pos_x=70pos_y=56b",
            "0pos_x=70pos_y=70b",
            "0pos_x=70pos_y=84b",
            "0pos_x=70pos_y=98b",

            "0pos_x=84pos_y=0b",
            "0pos_x=84pos_y=14b",
            "0pos_x=84pos_y=28b",
            "0pos_x=84pos_y=42b",
            "0pos_x=84pos_y=56b",
            "0pos_x=84pos_y=70b",
            "0pos_x=84pos_y=84b",
            "0pos_x=84pos_y=98b",

            "0pos_x=98pos_y=0b",
            "0pos_x=98pos_y=14b",
            "0pos_x=98pos_y=28b",
            "0pos_x=98pos_y=42b",
            "0pos_x=98pos_y=56b",
            "0pos_x=98pos_y=70b",
            "0pos_x=98pos_y=84b",
            "0pos_x=98pos_y=98b",
    };

    String[] rpiIdentifiers81 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=12b",
            "0pos_x=0pos_y=24b",
            "0pos_x=0pos_y=36b",
            "0pos_x=0pos_y=48b",
            "0pos_x=0pos_y=60b",
            "0pos_x=0pos_y=72b",
            "0pos_x=0pos_y=84b",
            "0pos_x=0pos_y=96b",

            "0pos_x=12pos_y=0b",
            "0pos_x=12pos_y=12b",
            "0pos_x=12pos_y=24b",
            "0pos_x=12pos_y=36b",
            "0pos_x=12pos_y=48b",
            "0pos_x=12pos_y=60b",
            "0pos_x=12pos_y=72b",
            "0pos_x=12pos_y=84b",
            "0pos_x=12pos_y=96b",

            "0pos_x=24pos_y=0b",
            "0pos_x=24pos_y=12b",
            "0pos_x=24pos_y=24b",
            "0pos_x=24pos_y=36b",
            "0pos_x=24pos_y=48b",
            "0pos_x=24pos_y=60b",
            "0pos_x=24pos_y=72b",
            "0pos_x=24pos_y=84b",
            "0pos_x=24pos_y=96b",

            "0pos_x=36pos_y=0b",
            "0pos_x=36pos_y=12b",
            "0pos_x=36pos_y=24b",
            "0pos_x=36pos_y=36b",
            "0pos_x=36pos_y=48b",
            "0pos_x=36pos_y=60b",
            "0pos_x=36pos_y=72b",
            "0pos_x=36pos_y=84b",
            "0pos_x=36pos_y=96b",

            "0pos_x=48pos_y=0b",
            "0pos_x=48pos_y=12b",
            "0pos_x=48pos_y=24b",
            "0pos_x=48pos_y=36b",
            "0pos_x=48pos_y=48b",
            "0pos_x=48pos_y=60b",
            "0pos_x=48pos_y=72b",
            "0pos_x=48pos_y=84b",
            "0pos_x=48pos_y=96b",

            "0pos_x=60pos_y=0b",
            "0pos_x=60pos_y=12b",
            "0pos_x=60pos_y=24b",
            "0pos_x=60pos_y=36b",
            "0pos_x=60pos_y=48b",
            "0pos_x=60pos_y=60b",
            "0pos_x=60pos_y=72b",
            "0pos_x=60pos_y=84b",
            "0pos_x=60pos_y=96b",

            "0pos_x=72pos_y=0b",
            "0pos_x=72pos_y=12b",
            "0pos_x=72pos_y=24b",
            "0pos_x=72pos_y=36b",
            "0pos_x=72pos_y=48b",
            "0pos_x=72pos_y=60b",
            "0pos_x=72pos_y=72b",
            "0pos_x=72pos_y=84b",
            "0pos_x=72pos_y=96b",

            "0pos_x=84pos_y=0b",
            "0pos_x=84pos_y=12b",
            "0pos_x=84pos_y=24b",
            "0pos_x=84pos_y=36b",
            "0pos_x=84pos_y=48b",
            "0pos_x=84pos_y=60b",
            "0pos_x=84pos_y=72b",
            "0pos_x=84pos_y=84b",
            "0pos_x=84pos_y=96b",

            "0pos_x=96pos_y=0b",
            "0pos_x=96pos_y=12b",
            "0pos_x=96pos_y=24b",
            "0pos_x=96pos_y=36b",
            "0pos_x=96pos_y=48b",
            "0pos_x=96pos_y=60b",
            "0pos_x=96pos_y=72b",
            "0pos_x=96pos_y=84b",
            "0pos_x=96pos_y=96b",

    };


    String[] rpiIdentifiers100 = {
            "0pos_x=0pos_y=0b",
            "0pos_x=0pos_y=11b",
            "0pos_x=0pos_y=22b",
            "0pos_x=0pos_y=33b",
            "0pos_x=0pos_y=44b",
            "0pos_x=0pos_y=55b",
            "0pos_x=0pos_y=66b",
            "0pos_x=0pos_y=77b",
            "0pos_x=0pos_y=88b",
            "0pos_x=0pos_y=99b",

            "0pos_x=11pos_y=0b",
            "0pos_x=11pos_y=11b",
            "0pos_x=11pos_y=22b",
            "0pos_x=11pos_y=33b",
            "0pos_x=11pos_y=44b",
            "0pos_x=11pos_y=55b",
            "0pos_x=11pos_y=66b",
            "0pos_x=11pos_y=77b",
            "0pos_x=11pos_y=88b",
            "0pos_x=11pos_y=99b",

            "0pos_x=22pos_y=0b",
            "0pos_x=22pos_y=11b",
            "0pos_x=22pos_y=22b",
            "0pos_x=22pos_y=33b",
            "0pos_x=22pos_y=44b",
            "0pos_x=22pos_y=55b",
            "0pos_x=22pos_y=66b",
            "0pos_x=22pos_y=77b",
            "0pos_x=22pos_y=88b",
            "0pos_x=22pos_y=99b",

            "0pos_x=33pos_y=0b",
            "0pos_x=33pos_y=11b",
            "0pos_x=33pos_y=22b",
            "0pos_x=33pos_y=33b",
            "0pos_x=33pos_y=44b",
            "0pos_x=33pos_y=55b",
            "0pos_x=33pos_y=66b",
            "0pos_x=33pos_y=77b",
            "0pos_x=33pos_y=88b",
            "0pos_x=33pos_y=99b",

            "0pos_x=44pos_y=0b",
            "0pos_x=44pos_y=11b",
            "0pos_x=44pos_y=22b",
            "0pos_x=44pos_y=33b",
            "0pos_x=44pos_y=44b",
            "0pos_x=44pos_y=55b",
            "0pos_x=44pos_y=66b",
            "0pos_x=44pos_y=77b",
            "0pos_x=44pos_y=88b",
            "0pos_x=44pos_y=99b",

            "0pos_x=55pos_y=0b",
            "0pos_x=55pos_y=11b",
            "0pos_x=55pos_y=22b",
            "0pos_x=55pos_y=33b",
            "0pos_x=55pos_y=44b",
            "0pos_x=55pos_y=55b",
            "0pos_x=55pos_y=66b",
            "0pos_x=55pos_y=77b",
            "0pos_x=55pos_y=88b",
            "0pos_x=55pos_y=99b",

            "0pos_x=66pos_y=0b",
            "0pos_x=66pos_y=11b",
            "0pos_x=66pos_y=22b",
            "0pos_x=66pos_y=33b",
            "0pos_x=66pos_y=44b",
            "0pos_x=66pos_y=55b",
            "0pos_x=66pos_y=66b",
            "0pos_x=66pos_y=77b",
            "0pos_x=66pos_y=88b",
            "0pos_x=66pos_y=99b",

            "0pos_x=77pos_y=0b",
            "0pos_x=77pos_y=11b",
            "0pos_x=77pos_y=22b",
            "0pos_x=77pos_y=33b",
            "0pos_x=77pos_y=44b",
            "0pos_x=77pos_y=55b",
            "0pos_x=77pos_y=66b",
            "0pos_x=77pos_y=77b",
            "0pos_x=77pos_y=88b",
            "0pos_x=77pos_y=99b",

            "0pos_x=88pos_y=0b",
            "0pos_x=88pos_y=11b",
            "0pos_x=88pos_y=22b",
            "0pos_x=88pos_y=33b",
            "0pos_x=88pos_y=44b",
            "0pos_x=88pos_y=55b",
            "0pos_x=88pos_y=66b",
            "0pos_x=88pos_y=77b",
            "0pos_x=88pos_y=88b",
            "0pos_x=88pos_y=99b",

            "0pos_x=99pos_y=0b",
            "0pos_x=99pos_y=11b",
            "0pos_x=99pos_y=22b",
            "0pos_x=99pos_y=33b",
            "0pos_x=99pos_y=44b",
            "0pos_x=99pos_y=55b",
            "0pos_x=99pos_y=66b",
            "0pos_x=99pos_y=77b",
            "0pos_x=99pos_y=88b",
            "0pos_x=99pos_y=99b",


    };

}
