package mgr.backend.controller;


import mgr.backend.dto.ZoneDataDTO;
import mgr.backend.entity.ZoneData;
import mgr.backend.service.FileStorageServiceImpl;
import mgr.backend.service.ZoneServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/zone")
@CrossOrigin("*")
public class ZoneController {

    @Autowired
    FileStorageServiceImpl storageService;

    @Autowired
    private ZoneServiceImpl zoneService;

    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ZoneData saveZoneData(@RequestBody ZoneData zoneData) throws IOException {
        zoneData.setZoneOccurences(0);
        ZoneData item = zoneService.saveZoneData(zoneData);
        return item;
    }

    @GetMapping("/all")
    public List<ZoneData> fetchZones() {
        return zoneService.fetchZonesList();
    }

    @GetMapping("/all/plotly")
    public List<ZoneDataDTO> fetchZonesDTO() {
        return zoneService.fetchZonesDTOList();
    }

    @GetMapping("/{id}")
    public ZoneData fetchZone(@PathVariable Long id) {
        return zoneService.fetchZone(id);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteZone(@PathVariable String code) {
        zoneService.deleteZone(code);
    }

    @GetMapping(value = "img/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable Long id) throws IOException {
        return storageService.load("zone_" + id + ".png").getData();
    }
}
