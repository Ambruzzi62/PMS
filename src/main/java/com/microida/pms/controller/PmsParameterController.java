package com.microida.pms.controller;

import com.microida.pms.domain.PmsParameter;
import com.microida.pms.service.PmsParameterService;
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
@RequestMapping("/pmsParameters")
public class PmsParameterController {

    private final PmsParameterService pmsParameterService;

    public PmsParameterController(final PmsParameterService pmsParameterService) {
        this.pmsParameterService = pmsParameterService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter, final Model model) {
        model.addAttribute("pmsParameters", pmsParameterService.findAll(filter));
        model.addAttribute("filter", filter);
        return "pmsParameter/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pmsParameter") final PmsParameter pmsParameter) {
        return "pmsParameter/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("pmsParameter") @Valid final PmsParameter pmsParameter,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("key") && pmsParameter.getKey() == null) {
            bindingResult.rejectValue("key", "NotNull");
        }
        if (!bindingResult.hasFieldErrors("key") && pmsParameterService.keyExists(pmsParameter.getKey())) {
            bindingResult.rejectValue("key", "Exists.pmsParameter.key");
        }
        if (bindingResult.hasErrors()) {
            return "pmsParameter/add";
        }
        pmsParameterService.create(pmsParameter);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsParameter.create.success"));
        return "redirect:/pmsParameters";
    }

    @GetMapping("/edit/{key}")
    public String edit(@PathVariable final String key, final Model model) {
        model.addAttribute("pmsParameter", pmsParameterService.get(key));
        return "pmsParameter/edit";
    }

    @PostMapping("/edit/{key}")
    public String edit(@PathVariable final String key,
            @ModelAttribute("pmsParameter") @Valid final PmsParameter pmsParameter,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsParameter/edit";
        }
        pmsParameterService.update(pmsParameter);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsParameter.update.success"));
        return "redirect:/pmsParameters";
    }

    @PostMapping("/delete/{key}")
    public String delete(@PathVariable final String key,
            final RedirectAttributes redirectAttributes) {
        pmsParameterService.delete(key);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pmsParameter.delete.success"));
        return "redirect:/pmsParameters";
    }

}
