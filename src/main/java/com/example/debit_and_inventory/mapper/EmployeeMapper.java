package com.example.debit_and_inventory.mapper;

import com.example.debit_and_inventory.Dto.EmployeeDto;
import com.example.debit_and_inventory.Dto.UpdateEmployeeDto;
import com.example.debit_and_inventory.model.Employee;

public class EmployeeMapper {

    public Employee convertEmployeeDtoToEntityForAdding(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        if (employeeDto.getEmail() != null) {
            employee.setEmail(employeeDto.getEmail());
        }
        if (employeeDto.getDepartment() != null) {
            employee.setDepartment(employeeDto.getDepartment());
        }
        if (employeeDto.getFirstname() != null) {
            employee.setFirstname(employeeDto.getFirstname());
        }
        if (employeeDto.getLastname() != null) {
            employee.setLastname(employeeDto.getLastname());
        }
        if (employeeDto.getRole() != null) {
            employee.setRole(employeeDto.getRole());
        }
        if (employeeDto.getLocation() != null) {
            employee.setLocation(employeeDto.getLocation());
        }
        employee.setActive(true);
        return employee;
    }

    public Employee convertEmployeeDtoToEntityForUpdate(Employee employee, UpdateEmployeeDto updateEmployeeDto) {
        if (updateEmployeeDto.getFirstname() != null) {
            employee.setFirstname(updateEmployeeDto.getFirstname());
        }
        if (updateEmployeeDto.getLastname() != null) {
            employee.setLastname(updateEmployeeDto.getLastname());
        }
        if (updateEmployeeDto.getDepartment() != null) {
            employee.setDepartment(updateEmployeeDto.getDepartment());
        }
        if (updateEmployeeDto.getRole() != null) {
            employee.setRole(updateEmployeeDto.getRole());
        }
        if (updateEmployeeDto.getLocation() != null) {
            employee.setLocation(updateEmployeeDto.getLocation());
        }
        return employee;
    }
}
