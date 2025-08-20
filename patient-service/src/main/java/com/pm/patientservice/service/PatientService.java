package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repositary.PatientRepositary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepositary patientRepositary;

    public PatientService(PatientRepositary patientRepositary) {
        this.patientRepositary = patientRepositary;
    }

    public List<PatientResponseDto> getPatients() {
        List<Patient> patients = patientRepositary.findAll();
        return patients.stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if (patientRepositary.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDto.getEmail() + " already exists");
        }

        Patient newPatient = patientRepositary.save(PatientMapper.toModel(patientRequestDto));
        return PatientMapper.toDto(newPatient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
        Patient patient = patientRepositary.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        if (patientRepositary.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDto.getEmail() + " already exists");
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
