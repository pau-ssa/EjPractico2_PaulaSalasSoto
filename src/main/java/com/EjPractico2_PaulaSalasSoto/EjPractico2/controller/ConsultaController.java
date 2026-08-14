package com.EjPractico2_PaulaSalasSoto.EjPractico2.controller;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.CitaMedica;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Especialidad;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.EstadoCita;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.UsuarioRepository;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.service.CitaMedicaService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final CitaMedicaService citaMedicaService;
    private final UsuarioRepository usuarioRepository;

    public ConsultaController(CitaMedicaService citaMedicaService, UsuarioRepository usuarioRepository) {
        this.citaMedicaService = citaMedicaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("pacientes", usuarioRepository.findByRol_Rol("PACIENTE"));
        return "consultas/listado";
    }

    // Requerimiento D.1 - Buscar citas por paciente y estado
    @PostMapping("/citasPorPacienteEstado")
    public String citasPorPacienteEstado(@RequestParam Integer pacienteId,
                                          @RequestParam EstadoCita estado,
                                          Model model) {
        List<CitaMedica> citasPorPacienteEstado = citaMedicaService.listarPorPacienteYEstado(pacienteId, estado);

        model.addAttribute("citasPorPacienteEstado", citasPorPacienteEstado);
        model.addAttribute("pacientes", usuarioRepository.findByRol_Rol("PACIENTE"));
        model.addAttribute("pacienteId", pacienteId);
        model.addAttribute("estado", estado);
        return "consultas/listado";
    }

    // Requerimiento D.2 - Contar citas por especialidad en un rango de fechas
    @PostMapping("/citasPorEspecialidadRango")
    public String citasPorEspecialidadRango(@RequestParam Especialidad especialidad,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
                                             Model model) {
        Long totalCitasEspecialidad = citaMedicaService.contarPorEspecialidadYRango(especialidad, inicio, fin);

        model.addAttribute("totalCitasEspecialidad", totalCitasEspecialidad);
        model.addAttribute("especialidadConsultada", especialidad);
        model.addAttribute("inicioConsultado", inicio);
        model.addAttribute("finConsultado", fin);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);
        model.addAttribute("pacientes", usuarioRepository.findByRol_Rol("PACIENTE"));
        return "consultas/listado";
    }
}