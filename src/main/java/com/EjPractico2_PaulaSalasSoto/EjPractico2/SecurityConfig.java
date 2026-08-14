package com.EjPractico2_PaulaSalasSoto.EjPractico2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/error").permitAll()

                .requestMatchers("/usuario/**").hasRole("ADMIN")
                .requestMatchers("/role/**").hasRole("ADMIN")
                .requestMatchers("/usuario_rol/**").hasRole("ADMIN")

                .requestMatchers("/citasmedicas/completar").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/citasmedicas/cancelar").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/citasmedicas/guardar").hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/consultas/**").hasAnyRole("ADMIN", "MEDICO")

                .requestMatchers("/citasmedicas/agregar").hasAnyRole("ADMIN", "PACIENTE")
                .requestMatchers("/citasmedicas/registrar").hasAnyRole("ADMIN", "PACIENTE")

                .requestMatchers("/citasmedicas/listado/**").hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                .requestMatchers("/citasmedicas/modificar/**").hasAnyRole("ADMIN", "MEDICO", "PACIENTE")

                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .defaultSuccessUrl("/citasmedicas/listado", true)
                .failureUrl("/login?error=true")
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/acceso_denegado")
        ).sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }
//lo hice asi porque sino el login fallaba por la encriptacion
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}


