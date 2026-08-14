package com.EjPractico2_PaulaSalasSoto.EjPractico2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idUsuario;
    
    @Column(length = 150)
    @NotBlank
    private String nombre;
    
    @Column(name = "email", unique = true, length = 200)
    @Email
    private String correo;
    
    @Column(length = 255)    
    private String password;

    private boolean activo;
      
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id") // Nombre de la columna FK en la tabla usuario
    private Rol rol;
    
    @ToString.Exclude
    @OneToMany(mappedBy = "paciente")
    private List<CitaMedica> citasmedicas;
    
}
