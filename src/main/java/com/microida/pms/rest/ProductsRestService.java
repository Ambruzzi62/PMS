package com.microida.pms.rest;

import java.util.List;

import com.microida.pms.domain.PmsProduct;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductsRestService {

    @GetMapping
    public ResponseEntity<List<PmsProduct>> getAllProduct() {
        return ResponseEntity.ok(null);
    }

}
