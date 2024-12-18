package org.datn.petcare.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupServiceDTO {
    private int id;
    private String name;
    private int bookedCount;
    private boolean isDeleted;
}
