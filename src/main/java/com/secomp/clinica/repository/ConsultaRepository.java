package com.secomp.clinica.repository;

import com.secomp.clinica.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {

    @Query("select a from Consulta a where a.dataConsulta = ?1")
    List<Consulta> findAllByDataConsulta(Date data);

}
