package com.microida.pms.rest;

import com.microida.pms.domain.PmsProduct;
import com.microida.pms.model.SimplePage;
import com.microida.pms.service.PmsProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/pmsProducts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PmsProductResource {

    private final PmsProductService pmsProductService;

    public PmsProductResource(final PmsProductService pmsProductService) {
        this.pmsProductService = pmsProductService;
    }

    @GetMapping
    public ResponseEntity<SimplePage<PmsProduct>> getAllPmsProducts(
            @RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(pmsProductService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PmsProduct> getPmsProduct(@PathVariable final Long id) {
        return ResponseEntity.ok(pmsProductService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createPmsProduct(
            @RequestBody @Valid final PmsProduct pmsProduct) {
        return new ResponseEntity<>(pmsProductService.create(pmsProduct), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePmsProduct(@PathVariable final Long id,
            @RequestBody @Valid final PmsProduct pmsProduct) {
        pmsProductService.update(id, pmsProduct);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePmsProduct(@PathVariable final Long id) {
        pmsProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
