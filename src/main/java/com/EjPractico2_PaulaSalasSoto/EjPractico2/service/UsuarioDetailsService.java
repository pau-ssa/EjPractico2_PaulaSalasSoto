package com.EjPractico2_PaulaSalasSoto.EjPractico2.service;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Usuario;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.UsuarioRepository;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {
        // Se busca el usuario de ese correo, que además debe estar activo
        Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        Set<SimpleGrantedAuthority> roles = Set.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getRol())
        );

        return new User(usuario.getCorreo(), usuario.getPassword(), roles);
    }
}
 