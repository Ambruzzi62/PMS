package com.microida.pms.controller;

import com.microida.pms.domain.PmsCategory;
import com.microida.pms.domain.PmsDescription;
import com.microida.pms.domain.PmsImage;
import com.microida.pms.domain.PmsProduct;
import com.microida.pms.model.SimplePage;
import com.microida.pms.repos.PmsCategoryRepository;
import com.microida.pms.repos.PmsDescriptionRepository;
import com.microida.pms.repos.PmsImageRepository;
import com.microida.pms.service.PmsProductService;
import com.microida.pms.util.WebUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/pmsImages")
public class PmsImagesController {

    private final PmsProductService pmsProductService;
    private final PmsCategoryRepository pmsCategoryRepository;
    private final PmsDescriptionRepository pmsDescriptionRepository;
    private final PmsImageRepository pmsImageRepository;

    public PmsImagesController(final PmsProductService pmsProductService,
                               final PmsCategoryRepository pmsCategoryRepository,
                               final PmsDescriptionRepository pmsDescriptionRepository,
                               final PmsImageRepository pmsImageRepository) {
        this.pmsProductService = pmsProductService;
        this.pmsCategoryRepository = pmsCategoryRepository;
        this.pmsDescriptionRepository = pmsDescriptionRepository;
        this.pmsImageRepository = pmsImageRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<PmsProduct> pmsProducts = pmsProductService.findAll(filter, pageable);
        model.addAttribute("pmsProducts", pmsProducts);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(pmsProducts));
        return "pmsProduct/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pmsProduct") final PmsProduct pmsProduct) {
        return "pmsProduct/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("pmsProduct") @Valid final PmsProduct pmsProduct,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsProduct/add";
        }
        pmsProductService.create(pmsProduct);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsProduct.create.success"));
        return "redirect:/pmsProducts";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("pmsProduct", pmsProductService.get(id));
        return "pmsProduct/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("pmsProduct") @Valid final PmsProduct pmsProduct,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsProduct/edit";
        }
        pmsProductService.update(id, pmsProduct);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsProduct.update.success"));
        return "redirect:/pmsProducts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        pmsProductService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pmsProduct.delete.success"));
        return "redirect:/pmsProducts";
    }

    @GetMapping("/images/{id}")
    public String images(@PathVariable final Long id, final Model model) {
        List<PmsImage> images = pmsImageRepository.findAllByProductId(id);
        model.addAttribute("images", images);
        return "pmsProducts/images";
    }

}
