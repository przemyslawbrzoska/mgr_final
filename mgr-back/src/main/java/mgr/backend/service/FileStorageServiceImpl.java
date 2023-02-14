package mgr.backend.service;

import mgr.backend.entity.FileData;
import mgr.backend.repository.FileRepository;
import mgr.backend.service.interfaces.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Transactional
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    FileRepository fileRepository;

    @Override
    public void saveMap(MultipartFile file, String fileName, String fileType) throws IOException {
        FileData incomingFile = new FileData();
        incomingFile.setData(file.getInputStream().readAllBytes());
        incomingFile.setType(fileType);
        incomingFile.setName(fileName);
        deletePreviousMap(fileType);
        fileRepository.save(incomingFile);
    }

    @Override
    public FileData load(String filename) {
        return fileRepository.findByName(filename);
    }

    @Transactional
    @Override
    public void deletePreviousMap(String fileType){
        fileRepository.deleteAllByType(fileType);
    }
    @Override
    public FileData loadMap() {
        return fileRepository.findByType("MAP");
    }
}
