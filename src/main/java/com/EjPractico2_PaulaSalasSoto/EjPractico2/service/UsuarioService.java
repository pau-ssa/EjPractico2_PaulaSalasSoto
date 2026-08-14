package com.EjPractico2_PaulaSalasSoto.EjPractico2.service;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Rol;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Usuario;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.RolRepository;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + correo));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorCorreoYPassword(String correo, String password) {
        return usuarioRepository.findByCorreoAndPassword(correo, password);
    }

    @Transactional(readOnly = true)
    public boolean existeUsuarioPorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Transactional
    public void save(Usuario usuario, Integer rolId, boolean encriptaClave) {
        final Integer idUser = usuario.getIdUsuario();
        Optional<Usuario> usuarioDuplicado = usuarioRepository.findByCorreo(usuario.getCorreo());
        if (usuarioDuplicado.isPresent()) {
            Usuario encontrado = usuarioDuplicado.get();
            if (idUser == null || !encontrado.getIdUsuario().equals(idUser)) {
                throw new DataIntegrityViolationException("El correo ya está en uso por otro usuario.");
            }
        }

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));
        usuario.setRol(rol);

        if (usuario.getIdUsuario() == null) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios.");
            }
            usuario.setPassword(encriptaClave ? passwordEncoder.encode(usuario.getPassword()) : usuario.getPassword());
            usuario.setActivo(true);
        } else {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario())
                        .orElseThrow(() -> new IllegalArgumentException("Usuario a modificar no encontrado."));
                usuario.setPassword(usuarioExistente.getPassword());
            } else {
                usuario.setPassword(encriptaClave ? passwordEncoder.encode(usuario.getPassword()) : usuario.getPassword());
            }
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene citas asociadas.", e);
        }
    }
}
 
  