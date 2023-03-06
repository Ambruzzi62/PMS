package com.microida.pms.repos;

import com.microida.pms.domain.PmsCategory;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PmsCategoryRepository extends JpaRepository<PmsCategory, Long> {

    List<PmsCategory> findAllById(Long id, Sort sort);

}
