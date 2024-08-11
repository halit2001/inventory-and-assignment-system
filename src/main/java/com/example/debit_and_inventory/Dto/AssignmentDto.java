package com.example.debit_and_inventory.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AssignmentDto {
    @NotNull(message = "email must be written")
    private String employee_email;

    @NotNull(message = "serial number must be written")
    private String equipment_serial_number;
}
