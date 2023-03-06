package com.microida.pms.service;

import com.microida.pms.domain.PmsUser;
import com.microida.pms.repos.PmsUserRepository;
import com.microida.pms.util.NotFoundException;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PmsUserService {

    private final PmsUserRepository pmsUserRepository;

    public PmsUserService(final PmsUserRepository pmsUserRepository) {
        this.pmsUserRepository = pmsUserRepository;
    }

    public List<PmsUser> findAll(final String filter) {
        List<PmsUser> pmsUsers;
        final Sort sort = Sort.by("id");
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            pmsUsers = pmsUserRepository.findAllById(longFilter, sort);
        } else {
            pmsUsers = pmsUserRepository.findAll(sort);
        }
        return pmsUsers;
    }

    public PmsUser get(final Long id) {
        return pmsUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }

    public Long create(final PmsUser pmsUser) {
        return pmsUserRepository.save(pmsUser).getId();
    }

    public void update(final PmsUser pmsUser) {
        pmsUserRepository.save(pmsUser);
    }

    public void delete(final Long id) {
        pmsUserRepository.deleteById(id);
    }

}
