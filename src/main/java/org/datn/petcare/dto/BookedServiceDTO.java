package org.datn.petcare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookedServiceDTO {
    private int id;
    private LocalDate bookingDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime bookingTime;

    private String note;
    private String paymentMethod;
    private String status;

    private double price;

    private LocalDateTime createAt;

    private String userId;
    private int serviceId;
    private int petId;
    private int ratingId;

}
