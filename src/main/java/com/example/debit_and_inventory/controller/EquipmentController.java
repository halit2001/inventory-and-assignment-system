package com.example.debit_and_inventory.controller;

import com.example.debit_and_inventory.Dto.EquipmentDto;
import com.example.debit_and_inventory.Dto.EquipmentMonthlyCountDto;
import com.example.debit_and_inventory.Dto.UpdateEquipmentDto;
import com.example.debit_and_inventory.model.Equipment;
import com.example.debit_and_inventory.service.equipment.EquipmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/equipment")
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/get_equipment/{equipment_serial_number}")
    public ResponseEntity<Equipment> getEquipmentBySerialNumber(@PathVariable("equipment_serial_number") String equipment_serial_number) {
        Optional<Equipment> optionalEquipment = equipmentService.getEquipmentBySerialNumber(equipment_serial_number);
        return optionalEquipment.map(equipment -> ResponseEntity.status(HttpStatus.OK).body(equipment)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/get_all_equipments")
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        Optional<List<Equipment>> optionalEquipmentList = equipmentService.getAllEquipments();
        return optionalEquipmentList.map(equipment -> ResponseEntity.status(HttpStatus.OK).body(equipment)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Equipment>> searchEquipmentsByQueries(@RequestParam(name = "serial_number", required = false) String serialNumber,
                                                                     @RequestParam(name = "type", required = false) String type,
                                                                     @RequestParam(name = "brand", required = false) String brand,
                                                                     @RequestParam(name = "model", required = false) String model) {
        List<Equipment> equipmentList = equipmentService.searchEquipmentsByQueries(serialNumber, type, brand, model);
        if (equipmentList.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).body(equipmentList);
    }

    @GetMapping("/total_count/{equipment_type}")
    public ResponseEntity<String> getEquipmentCount(@PathVariable String equipment_type) {
        Integer equipment_count = equipmentService.getEquipmentCount(equipment_type);
        if (equipment_count == 0)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipment not exists in database");
        return ResponseEntity.status(HttpStatus.OK).body("Equipment with name " + equipment_type + " exists with equipment count : " + equipment_count);
    }

    @GetMapping("/total_all_equipments_count")
    public ResponseEntity<String> getTotalEquipmentsCount() {
        Integer equipments_count = equipmentService.getTotalEquipmentsCount();
        if (equipments_count == 0) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Count is 0");
        return ResponseEntity.status(HttpStatus.OK).body("Equipments count are " + equipments_count);
    }

    @GetMapping("/monthly_addition_trends/{equipment_type}")
    public ResponseEntity<List<EquipmentMonthlyCountDto>> getMonthlyEquipmentTrends(@PathVariable String equipment_type) {
        Optional<List<EquipmentMonthlyCountDto>> optionalEquipmentMonthlyCountDto = equipmentService.getMonthlyEquipmentTrends(equipment_type);
        return optionalEquipmentMonthlyCountDto.map(equipmentMonthlyCountDtos -> ResponseEntity.status(HttpStatus.OK).body(equipmentMonthlyCountDtos)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/usage_status/{serial_number}")
    public ResponseEntity<String> getUsageStatus(@PathVariable(name = "serial_number") String serial_number) {
        try {
            String message = equipmentService.getUsageStatus(serial_number);
            if (message.equals("Equipment is being used")) return ResponseEntity.status(HttpStatus.OK).body(message);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/add_equipment")
    public ResponseEntity<?> addEquipment(@Valid @RequestBody EquipmentDto equipmentDto, BindingResult results) {
        if (results.hasErrors()) {
            String errorMessage = results.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        Optional<Equipment> optionalEquipment = equipmentService.addEquipment(equipmentDto);
        return optionalEquipment.map(equipment -> ResponseEntity.status(HttpStatus.CREATED).body(optionalEquipment.get()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping("add_equipments")
    public ResponseEntity<?> addEquipments(@Valid @RequestBody List<EquipmentDto> equipmentDtos, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorList = new ArrayList<>();
            result.getFieldErrors().forEach(fieldError -> errorList.add("Error field is " + fieldError.getField() + "Error message " + fieldError.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorList);
        }
        List<Equipment> equipmentList = equipmentService.addEquipments(equipmentDtos);
        if (equipmentList.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentList);
    }

    @PatchMapping("/update_equipment/{equipment_serial_number}")
    public ResponseEntity<Equipment> updateEquipment(@RequestBody UpdateEquipmentDto updateEquipmentDto, @PathVariable String equipment_serial_number) {
        Optional<Equipment> optionalEquipment = equipmentService.updateEquipment(updateEquipmentDto, equipment_serial_number);
        return optionalEquipment.map(equipment -> ResponseEntity.status(HttpStatus.OK).body(equipment)).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @DeleteMapping("/delete_equipment/{serial_number}")
    public ResponseEntity<String> deleteEquipment(@PathVariable String serial_number) {
        try {
            String isDeleted = equipmentService.deleteEquipment(serial_number);
            return ResponseEntity.status(HttpStatus.OK).body(isDeleted);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
