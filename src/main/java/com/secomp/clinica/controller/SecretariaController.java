package com.secomp.clinica.controller;

import com.secomp.clinica.model.Paciente;
import com.secomp.clinica.repository.PacienteRepository;
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
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Autowired
    public SecretariaController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public String secretaria() {
        return "secretaria/index";
    }

    @GetMapping("/novo")
    public String cadastro(Model model) {
        model.addAttribute("update", false);
        model.addAttribute("paciente", new Paciente());
        return "secretaria/cadastro";
    }

    @PostMapping
    public String salvar(@Valid Paciente paciente, BindingResult br, RedirectAttributes ra) {
        if(br.hasErrors()) {
            return "secretaria/cadastro";
        }
        if(paciente.getCpf() == null) {
            br.rejectValue("cpf", null, "O CPF informado já está cadastrado");
            return "secretaria/cadastro";
        } else {
            try {
                pacienteRepository.save(paciente);
            } catch (DataIntegrityViolationException e) {
                br.rejectValue("cpf", null, "O CPF informado já está cadastrado!");
                paciente.setCpf(null);
                return "secretaria/cadastro";
            }
            ra.addFlashAttribute("sucesso", "Paciente cadastrado com sucesso!");
            return "redirect:/secretaria";
        }
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "secretaria/list";
    }

    @GetMapping("/{id}")
    public String update(Model model, @PathVariable Integer id) {
        model.addAttribute("paciente", pacienteRepository.findOne(id));
        model.addAttribute("update", true);
        return "secretaria/cadastro";
    }

    @PostMapping("/{id}")
    public String updatePaciente(Paciente paciente, RedirectAttributes ra) {
        pacienteRepository.save(paciente);
        ra.addFlashAttribute("sucesso", "Paciente " + paciente.getNome() + " atualizado com sucesso!");
        return "redirect:/list";
    }
}
