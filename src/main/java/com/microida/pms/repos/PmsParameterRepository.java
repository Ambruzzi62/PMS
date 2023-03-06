package com.microida.pms.repos;

import com.microida.pms.domain.PmsParameter;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PmsParameterRepository extends JpaRepository<PmsParameter, String> {

    List<PmsParameter> findAllByKeyLikeIgnoreCase(String key, Sort sort);

    boolean existsByKeyIgnoreCase(String key);

}
