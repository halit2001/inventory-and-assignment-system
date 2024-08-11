package com.example.debit_and_inventory.mapper;

import com.example.debit_and_inventory.Dto.EquipmentDto;
import com.example.debit_and_inventory.Dto.UpdateEquipmentDto;
import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.model.Equipment;
import com.example.debit_and_inventory.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class EquipmentMapper {

    @Autowired
    private AssignmentRepository assignmentRepository;

    public Equipment ConvertEquipmentFromEquipmentDtoForFirstTime(EquipmentDto equipmentDto) {
        Equipment equipment = new Equipment();
        equipment.setBrand(equipmentDto.getBrand());
        equipment.setModel(equipmentDto.getModel());
        equipment.setType(equipmentDto.getType());
        equipment.setCreated_at(equipmentDto.getCreated_at());
        equipment.setUpdated_at(null);
        equipment.setSerialNumber(equipmentDto.getSerialNumber());
        equipment.setAssignment(null);
        return equipment;
    }

    public Equipment mapDtoToEquipmentAndUpdateAssignment(Equipment equipment, UpdateEquipmentDto updateEquipmentDto) {
        if(updateEquipmentDto.getModel() != null) {
            equipment.setModel(updateEquipmentDto.getModel());
        }
        if(updateEquipmentDto.getType() != null) {
            equipment.setType(updateEquipmentDto.getType());
        }
        if(updateEquipmentDto.getBrand() != null) {
            equipment.setBrand(updateEquipmentDto.getBrand());
        }
        if(updateEquipmentDto.getUpdated_at() != null) {
            equipment.setUpdated_at(updateEquipmentDto.getUpdated_at());
        }
        return equipment;
    }
}
