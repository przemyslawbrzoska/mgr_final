package mgr.backend.service;

import mgr.backend.dto.ZoneDataDTO;
import mgr.backend.entity.FileData;
import mgr.backend.entity.ZoneData;
import mgr.backend.repository.FileRepository;
import mgr.backend.repository.ZoneRepository;
import mgr.backend.service.interfaces.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ZoneServiceImpl implements ZoneService {

    @Autowired
    FileStorageServiceImpl storageService;
    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    FileRepository fileRepository;

    @Override
    public ZoneData saveZoneData(ZoneData zoneData) throws IOException {
        BufferedImage bufferedImage = null;
        if (zoneData.getZoneType().equals(ZoneData.type.CIRCLE.label)) {
            zoneData.setZoneHeight(zoneData.getZoneWidth());
        }
        zoneRepository.save(zoneData);
        if (zoneData.getZoneType().equals("rect")) {
            bufferedImage = new BufferedImage(zoneData.getZoneWidth(),
                    zoneData.getZoneHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(randomColor());
            g2d.fillRect(0, 0, zoneData.getZoneWidth(), zoneData.getZoneHeight());
            g2d.dispose();
        }
        if (zoneData.getZoneType().equals("circ")) {

            bufferedImage = new BufferedImage(zoneData.getZoneWidth() * 4,
                    zoneData.getZoneWidth() * 4, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(randomColor());
            g2d.setBackground(new Color(0, 0, 0, 1));
            g2d.fillOval(0, 0, zoneData.getZoneWidth() * 4, zoneData.getZoneWidth() * 4);
            g2d.dispose();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        fileRepository.save(new FileData(baos.toByteArray(), "zone_" + zoneData.getId() + ".png", "ZONE"));
        return zoneData;
    }

    @Override
    public List<ZoneData> fetchZonesList() {
        return (List<ZoneData>) zoneRepository.findAll();
    }

    @Override
    public List<ZoneDataDTO> fetchZonesDTOList() {
        List<ZoneDataDTO> zones = new ArrayList<>();
        ZoneDataDTO map = new ZoneDataDTO("http://localhost:8080/configuration/map", 0, 100, 100, 100);
        zones.add(map);

        for (ZoneData item : fetchZonesList()) {
            ZoneDataDTO zoneDTO = new ZoneDataDTO(item);
            zones.add(zoneDTO);
        }
        return zones;
    }


    @Override
    @Transactional
    public void deleteZone(String code) {
        zoneRepository.deleteByCode(code);
    }

    @Override
    public ZoneData fetchZone(Long id) {
        ZoneData zoneData = zoneRepository.findById(id).get();
        return zoneData;
    }

    private Color randomColor() {
        Random rand = new Random();
        return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
}
