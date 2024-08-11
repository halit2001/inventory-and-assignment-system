package com.example.debit_and_inventory.repository;

import com.example.debit_and_inventory.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>, QuerydslPredicateExecutor<Equipment> {
    Optional<Equipment> findBySerialNumber(String serialNumber);
    Equipment findEquipmentBySerialNumber(String serialNumber);

    @Query(value = "SELECT serial_number FROM equipment", nativeQuery = true)
    Set<String> findAllSerialNumbers();

    @Query(value = "SELECT COUNT(*) FROM equipment WHERE type = :equipmentType", nativeQuery = true)
    Integer findEquipmentCount(@Param("equipmentType") String equipmentType);

    @Query(value = "SELECT COUNT(*) FROM equipment", nativeQuery = true)
    Integer findAllEquipmentsCount();

    @Query(value = "SELECT date_format(created_at, '%Y-%M') AS created_month, COUNT(*) FROM equipment WHERE type = :equipment_type group by created_month order by created_month", nativeQuery = true)
    List<Object[]> findMonthlyTrends(@Param("equipment_type") String equipment_type);
}
