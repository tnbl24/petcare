package org.datn.petcare.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.dto.RatingDTO;
import org.datn.petcare.entity.BookedService;
import org.datn.petcare.entity.Rating;
import org.datn.petcare.entity.Services;
import org.datn.petcare.mapper.BookedServiceMapper;
import org.datn.petcare.mapper.GroupServiceMapper;
import org.datn.petcare.repository.BookedServiceRepository;
import org.datn.petcare.repository.GroupServiceRepository;
import org.datn.petcare.repository.RatingRepository;
import org.datn.petcare.repository.ServicesRepository;
import org.datn.petcare.service.admin.BookedServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    GroupServiceRepository groupServiceRepository;

    @Autowired
    GroupServiceMapper groupServiceMapper;

    @Autowired
    ServicesRepository servicesRepository;

    @Autowired
    RatingRepository ratingRepository;

    @GetMapping("home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("username", authentication.getName());
        }

        model.addAttribute("groupServices", groupServiceRepository.findAll());

        Pageable pageable = PageRequest.of(0, 6);
        List<Services> topServices = servicesRepository.findTop5ByOrderByIdDesc(pageable);
        convertImagesToBase64(topServices);
        model.addAttribute("topServices", topServices);

        List<Services> comboServices = servicesRepository.findAllComboServices();
        convertImagesToBase64(comboServices);
        model.addAttribute("comboServices", comboServices);

        List<RatingDTO> ratingsList = ratingRepository.findAllWithUserInfo();

        model.addAttribute("ratings", ratingsList);
        model.addAttribute("totalRatings", ratingsList.size());


        return "user/index";
    }

    private void convertImagesToBase64(List<Services> services) {
        for (Services service : services) {
            if (service.getImage() != null) {
                String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(service.getImage());
                service.setBase64Image(base64Image);
            }
        }
    }

}
