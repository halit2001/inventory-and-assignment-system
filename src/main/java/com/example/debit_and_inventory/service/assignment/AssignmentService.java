package com.example.debit_and_inventory.service.assignment;

import com.example.debit_and_inventory.Dto.AssignmentDto;
import com.example.debit_and_inventory.model.Assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {
    Assignment createAssignment(AssignmentDto assignmentDto);

    Assignment addEquipmentToAssignment(String equipmentSerialNumber, String employeeEmail);

    String deleteAssignment(Long assignmentId);

    Optional<Assignment> getAssignment(Long assignmentId);

    String removeEquipmentFromAssignment(String equipmentSerialNumber);

    List<Assignment> getAllAssignments();

    Assignment getAssignmentByEquipmentSerialNumber(String equipmentSerialNumber);

    Assignment getAssignmentByEmployeeEmail(String employeeEmail);

    String deleteAssignmentByEmployeeEmail(String employeeEmail);
}
