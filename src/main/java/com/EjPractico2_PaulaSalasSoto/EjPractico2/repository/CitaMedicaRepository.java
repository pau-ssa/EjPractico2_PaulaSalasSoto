package com.EjPractico2_PaulaSalasSoto.EjPractico2.repository;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.CitaMedica;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Especialidad;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.EstadoCita;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long>{
    
    Optional<CitaMedica> findByPaciente_IdUsuarioAndFechaHora(Integer idUsuario, LocalDateTime fechaHora);
    
    @Query("SELECT c FROM CitaMedica c WHERE c.paciente.idUsuario = :pacienteId AND c.estado = :estado")
    List<CitaMedica> buscarPorPacienteYEstado(@Param("pacienteId") Integer pacienteId,
                                               @Param("estado") EstadoCita estado);

    @Query("SELECT COUNT(c) FROM CitaMedica c WHERE c.especialidad = :especialidad AND c.fechaHora BETWEEN :inicio AND :fin")
    Long contarPorEspecialidadYRango(@Param("especialidad") Especialidad especialidad,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fin") LocalDateTime fin);

    List<CitaMedica> findByPacienteIdUsuarioOrderByFechaHoraDesc(Integer pacienteId);

    List<CitaMedica> findByEstadoOrderByFechaHoraAsc(EstadoCita estado);
    
}    