package mgr.backend.controller;


import mgr.backend.dto.ResponseMessage;
import mgr.backend.entity.FileData;
import mgr.backend.service.FileStorageServiceImpl;
import mgr.backend.service.TileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/configuration")
@CrossOrigin("*")
public class ConfigurationController {

    Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    FileStorageServiceImpl storageService;

    @Autowired
    TileServiceImpl tileService;

    @PostMapping("/upload/map")
    public ResponseEntity<ResponseMessage> uploadMap(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                logger.info("file " + file.getOriginalFilename() + "import begin");
                tileService.importTileMap(file.getInputStream());
            } else {
                storageService.saveMap(file, file.getName(), "MAP");
            }
            message = "Uploaded the map successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the map: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping(value = "/map",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage() throws IOException {
        FileData file = storageService.loadMap();
        return file.getData();
    }
}
