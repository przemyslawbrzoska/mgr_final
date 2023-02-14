package mgr.backend.service;

import mgr.backend.entity.RequestData;
import mgr.backend.repository.RequestRepository;
import mgr.backend.service.interfaces.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RequestServiceImpl implements RequestService {

    Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);
    @Autowired
    private RequestRepository requestRepository;

    @Override
    public RequestData saveRequestData(RequestData requestData) {
        return requestRepository.save(requestData);
    }

    @Override
    public ArrayList<RequestData> saveRequestDataCollection(List<RequestData> requestData) {
        return (ArrayList<RequestData>) requestRepository.saveAll(requestData);
    }

    @Override
    public Set<RequestData> fetchRequestsList() {
        return new HashSet<>((Collection) requestRepository.findAll());
    }

    @Override
    public Set<RequestData> fetchRequestsListFromLastSecond() {
//        logger.info(String.valueOf(LocalDateTime.now().minus(1, ChronoUnit.SECONDS)));
        return (Set<RequestData>) requestRepository.findAllByDateAfter(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
    }

    @Override
    @Transactional
    public void truncateRequests() {
        requestRepository.truncateRequests();
    }

    @Override
    public RequestData updateRequestData(RequestData requestData, Long requestDataId) {
        RequestData rq = requestRepository.findById(requestDataId).get();
        return requestRepository.save(rq);
    }
}
