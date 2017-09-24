package com.secomp.clinica.repository;

import com.secomp.clinica.model.Usuario;
import com.secomp.clinica.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findAllByRole(Role role);
}