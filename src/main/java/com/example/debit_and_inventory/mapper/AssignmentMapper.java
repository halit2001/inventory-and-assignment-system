package com.example.debit_and_inventory.mapper;

import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.model.Employee;
import com.example.debit_and_inventory.model.Equipment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignmentMapper {

    public Assignment createNewAssignment(LocalDateTime createdAt, Equipment equipment, Employee employee) {
        Assignment assignment = new Assignment();
        List<Equipment> employeeList = new ArrayList<>();
        if(createdAt != null) {
            assignment.setCreated_at(createdAt);
        }
        assignment.setEmployee(employee);
        employeeList.add(equipment);
        assignment.setEquipment(employeeList);
        return assignment;
    }
}
