package com.microida.pms.service;

import com.microida.pms.domain.PmsParameter;
import com.microida.pms.repos.PmsParameterRepository;
import com.microida.pms.util.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PmsParameterService {

    private final PmsParameterRepository pmsParameterRepository;

    public PmsParameterService(final PmsParameterRepository pmsParameterRepository) {
        this.pmsParameterRepository = pmsParameterRepository;
    }

    public List<PmsParameter> findAll(final String filter) {
        List<PmsParameter> pmsParameters;
        final Sort sort = Sort.by("key");
        if (filter != null) {
            pmsParameters = pmsParameterRepository.findAllByKeyLikeIgnoreCase("%" + filter + "%", sort);
        } else {
            pmsParameters = pmsParameterRepository.findAll(sort);
        }
        return pmsParameters.stream()
                .collect(Collectors.toList());
    }

    public PmsParameter get(final String key) {
        return pmsParameterRepository.findById(key)
                .orElseThrow(() -> new NotFoundException());
    }

    public String create(final PmsParameter pmsParameter) {
        pmsParameter.setKey(pmsParameter.getKey());
        return pmsParameterRepository.save(pmsParameter).getKey();
    }

    public void update(final PmsParameter pmsParameter) {
        pmsParameterRepository.save(pmsParameter);
    }

    public void delete(final String key) {
        pmsParameterRepository.deleteById(key);
    }

    public boolean keyExists(final String key) {
        return pmsParameterRepository.existsByKeyIgnoreCase(key);
    }

}
