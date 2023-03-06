package com.microida.pms.service;

import com.microida.pms.domain.PmsProduct;
import com.microida.pms.model.SimplePage;
import com.microida.pms.repos.PmsCategoryRepository;
import com.microida.pms.repos.PmsDescriptionRepository;
import com.microida.pms.repos.PmsImageRepository;
import com.microida.pms.repos.PmsProductRepository;
import com.microida.pms.util.NotFoundException;

import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PmsProductService {

    private final PmsProductRepository pmsProductRepository;
    private final PmsCategoryRepository pmsCategoryRepository;
    private final PmsDescriptionRepository pmsDescriptionRepository;
    private final PmsImageRepository pmsImageRepository;
    private final PmsImageService pmsImageService;

    public PmsProductService(final PmsProductRepository pmsProductRepository,
                             final PmsCategoryRepository pmsCategoryRepository,
                             final PmsDescriptionRepository pmsDescriptionRepository,
                             final PmsImageRepository pmsImageRepository,
                             final PmsImageService pmsImageService) {
        this.pmsProductRepository = pmsProductRepository;
        this.pmsCategoryRepository = pmsCategoryRepository;
        this.pmsDescriptionRepository = pmsDescriptionRepository;
        this.pmsImageRepository = pmsImageRepository;
        this.pmsImageService = pmsImageService;
    }

    public SimplePage<PmsProduct> findAll(final String filter, final Pageable pageable) {
        Page<PmsProduct> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = pmsProductRepository.findAllById(longFilter, pageable);
        } else {
            page = pmsProductRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .collect(Collectors.toList()),
                page.getTotalElements(), pageable);
    }

    public PmsProduct get(final Long id) {
        return pmsProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }

    public Long create(final PmsProduct pmsProduct) {
        return pmsProductRepository.save(pmsProduct).getId();
    }

    public void update(final Long id, final PmsProduct pmsProduct) {
        pmsProductRepository.save(pmsProduct);
    }

    public void delete(final Long id) {
        pmsProductRepository.deleteById(id);
    }

}
