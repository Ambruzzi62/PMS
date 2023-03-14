package com.microida.pms.controller;

import com.microida.pms.domain.PmsProduct;
import com.microida.pms.model.SimplePage;
import com.microida.pms.service.PmsEbayImporterService;
import com.microida.pms.service.PmsProductService;
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

    private final PmsProductService pmsProductService;

    public PmsEbayImporterController(final PmsEbayImporterService pmsEbayImporterService, final PmsProductService pmsProductService) {
        this.pmsEbayImporterService = pmsEbayImporterService;
        this.pmsProductService = pmsProductService;
    }

    @GetMapping
    public String init() {
        return "pmsEbayImporter/import";
    }

    @PostMapping("/import")
    public String importProduct(@RequestParam String productUrl, final Model model) {
        Long productId = pmsEbayImporterService.importProduct(productUrl);
        model.addAttribute("pmsProduct", pmsProductService.get(productId));
        return "pmsProduct/edit";
    }


}
