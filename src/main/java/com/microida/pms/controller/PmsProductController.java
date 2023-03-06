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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
@RequestMapping("/pmsProducts")
public class PmsProductController {

    private final PmsProductService pmsProductService;
    private final PmsCategoryRepository pmsCategoryRepository;
    private final PmsDescriptionRepository pmsDescriptionRepository;
    private final PmsImageRepository pmsImageRepository;

    public PmsProductController(final PmsProductService pmsProductService,
                                final PmsCategoryRepository pmsCategoryRepository,
                                final PmsDescriptionRepository pmsDescriptionRepository,
                                final PmsImageRepository pmsImageRepository) {
        this.pmsProductService = pmsProductService;
        this.pmsCategoryRepository = pmsCategoryRepository;
        this.pmsDescriptionRepository = pmsDescriptionRepository;
        this.pmsImageRepository = pmsImageRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("categoryValues", pmsCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .collect(Collectors.toMap(PmsCategory::getId, PmsCategory::getName)));
        model.addAttribute("descriptionValues", pmsDescriptionRepository.findAll(Sort.by("id"))
                .stream()
                .collect(Collectors.toMap(PmsDescription::getId, PmsDescription::getName)));
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
    public String images(@PathVariable final Long id, final Model model) throws IOException {
        List<String> imageNames = null;

        try (Stream<Path> stream = Files.list(Paths.get("src/main/media/" + id ))) {
            imageNames =  stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }

        model.addAttribute("pmsProductId", id);
        model.addAttribute("pmsImages", imageNames);
        return "pmsProduct/images";
    }

    @PostMapping("/images/{productId}/delete/{image}")
    public String deleteImage(@PathVariable final Long productId, @PathVariable final String image, final Model model) throws IOException {
        Files.delete(Paths.get("src/main/media/" + productId + "/" + image ));
        reorderImages(productId, null);
        return "redirect:/pmsProducts/images/" + productId;
    }

    @PostMapping("/images/{productId}/setAsFirst/{image}")
    public String setAsFirstImage(@PathVariable final Long productId, @PathVariable final String image, final Model model) throws IOException {
        reorderImages(productId, image);
        return "redirect:/pmsProducts/images/" + productId;
    }

    @PostMapping("/images/{productId}/addNewImage")
    public String addNewImage(@PathVariable final Long productId, @RequestParam("image") MultipartFile multipartFile, final Model model) throws IOException {
        int number = getAllFilesFromFolder(productId).size();
        String contentType  = multipartFile.getContentType();

        String filename = number + "." + contentType.substring(contentType.indexOf("/") + 1);
        byte[] bytes = multipartFile.getBytes();
        String insPath = "src/main/media/" + productId + "/"  + filename;
        Files.write(Paths.get(insPath), bytes);

        return "redirect:/pmsProducts/images/" + productId;
    }

    private void reorderImages(Long productId, String image) throws IOException {
        List<Path> paths = getAllFilesFromFolder(productId);

        for (int i = 0; i < paths.size(); i++){
            if(paths.get(i).getFileName().toString().equals(image)){
                Files.move(paths.get(i), paths.get(i).resolveSibling("tmp_0.jpeg"));
                continue;
            }
            Files.move(paths.get(i), paths.get(i).resolveSibling("tmp_" + (image == null ? i : i + 1) + ".jpeg"));
        }

        paths = getAllFilesFromFolder(productId);
        for (int i = 0; i < paths.size(); i++){
            Files.move(paths.get(i), paths.get(i).resolveSibling(i + ".jpeg"));
        }
    }

    private List<Path> getAllFilesFromFolder(Long productId) throws IOException {
        List<Path> paths = new ArrayList();
        try (Stream<Path> stream = Files.list(Paths.get("src/main/media/" + productId ))) {
            paths =  stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList());
        }
        return paths;
    }
}
