package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "Patient API")
public class PatientController {
  private final PatientService patientService;

  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  @GetMapping
  @Operation(summary = "Get all patients")
  public ResponseEntity<List<PatientResponseDto>> getPatients() {
    List<PatientResponseDto> patients = patientService.getPatients();
    return ResponseEntity.ok(patients);
  }

  @PostMapping
  @Operation(summary = "Create a new patient")
  public ResponseEntity<PatientResponseDto> createPatient(
      @Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody
          PatientRequestDto patientRequestDto) {
    PatientResponseDto patientResponseDto = patientService.createPatient(patientRequestDto);
    return ResponseEntity.ok().body(patientResponseDto);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a patient")
  public ResponseEntity<PatientResponseDto> updatePatient(
      @PathVariable UUID id,
      @Validated({Default.class}) @RequestBody PatientRequestDto patientRequestDto) {
    PatientResponseDto patientResponseDto = patientService.updatePatient(id, patientRequestDto);
    return ResponseEntity.ok().body(patientResponseDto);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a patient")
  public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
    patientService.deletePatient(id);
    return ResponseEntity.noContent().build();
  }
}
