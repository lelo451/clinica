package com.secomp.clinica.controller;

import com.secomp.clinica.model.Consulta;
import com.secomp.clinica.model.Paciente;
import com.secomp.clinica.model.Usuario;
import com.secomp.clinica.model.enums.Role;
import com.secomp.clinica.repository.ConsultaRepository;
import com.secomp.clinica.repository.PacienteRepository;
import com.secomp.clinica.repository.UsuarioRepository;
import com.secomp.clinica.util.Horario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('ROLE_SECRETARIA')")
@RequestMapping("/secretaria")
public class SecretariaController {

    @InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SecretariaController(PacienteRepository pacienteRepository, ConsultaRepository consultaRepository, UsuarioRepository usuarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String secretaria() {
        return "secretaria/index";
    }

    @GetMapping("/novo")
    public String cadastro(Model model) {
        model.addAttribute("update", false);
        model.addAttribute("visualizar", false);
        model.addAttribute("paciente", new Paciente());
        return "/paciente/cadastro";
    }

    @PostMapping
    public String salvar(@Valid Paciente paciente, BindingResult br, RedirectAttributes ra) {
        if(br.hasErrors()) {
            return "/paciente/cadastro";
        }
        if(paciente.getCpf() == null) {
            br.rejectValue("cpf", null, "O CPF informado já está cadastrado");
            return "/paciente/cadastro";
        } else {
            try {
                pacienteRepository.save(paciente);
            } catch (DataIntegrityViolationException e) {
                br.rejectValue("cpf", null, "O CPF informado já está cadastrado!");
                paciente.setCpf(null);
                return "/paciente/cadastro";
            }
            ra.addFlashAttribute("sucesso", "Paciente cadastrado com sucesso!");
            return "redirect:/secretaria";
        }
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "/paciente/list";
    }

    @GetMapping("/listConsulta")
    public String listarConsultas(Model model) {
        model.addAttribute("consultas", consultaRepository.findAll());
        return "secretaria/consulta/list";
    }

    @GetMapping("/{id}")
    public String update(Model model, @PathVariable Integer id) {
        model.addAttribute("paciente", pacienteRepository.findOne(id));
        model.addAttribute("visualizar", false);
        model.addAttribute("update", true);
        return "/paciente/cadastro";
    }

    @PostMapping("/{id}")
    public String updatePaciente(Paciente paciente, RedirectAttributes ra) {
        pacienteRepository.save(paciente);
        ra.addFlashAttribute("sucesso", "Paciente " + paciente.getNome() + " atualizado com sucesso!");
        return "redirect:/list";
    }

    @PostMapping("/horario")
    public @ResponseBody Iterable<String> horario(Date data, String medico, String tipo_consulta) {
        LocalTime agora = LocalTime.now();
        Horario horarios = new Horario(consultaRepository);
        List horas = horarios.horariosDisponiveis(data,tipo_consulta, medico, agora);
        return horas;
    }

    @GetMapping("/consulta/{id}")
    public String novaConsulta(Model model, @PathVariable Integer id) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("medicos", usuarioRepository.findAllByRole(Role.ROLE_MEDICO));
        model.addAttribute("pacienteID", id);
        return "secretaria/consulta/cadastro";
    }

    @PostMapping("/consulta")
    public String saveConsulta(Consulta consulta, RedirectAttributes ra, Integer pacienteID) {
        Paciente paciente = pacienteRepository.findOne(pacienteID);
        consulta.setPaciente(paciente);
        paciente.setConsulta(consulta);
        pacienteRepository.save(paciente);
        ra.addFlashAttribute("sucesso", "Consulta agendada com sucesso!");
        return "redirect:/secretaria";
    }
}