package com.example.debit_and_inventory.controller;

import com.example.debit_and_inventory.Dto.AssignmentDto;
import com.example.debit_and_inventory.model.Assignment;
import com.example.debit_and_inventory.service.assignment.AssignmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/get_assignment/{assignment_id}")
    public ResponseEntity<Assignment> getAssignment(@PathVariable(name = "assignment_id") Long assignment_id) {
        Optional<Assignment> optionalAssignment = assignmentService.getAssignment(assignment_id);
        return optionalAssignment.map(assignment -> ResponseEntity.status(HttpStatus.OK).body(assignment)).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping("/get_all_assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        List<Assignment> assignmentList = assignmentService.getAllAssignments();
        if (assignmentList.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).body(assignmentList);
    }

    @GetMapping("/get_assignment_by_equipment_serial_number")
    public ResponseEntity<?> getAssignmentByEquipmentSerialNumber(@RequestParam(name = "equipment_serial_number", required = true) String equipment_serial_number) {
        try {
            Assignment assignment = assignmentService.getAssignmentByEquipmentSerialNumber(equipment_serial_number);
            return ResponseEntity.status(HttpStatus.OK).body(assignment);
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/get_assignment_by_employee_email")
    public ResponseEntity<?> getAssignmentByEmployeeEmail(@RequestParam(name = "employee_email", required = true) String employee_email) {
        try {
            Assignment assignment = assignmentService.getAssignmentByEmployeeEmail(employee_email);
            return ResponseEntity.status(HttpStatus.OK).body(assignment);
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/create_assignment")
    public ResponseEntity<?> createAssignment(@Valid @RequestBody AssignmentDto assignmentDto, BindingResult results) {
        if (results.hasErrors()) {
            String error_message = results.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error_message);
        }
        try {
            Assignment assignment = assignmentService.createAssignment(assignmentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PatchMapping("/add_equipment_to_assignment/{equipment_serial_number}/{employee_email}")
    public ResponseEntity<?> addEquipmentToAssignment(@PathVariable(name = "equipment_serial_number", required = true) String equipment_serial_number,
                                                      @PathVariable(name = "employee_email", required = true) String employee_email) {
        try {
            Assignment assignment = assignmentService.addEquipmentToAssignment(equipment_serial_number, employee_email);
            return ResponseEntity.status(HttpStatus.OK).body(assignment);
        } catch (EntityNotFoundException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PatchMapping("/remove_equipment_from_assignment/{equipment_serial_number}")
    public ResponseEntity<String> removeEquipmentFromAssignment(@PathVariable(name = "equipment_serial_number", required = true) String equipment_serial_number) {
        try {
            String message = assignmentService.removeEquipmentFromAssignment(equipment_serial_number);
            return ResponseEntity.status(HttpStatus.OK).body("Equipment has deleted from assignment");
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete_assignment_mock/{assignment_id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable(name = "assignment_id") Long assignment_id) {
        String message = assignmentService.deleteAssignment(assignment_id);
        if (message.equals("deleted")) return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not deleted");
    }

    @DeleteMapping("/delete_assignment_by_employee_email/employee/{employee_email}")
    public ResponseEntity<String> deleteAssignmentByEmployeeEmail(@PathVariable(name = "employee_email", required = true) String employee_email) {
        try {
            String deleteMessage = assignmentService.deleteAssignmentByEmployeeEmail(employee_email);
            return ResponseEntity.status(HttpStatus.OK).body("Assignment was deleted");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
