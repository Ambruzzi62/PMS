package com.microida.pms.controller;

import com.microida.pms.domain.PmsDescription;
import com.microida.pms.service.PmsDescriptionService;
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
@RequestMapping("/pmsDescriptions")
public class PmsDescriptionController {

    private final PmsDescriptionService pmsDescriptionService;

    public PmsDescriptionController(final PmsDescriptionService pmsDescriptionService) {
        this.pmsDescriptionService = pmsDescriptionService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter, final Model model) {
        model.addAttribute("pmsDescriptions", pmsDescriptionService.findAll(filter));
        model.addAttribute("filter", filter);
        return "pmsDescription/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pmsDescription") final PmsDescription pmsDescription) {
        return "pmsDescription/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("pmsDescription") @Valid final PmsDescription pmsDescription,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsDescription/add";
        }
        pmsDescriptionService.create(pmsDescription);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsDescription.create.success"));
        return "redirect:/pmsDescriptions";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("pmsDescription", pmsDescriptionService.get(id));
        return "pmsDescription/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("pmsDescription") @Valid final PmsDescription pmsDescription,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsDescription/edit";
        }
        pmsDescriptionService.update(pmsDescription);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsDescription.update.success"));
        return "redirect:/pmsDescriptions";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        final String referencedWarning = pmsDescriptionService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            pmsDescriptionService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pmsDescription.delete.success"));
        }
        return "redirect:/pmsDescriptions";
    }

}
