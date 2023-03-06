package com.microida.pms.repos;

import com.microida.pms.domain.PmsCategory;
import com.microida.pms.domain.PmsImage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PmsImageRepository extends JpaRepository<PmsImage, Long> {

    List<PmsImage> findAllById(Long id, Sort sort);

    List<PmsImage> findAllByProductId(Long id);

}
