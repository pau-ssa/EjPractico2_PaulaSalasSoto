package com.EjPractico2_PaulaSalasSoto.EjPractico2.service;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.CitaMedica;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Especialidad;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.EstadoCita;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.CitaMedicaRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.ui.Model;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CitaMedicaService {
    

    // Las dependencias son final para asegurar la inmutabilidad
    private final CitaMedicaRepository citamedicaRepository;
    private final CorreoService correoService;
    private final MessageSource messageSource;

    // Ojo cómo se lee una información del application.properties
    @Value("${servidor.http}")
    private String servidor;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CitaMedicaService(CitaMedicaRepository citamedicaRepository,
                              CorreoService correoService,
                              MessageSource messageSource) {
        this.citamedicaRepository = citamedicaRepository;
        this.correoService = correoService;
        this.messageSource = messageSource;
    }

    @Transactional(readOnly = true)
    public Optional<CitaMedica> getCitaMedica(Long id) {
        return citamedicaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CitaMedica> getCitasMedicasLista(Long id) {
        if (id == null) {
            return citamedicaRepository.findAll();
        }
        List<CitaMedica> lista = new ArrayList<>();
        citamedicaRepository.findById(id).ifPresent(lista::add);
        return lista;
    }

    @Transactional(readOnly = true)
    public List<CitaMedica> listarPorPacienteYEstado(Integer pacienteId, EstadoCita estado) {
        return citamedicaRepository.buscarPorPacienteYEstado(pacienteId, estado);
    }

    @Transactional(readOnly = true)
    public Long contarPorEspecialidadYRango(Especialidad especialidad, LocalDateTime inicio, LocalDateTime fin) {
        return citamedicaRepository.contarPorEspecialidadYRango(especialidad, inicio, fin);
    }

    @Transactional
    public void save(CitaMedica citamedica) {
        citamedicaRepository.save(citamedica);
    }

    @Transactional
    public Model registrarCita(Model model, CitaMedica citamedica) {

        if (citamedica.getFechaHora() == null || !citamedica.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new DataIntegrityViolationException("La fecha y hora de la cita debe ser futura.");
        }
        final Integer idPaciente = citamedica.getPaciente().getIdUsuario();
        Optional<CitaMedica> citaDuplicada = citamedicaRepository
                .findByPaciente_IdUsuarioAndFechaHora(idPaciente, citamedica.getFechaHora());

        if (citaDuplicada.isPresent()) {
            Long idCitaActual = citamedica.getIdCita();
            if (idCitaActual == null || !citaDuplicada.get().getIdCita().equals(idCitaActual)) {
                throw new DataIntegrityViolationException("El paciente ya tiene una cita agendada en esa fecha y hora.");
            }
        }

        if (citamedica.getEstado() == null) {
            citamedica.setEstado(EstadoCita.PROGRAMADA);
        }
        citamedicaRepository.save(citamedica);
        

        String mensaje;
        try {
            enviaCorreoCita(citamedica);
            mensaje = String.format(
                    messageSource.getMessage("registro.mensaje.cita.ok", null, Locale.getDefault()),
                    citamedica.getPaciente().getCorreo());
        } catch (MessagingException | NoSuchMessageException e) {
            mensaje = "La cita fue registrada, pero no fue posible enviar el correo de confirmación.";
        }

        model.addAttribute("titulo", messageSource.getMessage("registro.mensaje.cita", null, Locale.getDefault()));
        model.addAttribute("mensaje", mensaje);
        return model;
    }

    private void enviaCorreoCita(CitaMedica citamedica) throws MessagingException {
        String mensaje = messageSource.getMessage("registro.correo.cita", null, Locale.getDefault());
        mensaje = String.format(mensaje,
                citamedica.getPaciente().getNombre(),
                citamedica.getEspecialidad(),
                citamedica.getFechaHora().format(FORMATO_FECHA),
                citamedica.getCosto(),
                servidor);

        String asunto = messageSource.getMessage("registro.mensaje.cita", null, Locale.getDefault());
        correoService.enviarCorreoHtml(citamedica.getPaciente().getCorreo(), asunto, mensaje);
    }

    @Transactional
    public void delete(Long id) {
        if (!citamedicaRepository.existsById(id)) {
            throw new IllegalArgumentException("La cita con ID " + id + " no existe.");
        }
        try {
            citamedicaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la cita. Tiene datos asociados.", e);
        }
    }

}
