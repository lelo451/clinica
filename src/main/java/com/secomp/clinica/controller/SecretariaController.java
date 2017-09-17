package com.secomp.clinica.controller;

import com.secomp.clinica.model.Consulta;
import com.secomp.clinica.model.Paciente;
import com.secomp.clinica.repository.ConsultaRepository;
import com.secomp.clinica.repository.PacienteRepository;
import com.secomp.clinica.util.Horario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
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

    @Autowired
    public SecretariaController(PacienteRepository pacienteRepository, ConsultaRepository consultaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
    }

    @GetMapping
    public String secretaria() {
        return "secretaria/index";
    }

    @GetMapping("/novo")
    public String cadastro(Model model) {
        model.addAttribute("update", false);
        model.addAttribute("paciente", new Paciente());
        return "secretaria/paciente/cadastro";
    }

    @PostMapping
    public String salvar(@Valid Paciente paciente, BindingResult br, RedirectAttributes ra) {
        if(br.hasErrors()) {
            return "secretaria/paciente/cadastro";
        }
        if(paciente.getCpf() == null) {
            br.rejectValue("cpf", null, "O CPF informado já está cadastrado");
            return "secretaria/paciente/cadastro";
        } else {
            try {
                pacienteRepository.save(paciente);
            } catch (DataIntegrityViolationException e) {
                br.rejectValue("cpf", null, "O CPF informado já está cadastrado!");
                paciente.setCpf(null);
                return "secretaria/paciente/cadastro";
            }
            ra.addFlashAttribute("sucesso", "Paciente cadastrado com sucesso!");
            return "redirect:/secretaria";
        }
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "secretaria/paciente/list";
    }

    @GetMapping("/{id}")
    public String update(Model model, @PathVariable Integer id) {
        model.addAttribute("paciente", pacienteRepository.findOne(id));
        model.addAttribute("update", true);
        return "secretaria/paciente/cadastro";
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

    @GetMapping("/consulta")
    public String novaConsulta(Model model) {
        model.addAttribute("consulta", new Consulta());
        return "secretaria/consulta/cadastro";
    }

    @PostMapping("/consulta")
    public String saveConsulta(@Valid Consulta consulta, BindingResult br, RedirectAttributes ra, String cpf) {
        System.out.println(cpf);
        if(br.hasErrors()){
            return "secretaria/consulta/cadastro";
        }
        Paciente paciente = pacienteRepository.findByCpf(cpf);
        if(paciente != null) {
            consulta.setPaciente(paciente);
            consultaRepository.save(consulta);
        } else {
            ra.addFlashAttribute("erro", "O paciente deve estar cadastrado no sistema!");
            return "secretaria/consulta/cadastro";
        }
        ra.addFlashAttribute("sucesso", "Consulta agendada com sucesso!");
        return "redirect:/secretaria";
    }
}
