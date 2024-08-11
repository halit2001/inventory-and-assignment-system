package com.example.debit_and_inventory.service.equipment;

import com.example.debit_and_inventory.Dto.EquipmentDto;
import com.example.debit_and_inventory.Dto.EquipmentMonthlyCountDto;
import com.example.debit_and_inventory.Dto.UpdateEquipmentDto;
import com.example.debit_and_inventory.model.Equipment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EquipmentService {
    Optional<Equipment> addEquipment(EquipmentDto equipmentDto);

    List<Equipment> addEquipments(List<EquipmentDto> equipmentDtos);

    Optional<Equipment> getEquipmentBySerialNumber(String serialNumber);

    Optional<Equipment> updateEquipment(UpdateEquipmentDto updateEquipmentDto, String equipmentSerialNumber);

    String deleteEquipment(String serialNumber);

    Optional<List<Equipment>> getAllEquipments();

    List<Equipment> searchEquipmentsByQueries(String serialNumber, String type, String brand, String model);

    Integer getEquipmentCount(String equipmentType);

    Integer getTotalEquipmentsCount();

    Optional<List<EquipmentMonthlyCountDto>> getMonthlyEquipmentTrends(String equipmentType);

    String getUsageStatus(String serialNumber);
}
