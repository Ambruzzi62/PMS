package com.microida.pms.controller;

import com.microida.pms.domain.PmsUser;
import com.microida.pms.service.PmsUserService;
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
@RequestMapping("/pmsUsers")
public class PmsUserController {

    private final PmsUserService pmsUserService;

    public PmsUserController(final PmsUserService pmsUserService) {
        this.pmsUserService = pmsUserService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter, final Model model) {
        model.addAttribute("pmsUsers", pmsUserService.findAll(filter));
        model.addAttribute("filter", filter);
        return "pmsUser/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pmsUser") final PmsUser pmsUser) {
        return "pmsUser/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("pmsUser") @Valid final PmsUser pmsUser,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsUser/add";
        }
        pmsUserService.create(pmsUser);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsUser.create.success"));
        return "redirect:/pmsUsers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("pmsUser", pmsUserService.get(id));
        return "pmsUser/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("pmsUser") @Valid final PmsUser pmsUser,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pmsUser/edit";
        }
        pmsUserService.update(pmsUser);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pmsUser.update.success"));
        return "redirect:/pmsUsers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        pmsUserService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pmsUser.delete.success"));
        return "redirect:/pmsUsers";
    }

}
