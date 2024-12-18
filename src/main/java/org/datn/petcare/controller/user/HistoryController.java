package org.datn.petcare.controller.user;

import org.datn.petcare.dto.*;
import org.datn.petcare.entity.BookedService;
import org.datn.petcare.entity.GroupService;
import org.datn.petcare.entity.Rating;
import org.datn.petcare.entity.Services;
import org.datn.petcare.exception.ResourceNotFoundException;
import org.datn.petcare.mapper.BookedServiceMapper;
import org.datn.petcare.mapper.GroupServiceMapper;
import org.datn.petcare.mapper.ServicesMapper;
import org.datn.petcare.repository.BookedServiceRepository;
import org.datn.petcare.repository.GroupServiceRepository;
import org.datn.petcare.repository.RatingRepository;
import org.datn.petcare.repository.ServicesRepository;
import org.datn.petcare.service.admin.BookedServiceService;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/history")
public class HistoryController {

    private static final Logger log = LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    private BookedServiceService bookedServiceService;

    @Autowired
    private BookedServiceRepository bookedServiceRepository;

    @Autowired
    private BookedServiceMapper bookedServiceMapper;

    @Autowired
    private FilterSpecification filterSpecification;

    @Autowired
    private GroupServiceMapper groupServiceMapper;

    @Autowired
    GroupServiceRepository groupServiceRepository;

    @Autowired
    ServicesRepository servicesRepository;

    @Autowired
    ServicesMapper servicesMapper;

    @Autowired
    RatingRepository ratingRepository;

    @GetMapping
    public String showHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer bookServiceId,
            @RequestParam(required = false) Integer serviceId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String username = userDetails.getUsername();

        Specification<BookedService> specification = (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.equal(root.get("user").get("username"), username);

            if (bookServiceId != null && bookServiceId > 0) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), bookServiceId));
            }

            if (serviceId != null && serviceId > 0) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("services").get("id"), serviceId));
            }

            return predicate;
        };

        Page<BookedService> bookedServices = bookedServiceRepository.findAll(specification,
                PageRequest.of(pageNum - 1, size, Sort.by(Sort.Direction.DESC, "id")));

        model.addAttribute("bookedServices", bookedServices.getContent());
        model.addAttribute("totalPages", bookedServices.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("services", servicesRepository.findAll());
        model.addAttribute("bookServiceId", bookServiceId);
        model.addAttribute("serviceId", serviceId);

        List<String> ratingStatusList = bookedServices.getContent().stream()
                .map(service -> service.getId() + ": " + (hasRating(service.getId()) ? "Có đánh giá" : "Không có đánh giá"))
                .collect(Collectors.toList());
        log.info(String.valueOf(ratingStatusList));
        model.addAttribute("ratingStatusList", ratingStatusList);

        return "user/history";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBookedService(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        BookedService bookedService = bookedServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booked service ID: " + id));

        if (bookedService.getStatus().equals("confirmed") || bookedService.getStatus().equals("pending")) {
            bookedService.setStatus("cancelled");
            bookedServiceRepository.save(bookedService);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy lịch này!");
        }

        return "redirect:/history";
    }

    @PostMapping("/{id}/rating")
    public String addRating(@PathVariable Integer id, @ModelAttribute RatingDTO ratingDTO, RedirectAttributes redirectAttributes) {
        BookedService bookedService = bookedServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booked service ID: " + id));

        Rating rating = new Rating();
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());
        rating.setCreatedAt(LocalDateTime.now());
        rating.setBookedService(bookedService);

        if (ratingDTO.getImageFile() != null && !ratingDTO.getImageFile().isEmpty()) {
            try {
                rating.setImage(ratingDTO.getImageFile().getBytes());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể tải lên ảnh.");
                return "redirect:/history";
            }
        }

        ratingRepository.save(rating);

        redirectAttributes.addFlashAttribute("successMessage", "Đánh giá đã được gửi thành công!");
        return "redirect:/history";
    }

    private boolean hasRating(Integer bookedServiceId) {
        return ratingRepository.existsByBookedServiceId(bookedServiceId);
    }

    @GetMapping("/{bookedServiceId}/ratings")
    @ResponseBody
    public RatingDTO getRatingByBookedServiceId(@PathVariable Integer bookedServiceId) {
        Rating rating = ratingRepository.findByBookedServiceId(bookedServiceId);

        if (rating == null) {
            throw new ResourceNotFoundException("Không tìm thấy đánh giá cho dịch vụ đã đặt với ID: " + bookedServiceId);
        }

        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());

        if (rating.getImage() != null) {
            dto.setBase64Image(Base64.getEncoder().encodeToString(rating.getImage()));
        }

        return dto;
    }
}