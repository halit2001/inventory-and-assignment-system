package com.example.debit_and_inventory.service.assignment;

import com.example.debit_and_inventory.Dto.AssignmentDto;
import com.example.debit_and_inventory.mapper.AssignmentMapper;
import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.model.Employee;
import com.example.debit_and_inventory.model.Equipment;
import com.example.debit_and_inventory.repository.AssignmentRepository;
import com.example.debit_and_inventory.repository.EmployeeRepository;
import com.example.debit_and_inventory.repository.EquipmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    private final AssignmentMapper assignmentMapper = new AssignmentMapper();

    @Override
    @Transactional
    public Assignment createAssignment(AssignmentDto assignmentDto) {
        // This method checks if both the Employee and Equipment exist based on the provided email and serial number. If neither is already assigned and both are present,
        // it creates a new assignment. The assignment is then saved, and both the employee and equipment are updated to reference this new assignment.
        // If either the employee or equipment is already assigned, it throws an IllegalArgumentException.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(assignmentDto.getEquipment_serial_number());
        Optional<Employee> optionalEmployee = employeeRepository.findEmployeeByEmail(assignmentDto.getEmployee_email());
        if (optionalEmployee.isPresent() && optionalEquipment.isPresent()) {
            if (optionalEquipment.get().getAssignment() != null || optionalEmployee.get().getAssignment() != null) {
                throw new IllegalArgumentException("Equipment or employee had already been assigned to another assignment");
            }
            Assignment assignment = assignmentMapper.createNewAssignment(LocalDateTime.now(), optionalEquipment.get(), optionalEmployee.get());
            optionalEmployee.get().setAssignment(assignment);
            optionalEquipment.get().setAssignment(assignment);
            assignmentRepository.save(assignment);
            employeeRepository.save(optionalEmployee.get());
            equipmentRepository.save(optionalEquipment.get());
            return assignment;
        }
        throw new EntityNotFoundException("Equipment or employee could not found");
    }

    @Override
    @Transactional
    public Assignment addEquipmentToAssignment(String equipmentSerialNumber, String employeeEmail) {
        // This method first checks if both the Employee and Equipment exist. It then either adds the equipment to the employee's existing assignment or creates a new assignment if the employee does not have one.
        // If the equipment was previously assigned to another assignment, it is removed from that assignment. The assignment is updated or created accordingly.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(equipmentSerialNumber);
        Optional<Employee> optionalEmployee = employeeRepository.findEmployeeByEmail(employeeEmail);
        if (optionalEmployee.isPresent() && optionalEquipment.isPresent()) {
            Employee employee = optionalEmployee.get();
            Equipment equipment = optionalEquipment.get();
            // employee can have a particular assignment and has an assignment id.
            Assignment assignment;
            if (employee.getAssignment() != null) {
                assignment = employee.getAssignment();
                if (equipment.getAssignment() != null && !equipment.getAssignment().getId().equals(assignment.getId())) {
                    Assignment previousAssignment = equipment.getAssignment();
                    previousAssignment.getEquipment().remove(equipment);
                    equipment.setAssignment(null);
                    assignmentRepository.save(previousAssignment);
                } else if (equipment.getAssignment() == assignment) {
                    throw new IllegalArgumentException("Equipment and employee have already been same assignment");
                }
                assignment.getEquipment().add(equipment);
                equipment.setAssignment(assignment);
                assignment.setUpdated_at(LocalDateTime.now());
            } else {
                // employee can not have a particular assignment.
                if (equipment.getAssignment() != null) {
                    Assignment previousAssignment = equipment.getAssignment();
                    previousAssignment.getEquipment().remove(equipment);
                    equipment.setAssignment(null);
                    if (previousAssignment.getEquipment().isEmpty()) {
                        assignmentRepository.delete(previousAssignment);
                    } else {
                        assignmentRepository.save(previousAssignment);
                    }
                }
                assignment = assignmentMapper.createNewAssignment(LocalDateTime.now(), equipment, employee);
                equipment.setAssignment(assignment);
                employee.setAssignment(assignment);
            }
            employeeRepository.save(employee);
            equipmentRepository.save(equipment);
            return assignmentRepository.save(assignment);
        }
        throw new EntityNotFoundException("Employee or equipment not found");
    }

    @Override
    @Transactional
    public String deleteAssignment(Long assignmentId) {
        //  This method finds the assignment by its ID. If found, it removes the assignment from all associated equipment and the employee, then deletes the assignment itself.
        //  It returns "deleted" if successful or "not deleted" if the assignment was not found.
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(assignmentId);
        if (optionalAssignment.isPresent()) {
            List<Equipment> equipments = optionalAssignment.get().getEquipment();
            for (Equipment equipment : equipments) {
                equipment.setAssignment(null);
                equipmentRepository.save(equipment);
            }
            Employee employee = optionalAssignment.get().getEmployee();
            if (employee != null) {
                employee.setAssignment(null);
                employeeRepository.save(employee);
            }
            assignmentRepository.delete(optionalAssignment.get());
            return "deleted";
        }
        return "not deleted";
    }

    @Override
    @Transactional
    public Optional<Assignment> getAssignment(Long assignmentId) {
        // This method returns the assignment with the specified ID, wrapped in an Optional. If the assignment is not found, it returns an empty Optional.
        return assignmentRepository.findById(assignmentId);
    }

    @Override
    @Transactional
    public String removeEquipmentFromAssignment(String equipmentSerialNumber) {
        // This method finds the equipment by its serial number. If the equipment is assigned, it removes it from the assignment and updates both the equipment and assignment in the repository.
        // If the equipment is not assigned or does not exist, it throws an IllegalArgumentException or EntityNotFoundException.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(equipmentSerialNumber);
        if (optionalEquipment.isPresent()) {
            Assignment assignment = optionalEquipment.get().getAssignment();
            if (assignment != null) {
                assignment.getEquipment().remove(optionalEquipment.get());
                optionalEquipment.get().setAssignment(null);
                equipmentRepository.save(optionalEquipment.get());
                assignmentRepository.save(assignment);
                return "Extracted";
            }
            throw new IllegalArgumentException("Equipment had already not been assigned any assignment");
        }
        throw new EntityNotFoundException("Equipment not exists");
    }

    @Override
    public List<Assignment> getAllAssignments() {
        // This method returns a list of all assignments from the repository.
        return assignmentRepository.findAll();
    }

    @Override
    @Transactional
    public Assignment getAssignmentByEquipmentSerialNumber(String equipmentSerialNumber) {
        // This method finds the equipment by its serial number and returns its associated assignment. If the equipment is not found or is not assigned,
        // it throws an IllegalArgumentException or EntityNotFoundException.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(equipmentSerialNumber);
        if (optionalEquipment.isPresent()) {
            Assignment assignment = optionalEquipment.get().getAssignment();
            if (assignment != null) {
                return assignment;
            }
            throw new IllegalArgumentException("Equipment had already not been assigned any assignment");
        }
        throw new EntityNotFoundException("Equipment not exists");
    }

    @Override
    public Assignment getAssignmentByEmployeeEmail(String employeeEmail) {
        // This method finds the employee by their email and returns their associated assignment.
        // If the employee is not found or does not have an assignment, it throws an IllegalArgumentException or EntityNotFoundException.
        Optional<Employee> optionalEmployee = employeeRepository.findEmployeeByEmail(employeeEmail);
        if (optionalEmployee.isPresent()) {
            Assignment assignment = optionalEmployee.get().getAssignment();
            if (assignment != null) {
                return assignment;
            }
            throw new IllegalArgumentException("Employee had already not been assigned any assignment");
        }
        throw new EntityNotFoundException("Employee not exists");
    }

    @Override
    @Transactional
    public String deleteAssignmentByEmployeeEmail(String employeeEmail) {
        // This method finds the employee by their email. If the employee has an assignment, it removes the assignment from all associated equipment, clears the assignment from the employee, and deletes the assignment.
        // It returns "deleted" if successful or throws an exception if the assignment or employee is not found.
        Optional<Employee> optionalEmployee = employeeRepository.findEmployeeByEmail(employeeEmail);
        if (optionalEmployee.isPresent()) {
            Assignment assignment = optionalEmployee.get().getAssignment();
            if (assignment != null) {
                if (!assignment.getEquipment().isEmpty()) {
                    List<Equipment> equipmentList = new ArrayList<>(assignment.getEquipment());
                    for(Equipment equipment : equipmentList) {
                        equipment.setAssignment(null);
                        equipmentRepository.save(equipment);
                    }
                    assignment.getEquipment().clear();
                }
                Employee employee = assignment.getEmployee();
                assignment.setEmployee(null);
                employee.setAssignment(null);
                employeeRepository.save(employee);
                assignmentRepository.delete(assignment);
                return "deleted";
            }
            throw new EntityNotFoundException("Assignment not found");
        }
        throw new EntityNotFoundException("Employee not found");
    }

}
