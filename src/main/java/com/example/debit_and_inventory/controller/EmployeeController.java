package com.example.debit_and_inventory.controller;

import com.example.debit_and_inventory.Dto.EmployeeDto;
import com.example.debit_and_inventory.Dto.UpdateEmployeeDto;
import com.example.debit_and_inventory.model.Employee;
import com.example.debit_and_inventory.service.employee.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping("/get_employee_by_email")
    public ResponseEntity<?> getEmployee(@RequestParam(name = "employee_email") String employee_email) {
        Optional<Employee> optionalEmployee = employeeService.getEmployee(employee_email);
        if(optionalEmployee.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found with email : " + employee_email);
        return ResponseEntity.status(HttpStatus.OK).body(optionalEmployee.get());
    }

    @GetMapping("/get_employees")
    public ResponseEntity<List<Employee>> getEmployees(@RequestParam(name = "firstname", required = false) String firstname,
                                          @RequestParam(name = "lastname", required = false) String lastname,
                                          @RequestParam(name = "department", required = false) String department,
                                          @RequestParam(name = "role", required = false) String role,
                                          @RequestParam(name = "location", required = false) String location,
                                          @RequestParam(name = "active", required = false) Boolean active) {
        List<Employee> employeeList = employeeService.getEmployees(firstname, lastname, department, role, location, active);
        if(employeeList.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(employeeList);
    }

    @GetMapping("/active_employee_count")
    public ResponseEntity<Integer> getEmployeeCount() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeCount());
    }

    @PostMapping("/add_employee")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody EmployeeDto employeeDto, BindingResult results) {
        if (results.hasErrors()) {
            String errorMessage = results.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        Employee employee = employeeService.addEmployee(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @PostMapping("/add_employees")
    public ResponseEntity<?> addEmployees(@Valid @RequestBody List<EmployeeDto> employeeDtos, BindingResult result) {
        if (employeeDtos.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dto is empty");
        if (result.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email fields can not be empty");
        List<Employee> employeeList = employeeService.addEmployees(employeeDtos);
        if(employeeList.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeList);
    }

    @PatchMapping("/update_employee/{employee_id}")
    public ResponseEntity<?> updateEmployee(@RequestBody UpdateEmployeeDto updateEmployeeDto, @PathVariable(name = "employee_id") Long employee_id) {
        try {
            Optional<Employee> optionalEmployee = employeeService.updateEmployee(updateEmployeeDto, employee_id);
            return ResponseEntity.status(HttpStatus.OK).body(optionalEmployee.get());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete_employee/{employee_email}")
    public ResponseEntity<String> deleteEmployee(@PathVariable(name = "employee_email") String employee_email) {
        String return_message = employeeService.deleteEmployee(employee_email);
        if(return_message.equals("deleted")) {
            return ResponseEntity.status(HttpStatus.OK).body("Employee successfully deleted with email email : " + employee_email);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee not deleted because employee email is not valid");
    }

    @DeleteMapping("/delete_employee_by_id/{employee_id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable(name = "employee_id") Long employee_id) {
        String message = employeeService.deleteEmployeeById(employee_id);
        if(message.equals("Deleted")) return ResponseEntity.status(HttpStatus.OK).body("Employee was deleted from repository");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID could not found with " + employee_id);
    }
}
