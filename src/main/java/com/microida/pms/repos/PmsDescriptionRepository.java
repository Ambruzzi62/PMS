package com.microida.pms.repos;

import com.microida.pms.domain.PmsDescription;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PmsDescriptionRepository extends JpaRepository<PmsDescription, Long> {

    List<PmsDescription> findAllById(Long id, Sort sort);

}
