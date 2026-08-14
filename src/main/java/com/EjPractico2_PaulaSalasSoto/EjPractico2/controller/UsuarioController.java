package com.EjPractico2_PaulaSalasSoto.EjPractico2.controller;

import com.EjPractico2_PaulaSalasSoto.EjPractico2.domain.Usuario;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.repository.RolRepository;
import com.EjPractico2_PaulaSalasSoto.EjPractico2.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioService usuarioService, RolRepository rolRepository, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios(false);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuario", new Usuario());
        return "usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario,
                           @RequestParam Integer rolId,
                           RedirectAttributes redirectAttributes) {
        try {
            usuarioService.save(usuario, rolId, true);
            redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("usuario.error04", null, Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            usuarioService.delete(idUsuario);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // Captura la excepción de argumento inválido para el mensaje de "no existe"
            detalle = "usuario.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // Captura la excepción de estado ilegal para el mensaje de "datos asociados"
            detalle = "usuario.error02";
        } catch (Exception e) {
            titulo = "error"; // Captura cualquier otra excepción inesperada
            detalle = "usuario.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable("idUsuario") Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        model.addAttribute("roles", rolRepository.findAll());
        return "usuario/modifica";
    }
}