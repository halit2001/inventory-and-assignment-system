package com.example.debit_and_inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Can not be empty")
    @Size(min = 6, max = 14, message = "Size must be between 6 and 14")
    @Column(nullable = false)
    private String serialNumber;

    @NotNull(message = "Can not be empty")
    @Column(nullable = false)
    private String type;

    @NotNull(message = "Can not be empty")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Can not be empty")
    @Column(nullable = false)
    private String brand;

    private LocalDateTime  created_at;
    private LocalDateTime  updated_at;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    @JsonIgnore
    private Assignment assignment;

    public Equipment(String serialNumber, String type, String model, String brand, LocalDateTime created_at) {
        this.serialNumber = serialNumber;
        this.type = type;
        this.model = model;
        this.brand = brand;
        this.created_at = created_at;
    }
}