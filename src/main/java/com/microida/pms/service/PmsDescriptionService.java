package com.microida.pms.service;

import com.microida.pms.domain.PmsDescription;
import com.microida.pms.repos.PmsDescriptionRepository;
import com.microida.pms.util.NotFoundException;
import com.microida.pms.util.WebUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PmsDescriptionService {

    private final PmsDescriptionRepository pmsDescriptionRepository;

    public PmsDescriptionService(final PmsDescriptionRepository pmsDescriptionRepository) {
        this.pmsDescriptionRepository = pmsDescriptionRepository;
    }

    public List<PmsDescription> findAll(final String filter) {
        List<PmsDescription> pmsDescriptions;
        final Sort sort = Sort.by("id");
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            pmsDescriptions = pmsDescriptionRepository.findAllById(longFilter, sort);
        } else {
            pmsDescriptions = pmsDescriptionRepository.findAll(sort);
        }
        return pmsDescriptions.stream()
                .collect(Collectors.toList());
    }

    public PmsDescription get(final Long id) {
        return pmsDescriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }

    public Long create(final PmsDescription pmsDescription) {
        return pmsDescriptionRepository.save(pmsDescription).getId();
    }

    public void update(final PmsDescription pmsDescription) {
        pmsDescriptionRepository.save(pmsDescription);
    }

    public void delete(final Long id) {
        pmsDescriptionRepository.deleteById(id);
    }

    @Transactional
    public String getReferencedWarning(final Long id) {
        final PmsDescription pmsDescription = pmsDescriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        if (!pmsDescription.getDescriptionPmsProducts().isEmpty()) {
            return WebUtils.getMessage("pmsDescription.pmsProduct.manyToOne.referenced", pmsDescription.getDescriptionPmsProducts().iterator().next().getId());
        }
        return null;
    }

}
