package com.example.debit_and_inventory.service.employee;

import com.example.debit_and_inventory.Dto.EmployeeDto;
import com.example.debit_and_inventory.Dto.UpdateEmployeeDto;
import com.example.debit_and_inventory.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee addEmployee(EmployeeDto employeeDto);

    Optional<Employee> updateEmployee(UpdateEmployeeDto updateEmployeeDto, Long employeeId);

    List<Employee> addEmployees(List<EmployeeDto> employeeDtos);

    String deleteEmployee(String employeeEmail);

    Optional<Employee> getEmployee(String employeeEmail);

    List<Employee> getEmployees(String firstname, String lastname, String department, String role, String location, Boolean active);

    Integer getEmployeeCount();

    String deleteEmployeeById(Long employeeId);
}
