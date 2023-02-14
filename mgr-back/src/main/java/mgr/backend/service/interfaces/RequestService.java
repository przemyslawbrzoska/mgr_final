package mgr.backend.service.interfaces;

import mgr.backend.entity.RequestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface RequestService {

    RequestData saveRequestData(RequestData requestData);
    ArrayList<RequestData> saveRequestDataCollection(List<RequestData> requestData);

    Set<RequestData> fetchRequestsList();

    Set<RequestData> fetchRequestsListFromLastSecond();

    RequestData updateRequestData(RequestData requestData, Long requestDataId);

    void truncateRequests();

    //no delete
}
