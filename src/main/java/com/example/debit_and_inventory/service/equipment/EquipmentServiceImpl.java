package com.example.debit_and_inventory.service.equipment;

import com.example.debit_and_inventory.Dto.EquipmentDto;
import com.example.debit_and_inventory.Dto.EquipmentMonthlyCountDto;
import com.example.debit_and_inventory.Dto.UpdateEquipmentDto;
import com.example.debit_and_inventory.mapper.EquipmentMapper;
import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.model.Equipment;
import com.example.debit_and_inventory.model.QEquipment;
import com.example.debit_and_inventory.repository.AssignmentRepository;
import com.example.debit_and_inventory.repository.EquipmentRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    private AssignmentRepository assignmentRepository;

    private final EquipmentMapper equipmentMapper = new EquipmentMapper();

    @Autowired
    public EquipmentServiceImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<Equipment> addEquipment(EquipmentDto equipmentDto) {
        //  Adds a new piece of equipment to the repository if it does not already exist.
       Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(equipmentDto.getSerialNumber());
       if (optionalEquipment.isPresent()) return Optional.empty();
       return Optional.of(equipmentRepository.save(equipmentMapper.ConvertEquipmentFromEquipmentDtoForFirstTime(equipmentDto)));
    }

    @Override
    public List<Equipment> addEquipments(List<EquipmentDto> equipmentDtos) {
        //  Adds multiple pieces of equipment, ensuring no duplicates are saved.
        List<Equipment> duplicateEquipments = equipmentDtos.stream().map(equipmentDto -> equipmentRepository.findEquipmentBySerialNumber(equipmentDto.getSerialNumber())).toList();
        if (duplicateEquipments.isEmpty()) {
            return equipmentDtos.stream().map(equipmentMapper::ConvertEquipmentFromEquipmentDtoForFirstTime).map(convertedEquipment -> equipmentRepository.save(convertedEquipment)).toList();
        }
        Set<String> serialNumbersSet = equipmentRepository.findAllSerialNumbers();
        List<Equipment> filteredEquipments = equipmentDtos.stream().filter(equipmentDto -> !serialNumbersSet.contains(equipmentDto.getSerialNumber())).map(equipmentMapper::ConvertEquipmentFromEquipmentDtoForFirstTime).toList();
        return filteredEquipments.stream().map(filteredEquipment -> equipmentRepository.save(filteredEquipment)).toList();
    }

    @Override
    public Optional<Equipment> getEquipmentBySerialNumber(String serialNumber) {
        // Retrieves an equipment by its serial number.
        return equipmentRepository.findBySerialNumber(serialNumber);
    }

    @Override
    @Transactional
    public Optional<Equipment> updateEquipment(UpdateEquipmentDto updateEquipmentDto, String equipmentSerialNumber) {
        // Updates an existing equipment with new details.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(equipmentSerialNumber);
        return optionalEquipment.map(equipment -> equipmentRepository.save(equipmentMapper.mapDtoToEquipmentAndUpdateAssignment(equipment, updateEquipmentDto)));
    }

    @Override
    @Transactional
    public String deleteEquipment(String serialNumber) {
        // Deletes an equipment by its serial number.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(serialNumber);
        if (optionalEquipment.isPresent()) {
            Assignment assignment = optionalEquipment.get().getAssignment();
            if (assignment != null) {
                assignment.getEquipment().remove(optionalEquipment.get());
                assignmentRepository.save(assignment);
            }
            equipmentRepository.delete(optionalEquipment.get());
            return "Deleted";
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Optional<List<Equipment>> getAllEquipments() {
        // Retrieves all equipment from the repository.
        return Optional.of(equipmentRepository.findAll());
    }

    @Override
    public List<Equipment> searchEquipmentsByQueries(String serialNumber, String type, String brand, String model) {
        // Searches for equipment based on various criteria.
        QEquipment equipment = QEquipment.equipment;
        JPAQuery<Equipment> query = jpaQueryFactory.selectFrom(equipment);
        if (serialNumber != null) {
            query.where(equipment.serialNumber.eq(serialNumber));
        }
        if (type != null) {
            query.where(equipment.type.containsIgnoreCase(type));
        }
        if (brand != null) {
            query.where(equipment.brand.containsIgnoreCase(brand));
        }
        if (model != null) {
            query.where(equipment.model.containsIgnoreCase(model));
        }
        return query.fetch();
    }

    @Override
    public Integer getEquipmentCount(String equipmentType) {
        // Retrieves the count of equipment of a specific type.
        return equipmentRepository.findEquipmentCount(equipmentType);
    }

    @Override
    public Integer getTotalEquipmentsCount() {
        // Retrieves the total count of all equipment.
        return equipmentRepository.findAllEquipmentsCount();
    }

    @Override
    public Optional<List<EquipmentMonthlyCountDto>> getMonthlyEquipmentTrends(String equipmentType) {
        // Retrieves monthly trends for a specific equipment type.
        List<Object[]> objects = equipmentRepository.findMonthlyTrends(equipmentType);
        if (objects.isEmpty()) return Optional.empty();
        List<EquipmentMonthlyCountDto> equipmentMonthlyCountDtoList = new ArrayList<>();
        for (Object[] row : objects) {
            EquipmentMonthlyCountDto equipmentMonthlyCountDto = new EquipmentMonthlyCountDto();
            equipmentMonthlyCountDto.setCreated_month((String) row[0]);
            equipmentMonthlyCountDto.setCount((Long) row[1]);
            equipmentMonthlyCountDtoList.add(equipmentMonthlyCountDto);
        }
        return Optional.of(equipmentMonthlyCountDtoList);
    }

    @Override
    public String getUsageStatus(String serialNumber) {
        // Retrieves the usage status of an equipment by its serial number. This method checks if the equipment with the given serial number is in use. It returns a status message indicating whether the equipment is being used or not.
        Optional<Equipment> optionalEquipment = equipmentRepository.findBySerialNumber(serialNumber);
        if (optionalEquipment.isPresent()) {
            if(optionalEquipment.get().getAssignment() != null) return "Equipment is being used";
            return "Equipment is not being used";
        }
        throw new EntityNotFoundException("Equipment not found with serial number: " + serialNumber);
    }

}
