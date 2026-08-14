package com.EjPractico2_PaulaSalasSoto.EjPractico2.controller;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.CitaMedica;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Especialidad;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Usuario;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.service.CitaMedicaService;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.service.UsuarioService;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
  
@Controller
@RequestMapping("/citasmedicas")
public class CitaMedicaController {

    private final CitaMedicaService citaMedicaService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // <-- agregar si falta
    private final MessageSource messageSource;

    public CitaMedicaController(CitaMedicaService citaMedicaService,
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository, // <-- agregar aquí
            MessageSource messageSource) {
        this.citaMedicaService = citaMedicaService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model, Authentication authentication) {
        boolean esPaciente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"));

        List<CitaMedica> citas;
        if (esPaciente) {
            Usuario paciente = usuarioService.buscarPorCorreo(authentication.getName());
            citas = citaMedicaService.getCitasMedicasLista(null).stream()
                    .filter(c -> c.getPaciente().getIdUsuario().equals(paciente.getIdUsuario()))
                    .collect(Collectors.toList());
        } else {
            citas = citaMedicaService.getCitasMedicasLista(null);
        }

        model.addAttribute("citas", citas);
        model.addAttribute("cita", new CitaMedica());
        model.addAttribute("especialidades", Especialidad.values());
        model.addAttribute("pacientes", usuarioService.getUsuarios(true));
        model.addAttribute("totalCitas", citas.size());
        return "citasmedicas/listado";
    }

    // El paciente autenticado agenda su propia cita (o ADMIN agenda para otro)
    @PostMapping("/registrar")
    public String registrarCita(@Valid CitaMedica citamedica,
            @RequestParam(required = false) Integer pacienteId,
            Authentication authentication, Model model){
        try {
            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            Usuario paciente;
            if (esAdmin && pacienteId != null) {
                paciente = usuarioService.getUsuario(pacienteId)
                        .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
            } else {
                paciente = usuarioService.buscarPorCorreo(authentication.getName());
            }
            citamedica.setPaciente(paciente);
            model = citaMedicaService.registrarCita(model, citamedica);
            return "citasmedicas/confirmacion";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cita", citamedica);
            return "citasmedicas/listado";
        }
    }

   // Guardar edición: ADMIN puede editar todos los campos, MEDICO solo cambia el estado
   // MEDICO/ADMIN: marcar cita como COMPLETADA  
    @PostMapping("/guardar")
    public String guardar(@Valid CitaMedica citamedica, RedirectAttributes redirectAttributes) {
        citaMedicaService.save(citamedica);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/citasmedicas/listado";
    }

    // ADMIN: eliminar una cita
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idCita, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            citaMedicaService.delete(idCita);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "citas.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "citas.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "citas.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/citasmedicas/listado";
    }

    // Formulario de edición: ADMIN/MEDICO
    @GetMapping("/editar/{idCita}")
    public String editar(@PathVariable("idCita") Long idCita, Model model, RedirectAttributes redirectAttributes) {
        Optional<CitaMedica> citaOpt = citaMedicaService.getCitaMedica(idCita);
        if (citaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("citas.error01", null, Locale.getDefault()));
            return "redirect:/citasmedicas/listado";
        }
        model.addAttribute("cita", citaOpt.get());
        model.addAttribute("pacientes", usuarioRepository.findByRol_Rol("PACIENTE"));
        return "citasmedicas/modifica";
    }

}
