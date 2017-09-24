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
@RequestMapping("/secretaria")
public class SecretariaController {

    @InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;

    @Autowired
    public SecretariaController(UsuarioRepository usuarioRepository, ConsultaRepository consultaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.consultaRepository = consultaRepository;
    }

    @GetMapping
    public String secretaria() {
        return "secretaria/index";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("pacientes", usuarioRepository.findAllByRole(Role.ROLE_SECRETARIA));
        return "secretaria/paciente/list";
    }

    @GetMapping("/listConsulta")
    public String listarConsultas(Model model) {
        model.addAttribute("consultas", consultaRepository.findAll());
        return "secretaria/consulta/list";
    }

    @PostMapping("/horario")
    public @ResponseBody Iterable<String> horario(Date data, String medico, String tipo_consulta) {
        LocalTime agora = LocalTime.now();
        Horario horarios = new Horario(consultaRepository);
        List horas = horarios.horariosDisponiveis(data,tipo_consulta, medico, agora);
        return horas;
    }

    @GetMapping("/consulta")
    public String novaConsulta(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("medicos", usuarioRepository.findAllByRole(Role.ROLE_MEDICO));
        return "secretaria/consulta/cadastro";
    }

    @PostMapping("/consulta")
    public String saveConsulta(@Valid Consulta consulta, BindingResult br, RedirectAttributes ra) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernamePaciente = auth.getName();
        Optional<Usuario> usuariOpt = usuarioRepository.findByUsername(usernamePaciente);
        usuariOpt.ifPresent(usuario -> consulta.setPaciente(usuarioRepository.findOne(usuario.getId())));
        if(br.hasErrors()){
            return "secretaria/consulta/cadastro";
        }
        ra.addFlashAttribute("sucesso", "Consulta agendada com sucesso!");
        return "redirect:/secretaria";
    }
}
