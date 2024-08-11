package com.example.debit_and_inventory.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EquipmentDto {
    @NotNull(message = "SerialNumber can not be empty")
    @Size(min = 6, max = 14, message = "SerialNumber size must be between 6 and 14")
    private String serialNumber;

    @NotNull(message = "Type can not be empty")
    private String type;

    @NotNull(message = "Model can not be empty")
    private String model;

    @NotNull(message = "Brand can not be empty")
    private String brand;

    private LocalDateTime  created_at;
    private LocalDateTime updated_at;
    private Long assignment_id;

    public EquipmentDto(String serialNumber, String type, String model, String brand, LocalDateTime created_at) {
        this.serialNumber = serialNumber;
        this.type = type;
        this.model = model;
        this.brand = brand;
        this.created_at = created_at;
    }
}
