package org.datn.petcare.controller.admin;

import org.datn.petcare.dto.BookedServiceDTO;
import org.datn.petcare.dto.GroupServiceDTO; // Ensure this import is present
import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.entity.Services;
import org.datn.petcare.service.admin.BookedServiceService;
import org.datn.petcare.service.admin.GroupServiceService;
import org.datn.petcare.service.admin.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private BookedServiceService bookedServiceService;

    @Autowired
    private GroupServiceService groupServiceService;

    @Autowired
    private ServiceService serviceService;


    @GetMapping("/getGroupServiceCounts")
    public ResponseEntity<List<GroupServiceDTO>> getGroupServiceCounts() {
        List<BookedServiceDTO> bookedServices = bookedServiceService.getAll();

        Map<Integer, Integer> bookedCountByGroupServiceId = new HashMap<>();

        for (BookedServiceDTO bookedService : bookedServices) {
            int serviceId = bookedService.getServiceId();

            ServiceDTO service = serviceService.getById(serviceId);

            if (service != null) {
                int groupServiceId = service.getGroupServiceId();
                bookedCountByGroupServiceId.merge(groupServiceId, 1, Integer::sum);
            }
        }

        List<GroupServiceDTO> groupServices = groupServiceService.getAll();

        for (GroupServiceDTO groupService : groupServices) {
            int groupServiceId = groupService.getId();
            int bookedCount = bookedCountByGroupServiceId.getOrDefault(groupServiceId, 0);

            groupService.setBookedCount(bookedCount); // Giả sử bạn đã thêm getter và setter cho bookedCount
        }

        return ResponseEntity.ok(groupServices);
    }

    @GetMapping("/getBookedServiceCountByStatus")
    public ResponseEntity<Map<String, Object>> getBookedServiceCountByStatus() {
        List<BookedServiceDTO> bookedServices = bookedServiceService.getAll();
        Map<String, Object> statusCount = new LinkedHashMap<>();

        // Khởi tạo biến cho confirmed và pending
        int confirmedCount = 0;
        int pendingCount = 0;

        // Khởi tạo bản đồ cho completed và cancelled
        Map<Integer, Integer> completedCountByMonth = new LinkedHashMap<>();
        Map<Integer, Integer> cancelledCountByMonth = new LinkedHashMap<>();

        int currentYear = LocalDate.now().getYear();

        // Khởi tạo các tháng với giá trị 0
        for (int month = 1; month <= 12; month++) {
            completedCountByMonth.put(month, 0);
            cancelledCountByMonth.put(month, 0);
        }

        for (BookedServiceDTO bookedService : bookedServices) {
            String status = bookedService.getStatus();
            LocalDate bookingDate = bookedService.getBookingDate(); // Giả sử đây là LocalDate

            // Đếm confirmed và pending
            if ("confirmed".equals(status)) {
                confirmedCount++;
            } else if ("pending".equals(status)) {
                pendingCount++;
            }

            // Đếm số lượng cho completed và cancelled theo tháng
            if (bookingDate.getYear() == currentYear) {
                int month = bookingDate.getMonthValue(); // Lấy tháng (1-12)

                if ("completed".equals(status)) {
                    completedCountByMonth.merge(month, 1, Integer::sum);
                } else if ("cancelled".equals(status)) {
                    cancelledCountByMonth.merge(month, 1, Integer::sum);
                }
            }
        }

        // Thêm số lượng vào statusCount
        statusCount.put("confirmed", confirmedCount);
        statusCount.put("pending", pendingCount);
        statusCount.put("completed", completedCountByMonth);
        statusCount.put("cancelled", cancelledCountByMonth);

        return ResponseEntity.ok(statusCount);
    }

    @GetMapping("/getTotalPriceByYearAndMonth/{year}")
    public ResponseEntity<Map<String, Object>> getTotalPriceByYearAndMonth(@PathVariable int year) {
        List<BookedServiceDTO> bookedServices = bookedServiceService.getAll();
        Map<String, Object> priceData = new LinkedHashMap<>();

        double totalPrice = 0.0;
        Map<Integer, Double> priceByMonth = new LinkedHashMap<>();

        for (int month = 1; month <= 12; month++) {
            priceByMonth.put(month, 0.0);
        }

        for (BookedServiceDTO bookedService : bookedServices) {
            LocalDate bookingDate = bookedService.getBookingDate();

            if ("completed".equals(bookedService.getStatus()) && bookingDate.getYear() == year) {
                totalPrice += bookedService.getPrice();

                int month = bookingDate.getMonthValue();
                priceByMonth.merge(month, bookedService.getPrice(), Double::sum);
            }
        }

        priceData.put("totalPrice", totalPrice);
        priceData.put("priceByMonth", priceByMonth);

        return ResponseEntity.ok(priceData);
    }
}