package com.example.debit_and_inventory.repository;

import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findAssignmentByEmployee(Employee employee);
}
