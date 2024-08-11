package com.example.debit_and_inventory.controller;

import com.example.debit_and_inventory.Dto.EquipmentDto;
import com.example.debit_and_inventory.Dto.EquipmentMonthlyCountDto;
import com.example.debit_and_inventory.Dto.UpdateEquipmentDto;
import com.example.debit_and_inventory.config.SecurityConfig;
import com.example.debit_and_inventory.model.Equipment;
import com.example.debit_and_inventory.service.equipment.EquipmentService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipmentController.class)
@Import(SecurityConfig.class)
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EquipmentService equipmentService;

    @Test
    @WithMockUser(username = "user")
    void getEquipmentBySerialNumber_Found() throws Exception {
        String serialNumber = "12345";
        Equipment equipment = new Equipment();
        equipment.setSerialNumber(serialNumber);
        when(equipmentService.getEquipmentBySerialNumber(serialNumber)).thenReturn(Optional.of(equipment));
        mockMvc.perform(get("/api/equipment/get_equipment/{equipment_serial_number}", serialNumber)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value(serialNumber));
    }

    @Test
    @WithMockUser(username = "user")
    void getEquipmentBySerialNumber_NotFound() throws Exception {
        String serialNumber = "12345";
        when(equipmentService.getEquipmentBySerialNumber(serialNumber)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/equipment/get_equipment/{serial_number}", serialNumber)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void getAllEquipments_Found() throws Exception {
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();
        equipment1.setSerialNumber("12345");
        equipment2.setSerialNumber("123456");
        List<Equipment> equipmentList = new ArrayList<>();
        equipmentList.add(equipment1);
        equipmentList.add(equipment2);
        when(equipmentService.getAllEquipments()).thenReturn(Optional.of(equipmentList));
        mockMvc.perform(get("/api/equipment/get_all_equipments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].serialNumber").value("12345"))
                .andExpect(jsonPath("$[1].serialNumber").value("123456"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllEquipments_NotFound() throws Exception {
        when(equipmentService.getAllEquipments()).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/equipment/get_all_equipments"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void searchEquipmentsByQueries_withAllParams() throws Exception {
        String serialNumberParam = "ASDRTY";
        String modelParam = "Rtx 2070";
        String typeParam = "Gpu";
        String brandParam = "Msi";
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();
        equipment1.setSerialNumber(serialNumberParam);
        equipment2.setSerialNumber("TTFRYF");
        equipment1.setModel(modelParam);
        equipment2.setModel(modelParam);
        equipment1.setType(typeParam);
        equipment2.setType(typeParam);
        equipment1.setBrand(brandParam);
        equipment2.setBrand(brandParam);
        List<Equipment> equipmentList = Arrays.asList(equipment1, equipment2);
        when(equipmentService.searchEquipmentsByQueries(serialNumberParam, typeParam, brandParam, modelParam))
                .thenReturn(equipmentList.stream().filter(equipment -> equipment.getSerialNumber().equals(serialNumberParam)).toList());
        mockMvc.perform(get("/api/equipment/search")
                        .param("serial_number", serialNumberParam)
                        .param("brand", brandParam)
                        .param("type", typeParam)
                        .param("model", modelParam)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].serialNumber").value(serialNumberParam));
    }

    @Test
    @WithMockUser(username = "user")
    void searchEquipmentsByQueries_withSomeParams() throws Exception {
        String brand = "Hp";
        String type = "Notebook";
        List<Equipment> equipmentList = getEquipment(brand, type);
        when(equipmentService.searchEquipmentsByQueries(null, type, brand, null))
                .thenReturn(equipmentList.stream().filter(equipment -> equipment.getBrand().equals(brand) && equipment.getType().equals(type)).toList());
        mockMvc.perform(get("/api/equipment/search")
                        .param("type", type).param("brand", brand)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].serialNumber").value("RRSDDAP"))
                .andExpect(jsonPath("$[1].serialNumber").value("KKSAPVC"));
    }

    private static List<Equipment> getEquipment(String brand, String type) {
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();
        Equipment equipment3 = new Equipment();
        equipment1.setSerialNumber("TFTRYAD");
        equipment2.setSerialNumber("RRSDDAP");
        equipment3.setSerialNumber("KKSAPVC");
        equipment1.setBrand("Dell");
        equipment2.setBrand(brand);
        equipment3.setBrand(brand);
        equipment1.setType("Workstation");
        equipment2.setType(type);
        equipment3.setType(type);
        return Arrays.asList(equipment1, equipment2, equipment3);
    }

    @Test
    @WithMockUser(username = "user")
    void searchEquipmentsByQueries_notFound() throws Exception {
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();
        equipment1.setBrand("Msi");
        equipment2.setBrand("Msi");
        equipment1.setType("Notebook");
        equipment2.setType("Notebook");
        equipment1.setModel("MSI CYBORG 15 A13VF-896XTR ");
        equipment2.setModel("MSI CYBORG 15 A13VF-9000TR ");
        equipment1.setSerialNumber("KFUUPRTF");
        equipment2.setSerialNumber("NN90AMDW");
        List<Equipment> equipmentList = Arrays.asList(equipment1, equipment2);
        when(equipmentService.searchEquipmentsByQueries(null, "Desktop", "Dell", null))
                .thenReturn(equipmentList.stream().filter(equipment -> equipment.getType().equals("Desktop")).toList());
        mockMvc.perform(get("/api/equipment/search")
                        .param("type", "Desktop"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void getEquipmentCount_found() throws Exception {
        String equipmentType = "Notebook";
        int equipmentCount = 1;
        String responseMessage = "Equipment with name " + equipmentType + " exists with equipment count : " + equipmentCount;
        when(equipmentService.getEquipmentCount(equipmentType)).thenReturn(equipmentCount);
        mockMvc.perform(get("/api/equipment/total_count/{equipment_type}", equipmentType)
                        .accept(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string(responseMessage));
    }

    @Test
    @WithMockUser(username = "user")
    void getEquipmentCount_notFound() throws Exception {
        String equipmentType = "Notebook";
        String responseMessage = "Equipment not exists in database";
        when(equipmentService.getEquipmentCount(equipmentType)).thenReturn(0);
        mockMvc.perform(get("/api/equipment/total_count/{equipment_type}", equipmentType)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string(responseMessage));
    }

    @Test
    @WithMockUser(username = "user")
    void getTotalEquipmentsCount_found() throws Exception {
        String responseMessage = "Equipments count are 28";
        when(equipmentService.getTotalEquipmentsCount()).thenReturn(28);
        mockMvc.perform(get("/api/equipment/total_all_equipments_count")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string(responseMessage));
    }

    @Test
    @WithMockUser(username = "user")
    void getTotalEquipmentsCount_notFound() throws Exception {
        String responseMessage = "Count is 0";
        when(equipmentService.getTotalEquipmentsCount()).thenReturn(0);
        mockMvc.perform(get("/api/equipment/total_all_equipments_count")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string(responseMessage));
    }

    @Test
    @WithMockUser(username = "user")
    void getMonthlyEquipmentTrends_found() throws Exception {
        String equipment_type = "Notebook";
        EquipmentMonthlyCountDto equipmentMonthlyCountDto1 = new EquipmentMonthlyCountDto();
        EquipmentMonthlyCountDto equipmentMonthlyCountDto2 = new EquipmentMonthlyCountDto();
        equipmentMonthlyCountDto1.setCreated_month("2024-July");
        equipmentMonthlyCountDto2.setCreated_month("2024-Sept");
        equipmentMonthlyCountDto1.setCount(12L);
        equipmentMonthlyCountDto2.setCount(6L);
        List<EquipmentMonthlyCountDto> equipmentMonthlyCountDtoList = Arrays.asList(equipmentMonthlyCountDto1, equipmentMonthlyCountDto2);
        when(equipmentService.getMonthlyEquipmentTrends(equipment_type)).thenReturn(Optional.of(equipmentMonthlyCountDtoList));
        mockMvc.perform(get("/api/equipment/monthly_addition_trends/{equipment_type}", equipment_type)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].created_month").value("2024-July"))
                .andExpect(jsonPath("$[1].created_month").value("2024-Sept"));
    }

    @Test
    @WithMockUser(username = "user")
    void getMonthlyEquipmentTrends_notFound() throws Exception {
        String equipment_type = "Notebook";
        when(equipmentService.getMonthlyEquipmentTrends(equipment_type)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/equipment/monthly_addition_trends/{equipment_type}", equipment_type))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    void getUsageStatus_isBeingUsed() throws Exception {
        String serial_number = "TFAFAVV";
        when(equipmentService.getUsageStatus(serial_number)).thenReturn("Equipment is being used");
        mockMvc.perform(get("/api/equipment/usage_status/{serial_number}", serial_number)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string("Equipment is being used"));
    }

    @Test
    @WithMockUser(username = "user")
    void getUsageStatus_notBeingUsed() throws Exception {
        String serial_number = "TFAFAVV";
        when(equipmentService.getUsageStatus(serial_number)).thenReturn("Equipment is not being used");
        mockMvc.perform(get("/api/equipment/usage_status/{serial_number}", serial_number)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNoContent())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string("Equipment is not being used"));
    }

    @Test
    @WithMockUser(username = "user")
    void getUsageStatus_equipmentNotFound() throws Exception {
        String serial_number = "TFAFAVV";
        String errorMessage = "Equipment not found with serial number: " + serial_number;
        when(equipmentService.getUsageStatus(serial_number)).thenThrow(new EntityNotFoundException(errorMessage));
        mockMvc.perform(get("/api/equipment/usage_status/{serial_number}", serial_number)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string(errorMessage));
    }

    @Test
    @WithMockUser(username = "user")
    void addEquipment_invalidJson() throws Exception {
        String invalidEquipmentDtoJson = "{ \"serialNumber\" : \"1452\", \"type\" : \"Notebook\", \"model\" : \"MSI CYBORG 15 A13VF-896XTR\", \"brand\" : \"Msi\"}";
        mockMvc.perform(post("/api/equipment/add_equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEquipmentDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string("Size must be between 6 and 14"));
    }

    @Test
    @WithMockUser(username = "user")
    void addEquipment_add() throws Exception {
    }

    @Test
    @WithMockUser(username = "user")
    void addEquipment_conflict() throws Exception {
    }


    @Test
    @WithMockUser(username = "user")
    void addEquipments_add() throws Exception {

    }

    @Test
    void updateEquipment_update() throws Exception {

    }

    @Test
    void deleteEquipment() throws Exception {

    }
}