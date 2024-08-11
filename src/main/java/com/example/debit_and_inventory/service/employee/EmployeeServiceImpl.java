package com.example.debit_and_inventory.service.employee;

import com.example.debit_and_inventory.Dto.EmployeeDto;
import com.example.debit_and_inventory.Dto.UpdateEmployeeDto;
import com.example.debit_and_inventory.mapper.EmployeeMapper;
import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.model.Employee;
import com.example.debit_and_inventory.model.QEmployee;
import com.example.debit_and_inventory.repository.AssignmentRepository;
import com.example.debit_and_inventory.repository.EmployeeRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    private AssignmentRepository assignmentRepository;

    private final EmployeeMapper employeeMapper = new EmployeeMapper();

    public EmployeeServiceImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Employee addEmployee(EmployeeDto employeeDto) {
        // This method converts an EmployeeDto to an Employee entity using a mapper and saves it to the repository.
        return employeeRepository.save(employeeMapper.convertEmployeeDtoToEntityForAdding(employeeDto));
    }

    @Transactional
    @Override
    public Optional<Employee> updateEmployee(UpdateEmployeeDto updateEmployeeDto, Long employeeId) {
        // This method first retrieves an employee by its ID. If the employee exists, it updates the employee using the provided UpdateEmployeeDto,
        // updates any associated assignments if necessary, and saves the changes. If the employee is not found, it throws an EntityNotFoundException.
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee updatedEmployee = employeeMapper.convertEmployeeDtoToEntityForUpdate(optionalEmployee.get(), updateEmployeeDto);
            Assignment assignment = optionalEmployee.get().getAssignment();
            if (assignment != null) {
                assignment.setEmployee(updatedEmployee);
                assignmentRepository.save(assignment);
            }
            return Optional.of(employeeRepository.save(updatedEmployee));
        }
        throw new EntityNotFoundException("Employee not found with employee id : " + employeeId);
    }


    @Override
    public List<Employee> addEmployees(List<EmployeeDto> employeeDtos) {
        // This method removes duplicate EmployeeDto objects, checks for existing emails to filter out already existing employees,
        // and saves the non-duplicate employees to the repository.
        Optional<Set<String>> optionalEmails = employeeRepository.findAllEmails();
        List<EmployeeDto> nonDuplicateEmployeeDtos = employeeDtos.stream().distinct().toList();
        if (optionalEmails.isPresent()) {
            List<EmployeeDto> filteredEmployeeDtos = nonDuplicateEmployeeDtos.stream().filter(employeeDto -> !optionalEmails.get().contains(employeeDto.getEmail())).toList();
            return filteredEmployeeDtos.stream().map(employeeMapper::convertEmployeeDtoToEntityForAdding).map(employee -> employeeRepository.save(employee)).toList();
        }
        return nonDuplicateEmployeeDtos.stream().map(employeeMapper::convertEmployeeDtoToEntityForAdding).map(employee -> employeeRepository.save(employee)).toList();
    }

    @Override
    @Transactional
    public String deleteEmployee(String employeeEmail) {
        // his method locates an employee by their email and deletes the employee if found.
        // If the employee is not found, it returns "not deleted"; otherwise, it returns "deleted".
        Optional<Employee> existedEmployee = employeeRepository.findEmployeeByEmail(employeeEmail);
        if (existedEmployee.isPresent()) {
            Assignment assignment = existedEmployee.get().getAssignment();
            employeeRepository.delete(existedEmployee.get());
            return "deleted";
        }
        return "not deleted";
    }

    @Override
    public Optional<Employee> getEmployee(String employeeEmail) {
        //  This method queries the repository for an employee with the specified email and returns it wrapped in an Optional.
        //  If the employee is not found, it returns an empty Optional.
        return employeeRepository.findEmployeeByEmail(employeeEmail);
    }

    @Override
    public List<Employee> getEmployees(String firstname, String lastname, String department, String role, String location, Boolean active) {
        // This method builds a query to search for employees using the provided parameters (firstname, lastname, department, role, location, active status).
        // It applies filters based on non-null input values and returns the list of matching employees.
        QEmployee qEmployee = QEmployee.employee;
        JPAQuery<Employee> query = jpaQueryFactory.selectFrom(qEmployee).where(firstname != null ? qEmployee.firstname.containsIgnoreCase(firstname) : null,
                lastname != null ? qEmployee.lastname.containsIgnoreCase(lastname) : null, active != null ? qEmployee.active.eq(active) : null,
                department != null ? qEmployee.department.containsIgnoreCase(department) : null,
                location != null ? qEmployee.location.containsIgnoreCase(location) : null, role != null ? qEmployee.role.containsIgnoreCase(role) : null);
        return (query.fetch());
    }

    @Override
    public Integer getEmployeeCount() {
        // Retrieves the total count of employees.
        return employeeRepository.findEmployeeCount();
    }

    @Override
    public String deleteEmployeeById(Long employeeId) {
        // This method retrieves an employee by their ID. If the employee exists, it deletes the employee and returns "Deleted". If not found, it returns "Not Deleted".
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            employeeRepository.delete(optionalEmployee.get());
            return "Deleted";
        }
        return "Not Deleted";
    }
}
