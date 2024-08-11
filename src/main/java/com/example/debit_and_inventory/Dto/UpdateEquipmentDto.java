package com.example.debit_and_inventory.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEquipmentDto {
    private String type;
    private String brand;
    private String model;
    private LocalDateTime updated_at;
}
