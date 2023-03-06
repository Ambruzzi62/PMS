package com.microida.pms.service;

import com.microida.pms.domain.PmsCategory;
import com.microida.pms.repos.PmsCategoryRepository;
import com.microida.pms.util.NotFoundException;
import com.microida.pms.util.WebUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class PmsCategoryService {

    private final PmsCategoryRepository pmsCategoryRepository;

    public PmsCategoryService(final PmsCategoryRepository pmsCategoryRepository) {
        this.pmsCategoryRepository = pmsCategoryRepository;
    }

    public List<PmsCategory> findAll(final String filter) {
        List<PmsCategory> pmsCategorys;
        final Sort sort = Sort.by("id");
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            pmsCategorys = pmsCategoryRepository.findAllById(longFilter, sort);
        } else {
            pmsCategorys = pmsCategoryRepository.findAll(sort);
        }
        return pmsCategorys.stream()
                .collect(Collectors.toList());
    }

    public PmsCategory get(final Long id) {
        return pmsCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }

    public Long create(final PmsCategory pmsCategory) {
        return pmsCategoryRepository.save(pmsCategory).getId();
    }

    public void update(final PmsCategory pmsCategory) {
        pmsCategoryRepository.save(pmsCategory);
    }

    public void delete(final Long id) {
        pmsCategoryRepository.deleteById(id);
    }


    @Transactional
    public String getReferencedWarning(final Long id) {
        final PmsCategory pmsCategory = pmsCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        if (!pmsCategory.getCategoryPmsProducts().isEmpty()) {
            return WebUtils.getMessage("pmsCategory.pmsProduct.manyToOne.referenced", pmsCategory.getCategoryPmsProducts().iterator().next().getId());
        }
        return null;
    }

}
