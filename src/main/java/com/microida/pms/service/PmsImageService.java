package com.microida.pms.service;

import com.microida.pms.domain.PmsImage;
import com.microida.pms.repos.PmsImageRepository;
import com.microida.pms.util.NotFoundException;
import org.springframework.stereotype.Service;


@Service
public class PmsImageService {

    private final PmsImageRepository pmsImageRepository;

    public PmsImageService(final PmsImageRepository pmsImageRepository) {
        this.pmsImageRepository = pmsImageRepository;
    }


    public PmsImage get(final Long id) {
        return pmsImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }

    public Long create(final PmsImage pmsImage) {
        return pmsImageRepository.save(pmsImage).getId();
    }

    public void update(final PmsImage pmsImage) {
        pmsImageRepository.save(pmsImage);
    }

    public void delete(final Long id) {
        pmsImageRepository.deleteById(id);
    }


//    @Transactional
//    public String getReferencedWarning(final Long id) {
//        final PmsImage pmsImage = pmsImageRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException());
//        if (!pmsImage.getImagePmsProducts().isEmpty()) {
//            return WebUtils.getMessage("pmsImage.pmsProduct.manyToOne.referenced", pmsImage.getImagePmsProducts().iterator().next().getId());
//        }
//        return null;
//    }

}
