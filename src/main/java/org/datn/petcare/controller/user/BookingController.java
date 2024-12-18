package org.datn.petcare.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.entity.*;
import org.datn.petcare.mapper.*;
import org.datn.petcare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class BookingController {

    @Autowired
    private BookedServiceMapper bookedServiceMapper;

    @Autowired
    private BookedServiceRepository bookedServiceRepository;

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private GroupServiceRepository groupServiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ServiceDetailRepository serviceDetailRepository;

    @GetMapping("/booking")
    public String getBooking(@RequestParam(required = false) Integer groupServiceId, Model model) {
        List<Services> services = (groupServiceId != null) ?
                servicesRepository.findByGroupServiceId(groupServiceId) :
                servicesRepository.findAll();

        for (Services service : services) {
            if ("1".equals(service.getHasDetail())) {
                double basePrice = service.getPrice();
                List<ServiceDetail> originalPriceDetails = serviceDetailRepository.findByServicesId(service.getId());
                List<ServiceDetail> updatedPriceDetails = new ArrayList<>();

                for (ServiceDetail originalDetail : originalPriceDetails) {
                    ServiceDetail detailCopy = new ServiceDetail();
                    detailCopy.setId(originalDetail.getId());
                    detailCopy.setWeight(originalDetail.getWeight());

                    double priceWeight = originalDetail.getPrice();
                    double priceServiceDetail = basePrice + (basePrice * priceWeight / 100);
                    detailCopy.setPrice(priceServiceDetail);

                    updatedPriceDetails.add(detailCopy);
                }

                service.setServiceDetails(updatedPriceDetails);
            }
        }

        List<GroupService> groupServices = groupServiceRepository.findAll();
        model.addAttribute("services", services);
        model.addAttribute("groupServices", groupServices);
        return "user/booking"; // Trả về view
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getPaymentURL(Map<String, String> paramsMap, boolean encodeKey) {
        return paramsMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String key = encodeKey ? URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) : entry.getKey();
                    String value = encodeKey ? URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8) : entry.getValue();

                    return key + "=" + value;
                })
                .collect(Collectors.joining("&"));
    }

    @PostMapping("/confirm-booking")
    public String confirmBooking(@RequestParam String fullName,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String email,
                                 @RequestParam String petName,
                                 @RequestParam int petAge,
                                 @RequestParam String petType,
                                 @RequestParam double petWeight,
                                 @RequestParam String paymentMethod,
                                 @RequestParam String notes,
                                 @RequestParam double price,
                                 @RequestParam int serviceId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime bookingTime,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        try {
            User user = userRepository.findByPhone(phoneNumber);
            if (user == null) {
                user = new User();
                user.setFullName(fullName);
                user.setPhone(phoneNumber);
                user.setEmail(email);
                user.setDeleted(false);
                user.setRegistered(false);
                userRepository.save(user);
            } else {
                user.setEmail(email);
                userRepository.save(user);
            }

            Pet selectedPet = null;
            if (user.getPets() == null) {
                user.setPets(new ArrayList<>());
            }
            for (Pet pet : user.getPets()) {
                if (pet.getName().equalsIgnoreCase(petName)) {
                    selectedPet = pet;
                    break;
                }
            }

            if (selectedPet == null) {
                selectedPet = new Pet();
                selectedPet.setName(petName);
                selectedPet.setAge(petAge);
                selectedPet.setType(petType);
                selectedPet.setWeight(petWeight);
                selectedPet.setUser(user);
                user.addPet(selectedPet);
                petRepository.save(selectedPet);
            } else {
                selectedPet.setAge(petAge);
                selectedPet.setType(petType);
                selectedPet.setWeight(petWeight);
            }

            BookedService bookedService = new BookedService();
            bookedService.setCreateAt(LocalDateTime.now());
            bookedService.setBookingDate(bookingDate);
            bookedService.setBookingTime(bookingTime);
            bookedService.setNote(notes);
            bookedService.setPaymentMethod(paymentMethod);
            bookedService.setPrice(price);
            bookedService.setUser(user);
            bookedService.setServices(servicesRepository.findById(serviceId).orElse(null));
            bookedService.setPet(selectedPet);

            bookedServiceRepository.save(bookedService);

            if ("online".equals(paymentMethod)) {
                String orderType = String.valueOf(bookedService.getServices().getId());
                if (!processOnlinePayment(price, request, bookedService.getId(), response, orderType)) {
                    return "redirect:/errorpage";
                }
                return null;
            } else {
                bookedService.setStatus("pending");
                bookedServiceRepository.save(bookedService);

                redirectAttributes.addFlashAttribute("bookedServiceId", bookedService.getId());
                redirectAttributes.addFlashAttribute("serviceName", bookedService.getServices().getName());
                redirectAttributes.addFlashAttribute("createAt", bookedService.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                redirectAttributes.addFlashAttribute("bookingTime", bookedService.getBookingTime().toString());
                redirectAttributes.addFlashAttribute("bookingDate", bookedService.getBookingDate().toString());
                double price2 = bookedService.getPrice(); // Giả sử bạn đã có giá trị này
                DecimalFormat df = new DecimalFormat("#,###"); // Định dạng với dấu phẩy
                String formattedPrice = df.format(price2).replace(",", ".") + " VND";;
                redirectAttributes.addFlashAttribute("servicePrice", formattedPrice);
                redirectAttributes.addFlashAttribute("paymentMethod", bookedService.getPaymentMethod());
                redirectAttributes.addFlashAttribute("notes", bookedService.getNote());
                redirectAttributes.addFlashAttribute("customerName", bookedService.getUser().getFullName());
                redirectAttributes.addFlashAttribute("customerPhone", bookedService.getUser().getPhone());
                redirectAttributes.addFlashAttribute("customerEmail", bookedService.getUser().getEmail());
                log.info("email khách hàng:", bookedService.getUser().getEmail());
                redirectAttributes.addFlashAttribute("petName", bookedService.getPet().getName());
                redirectAttributes.addFlashAttribute("petAge", bookedService.getPet().getAge());
                redirectAttributes.addFlashAttribute("petBreed", bookedService.getPet().getType());
                redirectAttributes.addFlashAttribute("petWeight", bookedService.getPet().getWeight());
                return "redirect:/booking-success";
            }
        } catch (Exception e) {
            log.error("Có lỗi xảy ra trong quá trình xác nhận đặt dịch vụ: ", e);
            return "redirect:/errorpage";
        }
    }

    private boolean processOnlinePayment(double amount, HttpServletRequest request, Integer orderId, HttpServletResponse response, String orderType) {
        try {
            String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

            Map<String, String> params = new TreeMap<>();
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", "");
            params.put("vnp_Amount", String.valueOf((long) amount * 100L)); // Đảm bảo amount >= 0
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", String.valueOf(orderId));
            params.put("vnp_OrderInfo", URLEncoder.encode("Thanh toan cho don hang " + orderId, StandardCharsets.UTF_8.toString()));
            params.put("vnp_OrderType", orderType);
            params.put("vnp_ReturnUrl", "http://localhost:8080/payment-callback"); // Không mã hóa
            params.put("vnp_IpAddr", "127.0.0.1");
            params.put("vnp_Locale", "vn");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String createDate = sdf.format(new Date());
            params.put("vnp_CreateDate", createDate);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);
            String expireDate = sdf.format(calendar.getTime());
            params.put("vnp_ExpireDate", expireDate);

            log.info("Sending parameters to VNPay: {}", params);

            String hashData = getPaymentURL(params, true); // Đảm bảo mã hóa keys
            log.info("HashData: {}", hashData);

            String vnp_SecureHash = hmacSHA512("",hashData);

            log.info("Calculated Secure Hash: {}", vnp_SecureHash);

            StringBuilder requestUrl = new StringBuilder(vnp_Url);
            requestUrl.append("?").append(hashData).append("&vnp_SecureHash=").append(vnp_SecureHash);

            log.info("Request URL: {}", requestUrl);

            response.sendRedirect(requestUrl.toString());

            return true;
        } catch (Exception e) {
            log.error("Có lỗi xảy ra trong quá trình thanh toán: ", e);
            return false;
        }
    }

    @GetMapping("/payment-callback")
    public String paymentCallback(@RequestParam Map<String, String> params, Model model) {
        log.info("Received parameters from VNPay: {}", params);

        String responseCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {
            BookedService bookedService = bookedServiceRepository.findById(Integer.parseInt(orderId)).orElse(null);
            if (bookedService != null) {
                bookedService.setStatus("pending");
                bookedServiceRepository.save(bookedService);

                model.addAttribute("bookedServiceId", bookedService.getId());
                model.addAttribute("serviceName", bookedService.getServices().getName());
                model.addAttribute("bookingTime", bookedService.getBookingTime().toString());
                model.addAttribute("bookingDate", bookedService.getBookingDate().toString());

                LocalDateTime createAt = bookedService.getCreateAt();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedDateTime = createAt.format(formatter);
                model.addAttribute("createAt", formattedDateTime);

                double price2 = bookedService.getPrice(); // Giả sử bạn đã có giá trị này
                DecimalFormat df = new DecimalFormat("#,###"); // Định dạng với dấu phẩy
                String formattedPrice = df.format(price2).replace(",", ".") + " VND";;
                model.addAttribute("servicePrice", formattedPrice);
                model.addAttribute("paymentMethod", bookedService.getPaymentMethod());
                model.addAttribute("notes", bookedService.getNote());
                model.addAttribute("customerName", bookedService.getUser().getFullName());
                model.addAttribute("customerPhone", bookedService.getUser().getPhone());
                model.addAttribute("customerEmail", bookedService.getUser().getEmail());
                model.addAttribute("petName", bookedService.getPet().getName());
                model.addAttribute("petAge", bookedService.getPet().getAge());
                model.addAttribute("petBreed", bookedService.getPet().getType());
                model.addAttribute("petWeight", bookedService.getPet().getWeight());

                return "user/booking-success";
            }
        } else {
            log.error("Payment failed with response code: {}", responseCode);
            bookedServiceRepository.deleteById(Integer.parseInt(orderId));
            return "redirect:/cancell-payment";
        }
        return "redirect:/errorpage";
    }
}