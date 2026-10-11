package com.ijse.moviebooking.dto.response;

import com.ijse.moviebooking.enums.TheatreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheatreResponse {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private TheatreStatus status;
}
