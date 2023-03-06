package com.microida.pms.repos;

import com.microida.pms.domain.PmsProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PmsProductRepository extends JpaRepository<PmsProduct, Long> {

    Page<PmsProduct> findAllById(Long id, Pageable pageable);

}
