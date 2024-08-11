package com.example.debit_and_inventory.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeDto {
    private String firstname;
    private String lastname;
    private String department;
    private String role;
    private String location;
}
