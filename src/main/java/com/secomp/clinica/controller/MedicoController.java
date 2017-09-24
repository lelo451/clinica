package com.secomp.clinica.controller;

import com.secomp.clinica.model.Consulta;
import com.secomp.clinica.model.Paciente;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
        Paciente paciente = pacienteRepository.findOne(id);
        Prontuario prontuario = paciente.getProntuario();
        if (prontuario == null) {
            prontuario = new Prontuario(paciente);
        }

        model.addAttribute("prontuario", prontuario);
        model.addAttribute("update", prontuario.getMedico() != null);
        return "/paciente/prontuario";
    }

    @GetMapping("/{id}")
    public String update(Model model, @PathVariable Integer id) {
        model.addAttribute("paciente", pacienteRepository.findOne(id));
        model.addAttribute("visualizar", true);
        return "/paciente/cadastro";
    }

    @PostMapping("/prontuario")
    public String saveProntuario(@Valid Prontuario prontuario, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "/paciente/prontuario";
        }
        Paciente paciente =  pacienteRepository.getOne(prontuario.getPaciente().getId());
        prontuario.setMedico(SecurityContextHolder.getContext().getAuthentication().getName());
        paciente.setProntuario(prontuario);
        pacienteRepository.save(paciente);
        ra.addFlashAttribute("sucesso", "Prontuário cadastrado com sucesso!");
        return "redirect:/medico/list";
    }

}
