package com.secomp.clinica.controller;

import com.secomp.clinica.model.Consulta;
import com.secomp.clinica.model.Prontuario;
import com.secomp.clinica.model.Usuario;
import com.secomp.clinica.model.enums.Role;
import com.secomp.clinica.repository.ConsultaRepository;
import com.secomp.clinica.repository.PacienteRepository;
import com.secomp.clinica.repository.UsuarioRepository;
import com.secomp.clinica.util.Horario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@PreAuthorize("hasAuthority('ROLE_MEDICO')")
@RequestMapping("/medico")
public class MedicoController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public MedicoController(UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public String medico() {
        return "medico/index";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "/paciente/list";
    }

    @GetMapping("/prontuario/{id}")
    public String prontuario(Model model, @PathVariable Integer id) {
        Prontuario prontuario = new Prontuario();
        prontuario.setPaciente(pacienteRepository.findOne(id));
        model.addAttribute("prontuario", prontuario);
        return "/paciente/prontuario";
    }

    @GetMapping("/{id}")
    public String update(Model model, @PathVariable Integer id) {
        model.addAttribute("paciente", pacienteRepository.findOne(id));
        model.addAttribute("visualizar", true);
        return "/paciente/cadastro";
    }

}
