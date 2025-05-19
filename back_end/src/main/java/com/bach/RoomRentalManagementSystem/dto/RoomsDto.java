package com.bach.RoomRentalManagementSystem.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomsDto {

    private Long building_id;
    private List<RoomDto> roomsDto;
}
