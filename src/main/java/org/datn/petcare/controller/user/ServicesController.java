package org.datn.petcare.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.dto.RatingDTO;
import org.datn.petcare.entity.GroupService;
import org.datn.petcare.entity.ServiceDetail;
import org.datn.petcare.entity.Services;
import org.datn.petcare.mapper.GroupServiceMapper;
import org.datn.petcare.mapper.ServiceDetailMapper;
import org.datn.petcare.mapper.ServicesMapper;
import org.datn.petcare.repository.GroupServiceRepository;
import org.datn.petcare.repository.RatingRepository;
import org.datn.petcare.repository.ServiceDetailRepository;
import org.datn.petcare.repository.ServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@Slf4j
public class ServicesController {

    @Autowired
    ServicesMapper servicesMapper;

    @Autowired
    ServicesRepository servicesRepository;

    @Autowired
    GroupServiceMapper groupServiceMapper;

    @Autowired
    GroupServiceRepository groupServiceRepository;

    @Autowired
    ServiceDetailMapper serviceDetailMapper;

    @Autowired
    ServiceDetailRepository serviceDetailRepository;

    @Autowired
    RatingRepository ratingRepository;

    @GetMapping("/services")
    public String getServices(Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) Integer groupServiceId,
                              @RequestParam(required = false) Double priceFrom,
                              @RequestParam(required = false) Double priceTo,
                              @RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "5") Integer size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Services> servicePage = servicesRepository.findByCriteria(search, groupServiceId, priceFrom, priceTo, pageable);

        List<Services> services = servicePage.getContent();

        for (Services service : services) {
            if (service.getImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(service.getImage());
                service.setBase64Image("data:image/png;base64," + base64Image);
            }

            if (service.getHasDetail().equals("1")) {
                double basePrice = service.getPrice();
                List<ServiceDetail> originalPriceDetails = serviceDetailRepository.findByServicesId(service.getId());

                List<ServiceDetail> updatedPriceDetails = new ArrayList<>();

                for (ServiceDetail originalDetail : originalPriceDetails) {
                    ServiceDetail detailCopy = new ServiceDetail();
                    detailCopy.setId(originalDetail.getId());
                    detailCopy.setWeight(originalDetail.getWeight());
                    detailCopy.setPrice(originalDetail.getPrice());

                    double weight = detailCopy.getPrice();
                    double priceServiceDetail = basePrice + (basePrice * weight / 100);
                    detailCopy.setPrice(priceServiceDetail);

                    updatedPriceDetails.add(detailCopy);
                }
                service.setServiceDetails(updatedPriceDetails);
            }
        }

        List<GroupService> groupServices = groupServiceRepository.findAll();

        model.addAttribute("services", services);
        model.addAttribute("groupServices", groupServices);
        model.addAttribute("currentPage", servicePage.getNumber());
        model.addAttribute("totalPages", servicePage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("groupServiceId", groupServiceId);
        model.addAttribute("priceFrom", priceFrom);
        model.addAttribute("priceTo", priceTo);

        List<RatingDTO> ratings = ratingRepository.findAllByGroupServiceId(groupServiceId);
        model.addAttribute("ratings", ratings);

        return "user/services";
    }
}
