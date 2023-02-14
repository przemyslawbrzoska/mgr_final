package mgr.backend.service;

import mgr.backend.entity.RpiData;
import mgr.backend.repository.RpiRepository;
import mgr.backend.service.interfaces.RpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

@Service
@Order(value = 98)
public class RpiServiceImpl implements RpiService {

    @Autowired
    private RpiRepository rpiRepository;

    @Override
    public RpiData saveRpiData(RpiData requestData) {
        return rpiRepository.save(requestData);
    }

    @Override
    @Cacheable("rasbperries")
    public ArrayList<RpiData> fetchRpiList() {
        return (ArrayList<RpiData>) rpiRepository.findAll();
    }

    @Override
    public ArrayList<RpiData> fetchRpiListByCode(Set<String> rpiCodes) {
        return (ArrayList<RpiData>) rpiRepository.findByCodeIn(rpiCodes);
    }

    @Override
    public RpiData updateRpiData(RpiData rpiData, Long requestDataId) {
        RpiData rq = rpiRepository.findById(requestDataId).get();
        return rpiRepository.save(rq);
    }
}