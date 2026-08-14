package com.EjPractico2_PaulaSalasSoto.EjPractico2.repository;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoAndPassword(String correo, String password);

    Optional<Usuario> findByCorreoAndActivoTrue(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByActivoTrue();
    
    List<Usuario> findByRol_Rol(String rol);

}
  