package com.example.debit_and_inventory.repository;

import com.example.debit_and_inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query(value = "SELECT email FROM employee", nativeQuery = true)
    Optional<Set<String>> findAllEmails();

    Optional<Employee> findEmployeeByEmail(String employeeEmail);

    @Query(value = "SELECT COUNT(*) FROM employee WHERE active = true", nativeQuery = true)
    Integer findEmployeeCount();
}
