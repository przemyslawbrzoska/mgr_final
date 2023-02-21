package mgr.backend.service;

import mgr.backend.configuration.ConfigProperties;
import mgr.backend.entity.*;
import mgr.backend.repository.PositionRepository;
import mgr.backend.repository.ZoneRepository;
import mgr.backend.service.interfaces.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@Order(value = 99)
public class PositionServiceImpl implements PositionService {

    Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

    private final ConfigProperties configProperties;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private RpiServiceImpl rpiService;

    public PositionServiceImpl(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public Optional<PositionData> getPositionById(String id) {
        return Optional.ofNullable(positionRepository.findTopByMobileClientIdentifierOrderByDateDesc(id));
    }

    @Override
    public List<PositionData> getPositions() {
        return positionRepository.findDistinctPositions();
    }

    Map<String, PositionData> lastPositon = new HashMap<>();
    Map<String, RpiData> raspberries = new HashMap<>();
    ArrayList<ZoneData> zones = new ArrayList<>();

    private PositionData updateZoneOccurences(PositionData position, PositionData previousPosition) {
        zones = (ArrayList<ZoneData>) zoneRepository.findAll();
        for (ZoneData item : zones) {
            if (item.isPositionInRange(position)) {
                position.addZone(item.getCode());
            }
            if (Objects.nonNull(previousPosition) && !previousPosition.getZones().contains(item.getCode())) {
                item.incrementZoneOccurences();
                zoneRepository.save(item);
            }
        }
        return position;
    }


    @Override
    @Scheduled(fixedRate = 1000)
    public void runPositionCalculation() {
        StopWatch watch = new StopWatch();

        if (configProperties.isRunScheduler()) {
            watch.start();
            zones = (ArrayList<ZoneData>) zoneRepository.findAll();
            if (raspberries.isEmpty()) {
                for (RpiData item : rpiService.fetchRpiList()) {
                    raspberries.put(item.getCode(), item);
                }
            }
            Set<RequestData> requestsFromLastSecond = (Set<RequestData>) requestService.fetchRequestsList();
            watch.stop();
            logger.info("Elapsed time fetch " + watch.getTotalTimeMillis());
            watch.start();
            logger.info("requestsFromLastSecondCount " + requestsFromLastSecond.size());
            Set<String> mobileClients = requestsFromLastSecond.stream().map(x -> x.getMobileClientIdentifier()).collect(Collectors.toSet());
            List<PositionData> positionDataList = new ArrayList<>();
            for (String mobileClient : mobileClients) {
                Set<RequestData> requestsFromLastSecondByClient = requestsFromLastSecond.stream().filter(x -> x.getMobileClientIdentifier().equals(mobileClient)).collect(Collectors.toSet());
                PositionData previousPosition = lastPositon.get(mobileClient);
                Set<String> triggeredRpiCodes = requestsFromLastSecondByClient.stream().map(x -> x.getRpiIdentifier()).collect(Collectors.toSet());
                ArrayList<RpiData> triggeredRpis = (ArrayList<RpiData>) triggeredRpiCodes.stream().map(raspberries::get).collect(Collectors.toList());
                if (triggeredRpis.isEmpty()) {
                    return;
                }
                Set<TileData> resultTiles = null;
                try {
                    resultTiles = new HashSet<>(triggeredRpis.get(0).getTilesCovered());
                } catch (NullPointerException e) {
                    logger.info("Invalid identifer " + triggeredRpiCodes);
                }
                Set<TileData> tempResultTiles = new HashSet<>(resultTiles);
                for (RpiData rpi : triggeredRpis) {

                    resultTiles.retainAll(rpi.getTilesCovered());
                }

                PositionData predictedPosition = null;
                if (!resultTiles.isEmpty()) {
                    predictedPosition = calculatePostionBasedOnRetainedTiles(resultTiles);
                    predictedPosition.setDate(requestsFromLastSecondByClient.iterator().next().getDate());
                    predictedPosition.setMobileClientIdentifier(requestsFromLastSecondByClient.iterator().next().getMobileClientIdentifier());
                    positionDataList.add(predictedPosition);
                    updateZoneOccurences(predictedPosition, lastPositon.get(mobileClient));
                    lastPositon.put(predictedPosition.getMobileClientIdentifier(), predictedPosition);
                } else if (Objects.nonNull(previousPosition)) {
                    predictedPosition = new PositionData();
                    predictedPosition.setPosX(previousPosition.getPosX());
                    predictedPosition.setPosY(previousPosition.getPosY());
                    predictedPosition.setMobileClientIdentifier(mobileClient);
                    predictedPosition.setDate(requestsFromLastSecondByClient.iterator().next().getDate());
                    positionDataList.add(predictedPosition);
                    updateZoneOccurences(predictedPosition, lastPositon.get(mobileClient));
                    lastPositon.put(predictedPosition.getMobileClientIdentifier(), predictedPosition);
                }

            }
            watch.stop();
            positionRepository.saveAll(positionDataList);
            logger.info("time## " + watch.getTotalTimeMillis());
            requestService.truncateRequests(); //improve performance
        }
    }
    private PositionData calculatePostionBasedOnRetainedTiles(Set<TileData> resultTiles) {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (TileData tile : resultTiles) {
            if (tile.getPosX() > maxX) {
                maxX = tile.getPosX();
            }
            if (tile.getPosY() > maxY) {
                maxY = tile.getPosY();
            }
            if (tile.getPosX() < minX) {
                minX = tile.getPosX();
            }
            if (tile.getPosY() < minY) {
                minY = tile.getPosY();
            }
        }
        int posx = (maxX + minX) / 2;
        int posy = (maxY + minY) / 2;

        return new PositionData(posx, posy);

    }


}
