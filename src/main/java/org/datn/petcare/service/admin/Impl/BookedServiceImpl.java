package org.datn.petcare.service.admin.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.dto.BookedServiceDTO;
import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.*;
import org.datn.petcare.mapper.BookedServiceMapper;
import org.datn.petcare.mapper.PetMapper;
import org.datn.petcare.mapper.ServicesMapper;
import org.datn.petcare.mapper.UserMapper;
import org.datn.petcare.repository.BookedServiceRepository;
import org.datn.petcare.repository.PetRepository;
import org.datn.petcare.repository.ServicesRepository;
import org.datn.petcare.repository.UserRepository;
import org.datn.petcare.service.admin.BookedServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookedServiceImpl implements BookedServiceService {

    @Autowired
    BookedServiceMapper bookedServiceMapper;

    @Autowired
    BookedServiceRepository serviceRepository;

    @Autowired
    ServicesMapper servicesMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServicesRepository servicesRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    PetRepository petRepository;

    @Override
    public List<BookedServiceDTO> getAll() {
        return serviceRepository.findAll()
                .stream()
                .map(bookedServiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookedServiceDTO> getByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return serviceRepository.findAll(pageable).map(bookedServiceMapper::toDTO);
    }

    @Override
    public BookedServiceDTO update(int id, BookedServiceDTO bookedServiceDTO) {
        Optional<BookedService> optionalBookedService = serviceRepository.findById(id);

        if (optionalBookedService.isPresent()) {
            BookedService existingService = optionalBookedService.get();

            String oldStatus = existingService.getStatus();

            if (bookedServiceDTO.getUserId() != null) {
                Optional<User> optionalUser = userRepository.findById(bookedServiceDTO.getUserId());
                optionalUser.ifPresent(existingService::setUser);
            }
            if (bookedServiceDTO.getServiceId() > 0) { // Kiểm tra serviceId hợp lệ
                Optional<Services> optionalService = servicesRepository.findById(bookedServiceDTO.getServiceId());
                optionalService.ifPresent(existingService::setServices);
            }
            if (bookedServiceDTO.getBookingTime() != null) {
                existingService.setBookingTime(bookedServiceDTO.getBookingTime());
            }
            if (bookedServiceDTO.getBookingDate() != null) {
                existingService.setBookingDate(bookedServiceDTO.getBookingDate());
            }
            if (bookedServiceDTO.getStatus() != null) {
                existingService.setStatus(bookedServiceDTO.getStatus());
            }
            if (bookedServiceDTO.getPrice() != 0.0) {
                existingService.setPrice(bookedServiceDTO.getPrice());
            }

            BookedService updatedService = serviceRepository.save(existingService);


            sendBookingConfirmationEmail(updatedService);


            return bookedServiceMapper.toDTO(updatedService);
        }

        return null;
    }

    private void sendBookingConfirmationEmail(BookedService bookedService) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(bookedService.getUser().getEmail()); // Đảm bảo có địa chỉ email
        message.setSubject("Dịch vụ chăm sóc thú cưng");

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Cảm ơn bạn đã đặt lịch dịch vụ. Dưới đây là thông tin đặt lịch của bạn:\n");

        LocalDateTime createAt = bookedService.getCreateAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = createAt.format(formatter);
        emailContent.append("\nThời gian tạo: ").append(formattedDateTime).append("\n");
        emailContent.append("Mã lịch đặt: ").append(bookedService.getId()).append("\n");
        String status = bookedService.getStatus();
        String statusInVietnamese;

        switch (status) {
            case "pending":
                statusInVietnamese = "Đang chờ xác nhận";
                break;
            case "confirmed":
                statusInVietnamese = "Đã xác nhận";
                break;
            case "completed":
                statusInVietnamese = "Hoàn thành";
                break;
            case "canceled":
                statusInVietnamese = "Đã hủy";
                break;
            default:
                statusInVietnamese = "Không xác định"; // Trường hợp không có trong danh sách
        }

        emailContent.append("Trạng thái lịch: ").append(statusInVietnamese).append("\n");

        emailContent.append("\n**Thông tin đặt lịch:**\n");
        double price = bookedService.getPrice();
        DecimalFormat df = new DecimalFormat("#");
        String formattedPrice = df.format(price);
        emailContent.append("Giá: ").append(formattedPrice).append(" VND\n");

        String paymentMethod = bookedService.getPaymentMethod();
        if ("offline".equals(paymentMethod)) {
            emailContent.append("Phương thức thanh toán: Trả tiền mặt\n");
        } else if ("online".equals(paymentMethod)) {
            emailContent.append("Phương thức thanh toán: Thanh toán online\n");
        } else {
            emailContent.append("Phương thức thanh toán: Không xác định\n");
        }

        emailContent.append("Ghi chú: ").append(bookedService.getNote()).append("\n");
        emailContent.append("Ngày đặt: ").append(bookedService.getBookingDate()).append("\n");
        emailContent.append("Giờ đặt: ").append(bookedService.getBookingTime()).append("\n");

        emailContent.append("\n**Thông tin khách hàng:**\n");
        emailContent.append("Họ và tên: ").append(bookedService.getUser().getFullName()).append("\n");
        emailContent.append("Số điện thoại: ").append(bookedService.getUser().getPhone()).append("\n");

        emailContent.append("\n**Thông tin thú cưng:**\n");

        Integer petId = bookedService.getPet().getId(); // Giả sử có phương thức này trong BookedService
        Optional<Pet> optionalPet = petRepository.findById(petId); // Thay thế petRepository bằng repository thực tế của bạn

        if (optionalPet.isPresent()) {
            Pet selectedPet = optionalPet.get();
            emailContent.append("Tên thú cưng: ").append(selectedPet.getName()).append("\n");
            emailContent.append("Tuổi: ").append(selectedPet.getAge()).append("\n");
            emailContent.append("Giống: ").append(selectedPet.getType()).append("\n");
            emailContent.append("Cân nặng: ").append(selectedPet.getWeight()).append("\n");
        } else {
            emailContent.append("Không tìm thấy thông tin thú cưng.\n");
        }

        message.setText(emailContent.toString());

        try {
            mailSender.send(message);
            log.info("Email xác nhận đã được gửi đến: " + bookedService.getUser().getEmail());
        } catch (Exception e) {
            log.error("Có lỗi xảy ra khi gửi email xác nhận: " + e.getMessage());
        }
    }
}
