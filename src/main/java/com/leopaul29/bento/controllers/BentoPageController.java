package com.leopaul29.bento.controllers;

import com.leopaul29.bento.services.BentoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BentoPageController {

    private final BentoService bentoService;

    public BentoPageController(BentoService bentoService) {
        this.bentoService = bentoService;
    }

    @GetMapping
    public String getBentos(Model model) {
        Pageable pageable = PageRequest.of(0,20,Sort.Direction.ASC, "id");
        model.addAttribute("bentos", bentoService.getBentoWithFilter(null, pageable));
        return "bento-list"; // templates/bento-list.html
    }
}
