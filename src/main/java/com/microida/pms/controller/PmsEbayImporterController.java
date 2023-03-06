package com.microida.pms.controller;

import com.microida.pms.domain.PmsProduct;
import com.microida.pms.model.SimplePage;
import com.microida.pms.service.PmsEbayImporterService;
import com.microida.pms.util.WebUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pmsEbayImporter")
public class PmsEbayImporterController {

    private final PmsEbayImporterService pmsEbayImporterService;

    public PmsEbayImporterController(PmsEbayImporterService pmsEbayImporterService) {
        this.pmsEbayImporterService = pmsEbayImporterService;
    }

    @GetMapping
    public String init() {
        return "pmsEbayImporter/import";
    }

    @PostMapping("/import")
    public String importProduct(@RequestParam String productUrl) {
        pmsEbayImporterService.importProduct();
        return "/pmsEbayImporter";
    }


}
