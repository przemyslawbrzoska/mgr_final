package mgr.backend.service.interfaces;

import mgr.backend.entity.FileData;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    void saveMap(MultipartFile file, String fileName, String fileType) throws IOException;

    FileData load(String filename);

    FileData loadMap();

    void deletePreviousMap(String fileType);
}
