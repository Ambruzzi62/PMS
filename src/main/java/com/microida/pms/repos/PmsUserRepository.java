package com.microida.pms.repos;

import com.microida.pms.domain.PmsUser;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PmsUserRepository extends JpaRepository<PmsUser, Long> {

    List<PmsUser> findAllById(Long id, Sort sort);

}
