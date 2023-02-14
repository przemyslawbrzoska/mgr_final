package mgr.backend.controller;


import mgr.backend.dto.PerformanceDTO;
import mgr.backend.dto.RequestDTO;
import mgr.backend.entity.RequestData;
import mgr.backend.service.PositionServiceImpl;
import mgr.backend.service.RequestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestReaderController {

    Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

    @Autowired
    private RequestServiceImpl requestService;

    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ArrayList<RequestData> saveRequestData(@RequestBody List<RequestDTO> requestDTO) {
        List<RequestData> requestDataList = new ArrayList<>();
        for (RequestDTO item : requestDTO) {
            requestDataList.add(new RequestData(item));
        }
        return requestService.saveRequestDataCollection(requestDataList);
    }

    @PostMapping(value = "/saveperf", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ArrayList<RequestData> saveRequestDataPerformanceTest(@RequestBody PerformanceDTO performanceDTO) {

        List<RequestData> requestDataList = new ArrayList<>();
        for (RequestDTO item : performanceDTO.getRequestDTOList()) {
            requestDataList.add(new RequestData(item, performanceDTO.getTimestamp()));
        }
        return requestService.saveRequestDataCollection(requestDataList);
    }


    @GetMapping("/all")
    public List<RequestData> fetchRequests() {
        return (List<RequestData>) requestService.fetchRequestsList();
    }


    @GetMapping("/hello")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
