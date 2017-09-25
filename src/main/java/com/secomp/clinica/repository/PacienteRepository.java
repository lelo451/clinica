package com.secomp.clinica.repository;

import com.secomp.clinica.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Paciente findByCpf(String cpf);

}