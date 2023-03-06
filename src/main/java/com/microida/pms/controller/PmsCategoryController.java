package com.microida.pms.controller;

import com.microida.pms.domain.PmsCategory;
import com.microida.pms.service.PmsCategoryService;
import com.microida.pms.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
@RequestMapping("/pmsCategorys")
public class PmsCategoryController {

    private final PmsCategoryService pmsCategoryService;

    public PmsCategoryController(final PmsCategoryService pmsCategoryService) {
        this.pmsCategoryService = pmsCategoryService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter, final Model model) {
        model.addAttribute("pmsCategorys", pmsCategoryService.findAll(filter));
        model.addAttribute("filter", filter);
        return "pmsCategory/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pmsCategory") final PmsCategory pmsCategory) {
        return "pmsCategory/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("pmsCategory") @Valid final PmsCategory pmsCategory,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsCategory/add";
        }
        pmsCategoryService.create(pmsCategory);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsCategory.create.success"));
        return "redirect:/pmsCategorys";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("pmsCategory", pmsCategoryService.get(id));
        return "pmsCategory/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("pmsCategory") @Valid final PmsCategory pmsCategory,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsCategory/edit";
        }
        pmsCategoryService.update(pmsCategory);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsCategory.update.success"));
        return "redirect:/pmsCategorys";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = pmsCategoryService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            pmsCategoryService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pmsCategory.delete.success"));
        }
        return "redirect:/pmsCategorys";
    }

}
