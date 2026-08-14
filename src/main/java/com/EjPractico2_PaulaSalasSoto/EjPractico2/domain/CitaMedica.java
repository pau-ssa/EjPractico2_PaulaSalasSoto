
package com.EjPractico2_PaulaSalasSoto.EjPractico2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;
 
@Data
@Entity
@Table(name = "cita_medica")
public class CitaMedica implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idCita;

    // Relación de muchos a uno con la clase usuario(paciente)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;
 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Especialidad especialidad;
    
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(precision = 12, scale = 2)
    @NotNull(message = "El costo no puede estar vacío.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El costo debe ser mayor a 0.")
    private BigDecimal costo;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCita estado;

}

