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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
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
        Prontuario prontuario = pacienteRepository.findOne(id).getProntuario();
        model.addAttribute("prontuario", prontuario);
        model.addAttribute("update", prontuario!=null);
       return "/paciente/prontuario";
    }

    @GetMapping("/{id}")
    public String update(Model model, @PathVariable Integer id) {
        model.addAttribute("paciente", pacienteRepository.findOne(id));
        model.addAttribute("visualizar", true);
        return "/paciente/cadastro";
    }

}
