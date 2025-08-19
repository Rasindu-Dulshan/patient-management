package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repositary.PatientRepositary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private PatientRepositary patientRepositary;

    public PatientService(PatientRepositary patientRepositary) {
        this.patientRepositary = patientRepositary;
    }

    public List<PatientResponseDto> getPatients() {
        List<Patient> patients = patientRepositary.findAll();
        return patients.stream().map(PatientMapper::toDto).toList();
    }
}
