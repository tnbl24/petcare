package org.datn.petcare.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceDTO {
    private int id;
    private String name;
    private String description;
    private double price;
    private byte[] image;
    private MultipartFile imageFile;

    private String base64Image;

    private boolean isDeleted;

    private String hasDetail;

    private int groupServiceId;
    private List<ServiceDetailDTO> serviceDetails;

}
