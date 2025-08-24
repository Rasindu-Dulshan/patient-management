package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repositary.PatientRepositary;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
  private final PatientRepositary patientRepositary;
  private final BillingServiceGrpcClient billingServiceGrpcClient;

  public PatientService(
      PatientRepositary patientRepositary, BillingServiceGrpcClient billingServiceGrpcClient) {
    this.patientRepositary = patientRepositary;
    this.billingServiceGrpcClient = billingServiceGrpcClient;
  }

  public List<PatientResponseDto> getPatients() {
    List<Patient> patients = patientRepositary.findAll();
    return patients.stream().map(PatientMapper::toDto).toList();
  }

  public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
    if (patientRepositary.existsByEmail(patientRequestDto.getEmail())) {
      throw new EmailAlreadyExistsException(
          "Patient with email " + patientRequestDto.getEmail() + " already exists");
    }

    Patient newPatient = patientRepositary.save(PatientMapper.toModel(patientRequestDto));
    billingServiceGrpcClient.createBillingAccount(
        newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());
    return PatientMapper.toDto(newPatient);
  }

  public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
    Patient patient =
        patientRepositary
            .findById(id)
            .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
    if (patientRepositary.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)) {
      throw new EmailAlreadyExistsException(
          "Patient with email " + patientRequestDto.getEmail() + " already exists");
    }

    patient.setName(patientRequestDto.getName());
    patient.setEmail(patientRequestDto.getEmail());
    patient.setAddress(patientRequestDto.getAddress());
    patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

    Patient updatedPatient = patientRepositary.save(patient);
    return PatientMapper.toDto(updatedPatient);
  }

  public void deletePatient(UUID id) {
    patientRepositary.deleteById(id);
  }
}
