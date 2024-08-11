package com.example.debit_and_inventory.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeDto {
    @NotNull(message = "email can not be null")
    private String email;

    private String firstname;
    private String lastname;
    private String department;
    private String role;
    private String location;
}
