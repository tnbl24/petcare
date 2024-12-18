package org.datn.petcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Builder
@Table
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    @Lob
    private byte[] image;
    private double price;

    private String hasDetail;

    private String base64Image;
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "group_service_id", nullable = false)
    private GroupService groupService;

    @ManyToMany
    @JoinTable(
            name = "ServiceMapping",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "detail_id")
    )
    private List<ServiceDetail> serviceDetails;
}
