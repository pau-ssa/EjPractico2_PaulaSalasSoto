package com.EjPractico2_PaulaSalasSoto.EjPractico2.repository;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

     Optional<Rol> findByRol(String rol);

} 
  
